//Crafty.init(500,350, document.getElementById('game'));
//Crafty.e('2D, DOM, Color, Fourway, Gravity').attr({x: 0, y: 0, w: 100, h: 100}).color('#F00').fourway(8).gravity('Floor');
//
//Crafty.e('Floor, 2D, Canvas, Color').attr({
//    x: 0,
//    y: 250,
//    w: 250,
//    h: 10
//}).color('green');

//
//Crafty("Keyboard").bind("KeyDown", function(){
//    Crafty("*").trigger("Explode");
//});
function log(value) {
	console.log(value);
}

// Players:
var YELLOW = 0,
    RED = 1,
    COLUMNS = 7,
    ROWS = 6;

var gameActive = false,
	firstRun = true,
	yellowPlayerType,
	redPlayerType,
	scoreboardYellow = document.getElementById('yellowScore'),
	scoreboardRed = document.getElementById('redScore'),
	scoreboardDraw = document.getElementById('drawScore'),
	gameWinnerStatus = document.getElementById('gameWinnerStatus'),
	autoPlay;


function toggleStartInstructions(show) {
	document.getElementById('curtain').style.display = show ? "block" : "none";
}

function startNewGame(e) {
	Crafty.lastEvent = e;
	toggleStartInstructions(false);
	gameWinnerStatus.innerHTML = "";
	
	function newGameResponse() {
		gameActive = true;
		
		/**
		 * Ako pocetni igrac nije covjek, prepustiti inicijalni potez racunaru 
		 */
		if(yellowPlayerType != 0) {
			createAjaxRequest("POST", "/ConnectFour/req", ConnectFour.moveAI, JSON.stringify({
        		type: 1,
            	data: {
            		column: -1,
            		row: -1,
            		player: YELLOW
            	}
        	}));
		}
	}
	
	yellowPlayerType = document.getElementById('yellowMenu').selectedIndex;
	redPlayerType = document.getElementById('redMenu').selectedIndex;
	autoPlay = document.getElementById('autoPlay').checked;
	
	createAjaxRequest("POST", "/ConnectFour/req", newGameResponse, JSON.stringify({
 		type: 0,
     	data: {
     		columns: COLUMNS,
     		rows: ROWS,
     		startingPlayer: YELLOW,
     		yellow: yellowPlayerType,
     		red: redPlayerType,
     		yellowDepth: parseInt(document.getElementById('cutoffYellow').value),
     		redDepth: parseInt(document.getElementById('cutoffRed').value),
     		autoPlay: autoPlay
     	}
 	}));
	
	if(firstRun) {
		firstRun = false;
		scoreboardRed.innerHTML = 0;
		scoreboardYellow.innerHTML = 0;
		scoreboardDraw.innerHTML = 0;
	} else {
		ConnectFour.init();
	}	
}

function createAjaxRequest(method, URL, callback, data) {
	var httpRequest = new XMLHttpRequest();

	httpRequest.onreadystatechange = function() {
		if(httpRequest.readyState == XMLHttpRequest.DONE && httpRequest.status == 200) {
			callback(httpRequest.responseText);
		}
	};

	httpRequest.open(method, URL, true);
	httpRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	httpRequest.send(data ? "data=" + data : null);
}

