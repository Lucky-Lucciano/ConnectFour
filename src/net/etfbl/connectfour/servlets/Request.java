package net.etfbl.connectfour.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.etfbl.connectfour.Game;

/**
 * Servlet implementation class Game
 */
@WebServlet("/req")
public class Request extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Depth == 4 - 50% win rate for me
	// Depth == 5 - 1% win rate for me
	private static final int DEPTH = 6;
    
	public Game game;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public Request() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// TODO U sesiju upisati trenutni score
		//HttpSession session = request.getSession();
		//session.setAttribute("score", GameClassGetCurrentScore()++);
		PrintWriter pw = response.getWriter();
		response.setContentType("text/plain");
		
		if(request.getParameter("data") == null) {
			pw.println("No information received!");
			return;
		}
		
		JsonObject requestObj;
		
		try {
			requestObj = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();
		} catch(Exception e) {
			System.out.println("ParseException: " + e.getMessage());
			return;
		}
		
		switch(requestObj.get("type").getAsInt()) {
			case 0:
				// Create a new game
				game = new Game(6, 7, requestObj.get("data").getAsJsonObject().get("startingPlayer").getAsInt(), 1, DEPTH);
				break;
			case 1:
				//Handle move for current game
				JsonObject moveInfo = requestObj.get("data").getAsJsonObject();
				String AIMove = game.makeMove(moveInfo.get("row").getAsInt(), moveInfo.get("column").getAsInt());
				pw.println(AIMove);
			default:
				break;
		}
		
		pw.close();
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	/*public static void main(String[] args) {
		
	}*/
}
