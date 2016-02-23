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
    RED = 1;

var vsAI = true,
	firstRun = true,
	yellowPlayerType,
	redPlayerType;

function startNewGame() {
	console.log("START !!");
	function newGameResponse() {
		/**
		 * Ako nije human player da onda samostalno odigra potez
		 */
		if(yellowPlayerType != 0) {
			createAjaxRequest("POST", "/ConnectFour/req", ConnectFour.moveAI, JSON.stringify({
        		type: 1,
            	data: {
            		column: -1,
            		row: -1,
            		player: 0
            	}
        	}));
		}
	}
	
	yellowPlayerType = document.getElementById('yellowMenu').selectedIndex;
	redPlayerType = document.getElementById('redMenu').selectedIndex;
	
	createAjaxRequest("POST", "/ConnectFour/req", newGameResponse, JSON.stringify({
 		type: 0,
     	data: {
     		columns: 7,
     		rows: 6,
     		startingPlayer: YELLOW,
     		yellow: yellowPlayerType,
     		red: redPlayerType,
     		yellowDepth: parseInt(document.getElementById('cutoffYellow').value),
     		redDepth: parseInt(document.getElementById('cutoffRed').value),
     		autoPlay: document.getElementById('autoPlay').checked
     	}
 	}));
	
	if(firstRun) {
		firstRun = false;
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
	httpRequest.send(data ? "data=" + data + "&test=FCb" : null);
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
	                    console.log("STOP");
	                    var column = Math.round(this._x / 64);
	                    this.x = column * 64;
	                    this.gravity("stopper");
	                    this.unbind("mousedown");
	                    
	                    setTimeout(function() {
	                    	reset(column);
	                    }, 1000)
	                });
	            }
	        });

	        function reset(column) {
	            var row = findEmptyRow(column);
	            if(row !== COLUMN_FULL && column >= 0 && column < 7) {
	            	board[column][row] = turn;
	                
	                console.log("TURN made: " + turn + ";" + (turn ? "red" : "yellow"));
	                
	                if(checkFour(column,row)) {
	                    win(turn);
	                    return;
	                }
	                
//	                if(turn == 0) {
	                // TODO provjera je li human
	                	makeAIMove(column, row, turn);
//	                } else if(turn == 1) {
//	                	makeAIMove(column, row, turn);
//	                }

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
	}
	
	function win(turn) {
		  alert((turn ? "RED" : "YELLOW") +  " has won!");
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

    function checkFour(column,row) {
        if(checkVertical(column,row)) return true;
        if(checkHorizontal(column,row)) return true;
        if(checkLeftDiagonal(column,row)) return true;
        if(checkRightDiagonal(column,row)) return true;
        return false;
    }

    function checkVertical(column,row) {
        if(row < 3) return false;
        for(var i = row; i > row-4; i--) {
            if(board[column][i] != turn) return false;
        }
        return true;
    }

    function checkHorizontal(column,row) {
        var counter = 1;
        for(var i = column-1; i >= 0; i--) {
            if(board[i][row] != turn) break;
            counter++;
        }

        for(var j = column+1; j < 7; j++) {
            if(board[j][row] != turn) break;
            counter++;
        }
        return counter>=4;
    }

    function checkLeftDiagonal(column,row) {
        var counter = 1;
        var tmp_row = row-1;
        var tmp_column = column-1;

        while(tmp_row >= 0 && tmp_column >= 0) {
            if(board[tmp_column][tmp_row] == turn) {
                counter++;
                tmp_row--;
                tmp_column--;
            } else break;
        }

        row += 1;
        column += 1;

        while(row < 6 && column < 7) {
            if(board[column][row] == turn) {
                counter++;
                row++;
                column++;
            } else { break; }
        }
        return counter>=4;
    }

    function checkRightDiagonal(column,row) {
        var counter = 1;
        var tmp_row = row+1;
        var tmp_column = column-1;

        while(tmp_row < 6 && tmp_column >= 0) {
            if(board[tmp_column][tmp_row] == turn) {
                counter++;
                tmp_row++;
                tmp_column--;
            } else break;
        }

        row -= 1;
        column += 1;

        while(row >= 0 && column < 7) {
            if(board[column][row] == turn) {
                counter++;
                row--;
                column++;
            } else break;
        }
        return counter>=4;
    }
    
    function moveAI(result) {
    	var moveDecision = JSON.parse(result),
    		column = moveDecision.column;
    	
    	current.startDrag();
    	current.attr({x: 64 * column, y: 0});
    	current.stopDrag();
    }

	return {
		init: init,
		moveAI: moveAI
	}
}());

window.onload = ConnectFour.init;