var ConnectFour = (function(){
	var turn, //turn based
	    board,
	    COLUMN_FULL = -2,
	    EMPTY = -1,
	    YELLOW = 0,
	    RED = 1;
	
	var vsAI = true,
		current;
	
	var init = function() {
		turn = 0;
		board = [];
		
		Crafty.init(600,500, document.getElementById('game'));
	    //Crafty.canvas();

	    Crafty.sprite(64, "resources/img/sprite.png", {
	        red: [0,0],
	        yellow: [1,0],
	        empty: [2,0]
	    });
	    
	    Crafty.sprite(64, "resources/img/winnerCoins.png", {
	        winnerRed: [0,0],
	        winnerYellow: [1,0]
	    });
	    
	    Crafty.scene("game", function() {
	        //generate board
	        for(var i = 0; i < 7; i++) {
	            board[i] = []; //init board
	            for(var j = 0; j < 6; j++) {
	                Crafty.e("2D, Canvas, empty").attr({x: i * 64, y: j * 64 + 100, z: 2});
	                board[i][j] = EMPTY; //set it to empty
	            }
	        }

	        Crafty.c("piece", {
	            init: function() {
	                this.z = 3;
	                this.requires("Mouse, Gravity, Draggable");
	                this.bind("StopDrag", function() {
	                    var column = Math.round(this._x / 64);
	                    this.x = column * 64;
	                    this.gravity("stopper");
	                    this.unbind("mousedown");
	                    
	                    //TODO Dodati callback kada se "slegne" disk a ne preko timeouta
	                    if(gameActive) {
	                    	setTimeout(function() {
		                    	handleTurn(column);
		                    }, 1500)
	                    } else {
	                    	handleTurn(column);
	                    }
	                });
	            }
	        });

	        function handleTurn(column) {
	            var row = findEmptyRow(column);
	            if(row !== COLUMN_FULL && column >= 0 && column < 7) {
	            	board[column][row] = turn;
	                
                	log((gameActive ? "" : "GAME OVER - ") + (turn ? "RED" : "YELLOW") + " (" + column + ", " + row + ")");
	                /*if(checkFour(column,row)) {
	                    win(turn);
	                    return;
	                }*/
                	if(gameActive && (turn == 0 &&  redPlayerType != 0) || (turn == 1 && yellowPlayerType != 0)) {
	                	makeAIMove(column, row, turn);
	                }

	                turn ^= 1; //alternate turns
	                current = Crafty.e("2D, Canvas, piece, stopper," + (turn ? "red" : "yellow")).attr({x: 495, y: 420});
	            } else {
	                //dont' place
	                current.destroy();
	                current = Crafty.e("2D, Canvas, piece, stopper," + (turn ? "red" : "yellow")).attr({x: 495, y: 420});
	            }
	        }
	        
	        current = Crafty.e("2D, Canvas, piece, stopper, yellow").attr({x: 495, y: 420});

	        var ground = Crafty.e("2D, stopper").attr({y: Crafty.viewport.height - 16, w: Crafty.viewport.width, h: 20 });
	        var bg = Crafty.e("2D, Canvas, Image").image("resources/img/bg.png").attr({z: -1});
	    });
	    
	    function makeAIMove(column, row, player) {
	    	createAjaxRequest("POST", "/ConnectFour/req", moveAI, JSON.stringify({
        		type: 1,
            	data: {
            		column: column,
            		row: row,
            		player: player
            	}
        	}));
	    }
	    
//	    Crafty.scene("win", function() {
//	        var bg = Crafty.e("2D, DOM, Image").image("resources/img/win.png", "no-repeat").attr({w: 600, h: 500, z: -1});
//	        Crafty.e("2D, DOM, Text").attr({x: 220, y: 200}).text(turn ? "RED" : "YELLOW").font("30pt Arial");
//	    });
	    
	    // start the game
	    Crafty.scene("game");
	    document.getElementById("btnStartGame").addEventListener("click", startNewGame);
	}
	
	function highlightWinningSequence(player, winnerSequence) {
        var setWinnerCoin = function(playerWinner, posX, posY) {
	    	Crafty.e("2D, Canvas, winner" + playerWinner).attr({x: posX, y: posY + 100, z: 4});
	    } 
		for(var i = 0; i < winnerSequence.length; i++) {
			var winnerMove = winnerSequence[i];
			setWinnerCoin(player, (winnerMove.col) * 64, (ROWS - winnerMove.row - 1) * 64);
//			Crafty.e("2D, Canvas, " + player ? "winnerRed" : "winnerYellow").attr({x: winnerMove.row * 64, y: winnerMove.col * 64 + 100, z: 2});
		}
		
//		Crafty.e("2D, Canvas, piece, stopper," + (turn ? "winnerRed" : "winnerYellow")).attr({x: 495, y: 420});
	}
	
	//TODO preko crafty-evog callback ovo tek pozvati, nakon sto se coin smiri na poziciji
	function win(player, winnerSequence) {
		setTimeout(function() {
			var winningPlayer = (player ? "Red" : "Yellow");
			gameWinnerStatus.innerHTML = winningPlayer + " won!";
			highlightWinningSequence(winningPlayer, winnerSequence);
		}, 1000);
//		setTimeout(function() {
			//alert((turn ? "RED" : "YELLOW") +  " has won!");
//		}, 100);
//        Crafty.scene("win");
    }
	
	function findEmptyRow(column) {
        if(!board[column]) return;
        for(var i = 0; i < board[column].length; i++) {
            if(board[column][i] == EMPTY)
                return i;
        }
        return COLUMN_FULL;
    }
    
    function placeCoin(column) {
    	current.startDrag();
    	current.attr({x: 64 * column, y: 0});
    	current.stopDrag();
    }
    
    function moveAI(result) {
    	var response = JSON.parse(result);
    		
    	if(!response) {
    		log("Invalid response");
    		return;
    	}
    	
    	var gameResult = response.gameResult;
    	if(gameResult == 3) {
    		gameActive = false;
    		return;
    	}
    	
    	var	column = response.column;
    	
    	if(gameResult != -1) {
    		gameActive = false;
    		placeCoin(column);
    		
    		if(gameResult == 0){
    			scoreboardYellow.innerHTML = parseInt(scoreboardYellow.innerHTML) + 1;
    			if(!autoPlay)
    				win(gameResult, response.winnerSequence);
    		} else if(gameResult == 1) {
    			scoreboardRed.innerHTML = parseInt(scoreboardRed.innerHTML) + 1;
    			if(!autoPlay)
    				win(gameResult, response.winnerSequence);
    		} else if(gameResult == 2) {
    			scoreboardDraw.innerHTML = parseInt(scoreboardDraw.innerHTML) + 1;
    			if(!autoPlay)
    				alert("Draw!");
    		}
    		
    		if(autoPlay) {
    			startNewGame();
    		}
    		
//    		setTimeout(function() {
//    			if(autoPlay) {
//        			startNewGame();
//        		}
//    		}, 1500);
    	} else {
    		placeCoin(column);
    	}
    }

	return {
		init: init,
		moveAI: moveAI
	}
}());

window.onload = ConnectFour.init;