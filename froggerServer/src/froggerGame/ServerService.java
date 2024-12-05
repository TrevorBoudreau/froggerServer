package froggerGame;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.ImageIcon;


//processing routine on server (B)
public class ServerService implements Runnable {
	final int CLIENT_PORT = 5555;

	private Socket s;
	private Scanner in;
	
	private frogSprite frog;
	private logSprite log[][];
	private carSprite car[][];
	private scoreSQL scoreDB;

	private char[] command;
	
	public ServerService() {}

	public ServerService (Socket Socket, frogSprite frog, logSprite[][] log, carSprite[][] car, scoreSQL scoreDB) {
		this.s = Socket;
		this.frog = frog;
		this.log = log;
		this.car = car;
	}
	
	public void run() {
		
		try {
			in = new Scanner(s.getInputStream());
			processRequest( );
			
		} catch (IOException e){
			e.printStackTrace();
		
		} finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//processing the requests
	public void processRequest () throws IOException {
		
		//if next request is empty then return
		while(true) {
			if(!in.hasNext( )){
				return;
			}
			String command = in.next();
			if (command.equals("Quit")) {
				return;
			} else {
				executeCommand(command);
			}
		}
		
	}
	
	public void executeCommand(String command) throws IOException{
		
		if ( command.equals("MOVEFROG") ) {
			
			System.out.println("RECIEVED: " + command);
			
			String direction = in.next();
			
			int x = frog.getX();
			int y = frog.getY();
			
			if (direction.equals("UP")) {
				
				System.out.println("RECIEVED: " + direction);
				
				//MOVE UP ONE STEP
				y -= gameProperties.STEP;
				
			} else if (direction.equals("LEFT")) {
				
				//MOVE LEFT ONE STEP
				x -= gameProperties.STEP;
					
				//Wrap character to other side if he goes off screen
				if (x + frog.getWidth() < 0) { x = gameProperties.SCREEN_WIDTH; }
				
			} else if (direction.equals("RIGHT")) {
				
				//MOVE RIGHT ONE STEP
				x += gameProperties.STEP;
					
				//Wrap character to other side if he goes off screen
				if (x >= gameProperties.SCREEN_WIDTH) { x = -1 * frog.getWidth(); }
				
			} else if (direction.equals("DOWN")) {
				
				//prevent frog from moving under lower perimeter
				if (y + gameProperties.STEP < gameProperties.SCREEN_HEIGHT) {
					y += gameProperties.STEP;
				}
				
			}
			
			//move the frog to new spot with new x and y
			frog.setX(x);
			frog.setY(y);
			
			//send response back to client
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);

			String commandOut = "GETFROG\n" + frog.getX() + "\n" + frog.getY() + "\n";
			System.out.println("Sending: " + commandOut);
			out.println(commandOut);
			out.flush();
				
			s2.close();
			return;
			
		} else if ( command.equals("GETFROG") ) {
			
			//open a socket to client
			//FROGPOSITION + frog.getX() + frog.getY() + \n
			
			//send response back to client
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);

			String commandOut = "GETFROG\n" + frog.getX() + "\n" + frog.getY() + "\n";
			System.out.println("Sending: " + commandOut);
			out.println(commandOut);
			out.flush();
				
			s2.close();
			
			return;
			
		} else if ( command.equals("STARTGAME") ) {
			
			//check the start game function in gameprep for reference
			
			//let frog be controllable
			//content.setFocusable(true);
			//content.requestFocusInWindow();  //DOES NOT WORK WITHOUT THIS LINE!!
			
			//hide visibility button
			//restartBtn.setVisible(false);
			
			//reset frogs position to start
			frog.setX(400);
			frog.setY(800);
			//frogLabel.setLocation(frog.getX(), frog.getY());
			
			//restart threads for cars and logs
			for ( int i = 0; i < car.length; i++ ) {
				int temp = 300;//temp local variable for adjusting height during car initialization
				
				for ( int j = 0; j < car[i].length; j++ ) {
					car[i][j].setX(i * 300);
					car[i][j].setY(gameProperties.SCREEN_HEIGHT - temp);
					car[i][j].setFrog(frog);
					//car[i][j].setFrogLabel(frogLabel);
					
					//carLabel[i][j].setLocation( car[i][j].getX(), car[i][j].getY() );
					
					car[i][j].runThread();
					
					temp += 100;
				}
			}
			for ( int i = 0; i < log.length; i++ ) {
				int temp2 = 700;//temp local variable for adjusting height during log initialization
				
				for ( int j = 0; j < car[i].length; j++ ) {
					log[i][j].setX(i * 300);
					log[i][j].setY(gameProperties.SCREEN_HEIGHT - temp2);
					log[i][j].setFrog(frog);
					//log[i][j].setFrogLabel(frogLabel);
					log[i][j].setIntersecting(true);
					
					//logLabel[i][j].setLocation( log[i][j].getX(), log[i][j].getY() );
					
					log[i][j].runThread();
					
					temp2 += 100;
				}
			}
			
			//send response back to client
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);

