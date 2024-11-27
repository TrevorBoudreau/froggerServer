import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


//processing routine on server (B)
public class ServerService implements Runnable {
	final int CLIENT_PORT = 5656;

	private Socket s;
	private Scanner in;
	
	private frogSprite frog;
	private logSprite log[][];
	private carSprite car[][];
	private score score;
	
	public ServerService() {}

	public ServerService (Socket Socket, frogSprite frog, logSprite[][] log, carSprite[][] car, score score) {
		this.s = Socket;
		this.frog = frog;
		this.log = log;
		this.car = car;
		this.score = score;
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
			
			String direction = in.next();
			
			int x = frog.getX();
			int y = frog.getY();
			
			if (direction.equals("UP")) {
				
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
			
			return;
			
		} else if ( command.equals("GETFROG") ) {
			
			//open a socket to client
			//FROGPOSITION + frog.getX() + frog.getY() + \n
			return;
			
		} else if ( command.equals("STARTGAME") ) {
			
			//check the start game function in gameprep for reference
			
			return;
			
			
		} else if ( command.equals("WINGAME") ) {
				
			//check the WIN game function in gameprep for reference
				
			return;
		
			
		} else if ( command.equals("LOSEGAME") ) {
			
			//check the LOSE game function in gameprep for reference
				
			return;
				
		} else if ( command.equals("GETCARX") ) {
			
			//open a socket to client
			//.....
			
			return;
			
		} else if ( command.equals("GETCARY") ) {
			
			//open a socket to client
			//.....
			
			return;
			
		} else if ( command.equals("GETLOGX") ) {
			
			//open a socket to client
			//.....
			
			return;
			
		} else if ( command.equals("GETLOGY") ) {
			
			//open a socket to client
			//.....
			
			return;
			
		}
		
		
		
		/*
		if ( command.equals("PLAYER")) {
			
			int playerNo = in.nextInt();
			String playerAction = in.next();
			System.out.println("Player "+playerNo+" moves "+playerAction);
			
			
			//send a response
			Socket s2 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s2.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);

			String commandOut = "PLAYER "+playerNo+" POSTION 500 400\n";
			System.out.println("Sending: " + commandOut);
			out.println(commandOut);
			out.flush();
				
			s2.close();

		}
		*/
	}
	
}