			String commandOut = "STARTGAME\n";
			System.out.println("Sending: " + commandOut);
			out.println(commandOut);
			out.flush();
			
			s2.close();
			
			//frogLabel.setIcon( frogImage );
			
			//score = scoreDB.getScore();
			//scoreLabel.setText("Score: " + score);
			
			return;
			
			
		} else if ( command.equals("WINGAME") ) {
				
			System.out.println("GAME WIN");
			
			//stop ongoing threads
			for ( int i = 0; i < car.length; i++ ) {
				for ( int j = 0; j < car[i].length; j++ ) {
					car[i][j].stopThread();
				}
			}
			for ( int i = 0; i < log.length; i++ ) {
				for ( int j = 0; j < log[i].length; j++ ) {
					log[i][j].stopThread();
				}
			}
			
			//prevent player from moving
			//content.setFocusable(false);
			
			//show visibility button
			//restartBtn.setVisible(true);
			
			//update score
			//scoreDB.addScore();
			
			//send response back to client
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);

			String commandOut = "WINGAME\n";
			System.out.println("Sending: " + commandOut);
			out.println(commandOut);
			out.flush();
				
			s2.close();
				
			return;
		
			
		} else if ( command.equals("LOSEGAME") ) {
			
			//check the LOSE game function in gameprep for reference
			
			System.out.println("GAME WIN");
			
			//stop ongoing threads
			for ( int i = 0; i < car.length; i++ ) {
				for ( int j = 0; j < car[i].length; j++ ) {
					car[i][j].stopThread();
				}
			}
			for ( int i = 0; i < log.length; i++ ) {
				for ( int j = 0; j < log[i].length; j++ ) {
					log[i][j].stopThread();
				}
			}
			
			//send response back to client
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);

			String commandOut = "LOSEGAME\n";
			System.out.println("Sending: " + commandOut);
			out.println(commandOut);
			out.flush();
				
			s2.close();
				
			return;
				
		} else if ( command.equals("GETCAR") ) {

			//send response back to client
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);
			
			for ( int i = 0; i < car.length; i++ ) {
				for ( int j = 0; j < car[i].length; j++ ) {
					
					String commandOut = "GETCAR\n" + car[i][j].getX() + "\n" + car[i][j].getY() + "\n" +car[i][j].getIsMoving()+ "\n";
					System.out.println("Sending: " + commandOut);
					out.println(commandOut);
					out.flush();
					
				}
			}
			
			s2.close();
			
			
			
			return;
			
		} else if ( command.equals("GETLOG") ) {
			
			//open a socket to client
			//.....
			
			//send response back to client
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);
			
			for ( int i = 0; i < log.length; i++ ) {
				for ( int j = 0; j < log[i].length; j++ ) {
					
					String commandOut = "GETLOG\n" + log[i][j].getX() + "\n" + log[i][j].getY() + "\n" + log[i][j].isIntersecting();
					System.out.println("Sending: " + commandOut);
					out.println(commandOut);
					out.flush();
					
				}
			}
			
			s2.close();
			
			return;
			
		}
	
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}