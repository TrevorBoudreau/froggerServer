//Trevor Boudreau, w0483725
//prog2200/3288 2024

package froggerGame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class gamePrepServer {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub

		frogSprite frog;
		logSprite log[][];
		carSprite car[][];
		
		//scoreSQL scoreDB;
		//score score = new score(0);
		
		//set up score db
		scoreSQL scoreDB = new scoreSQL();
		scoreDB.createDB();

		//set up frog sprite
		frog = new frogSprite(400, 800, 100, 90, gameProperties.FROG_IMAGE);

		car = new carSprite[4][3];

		for ( int i = 0; i < car.length; i++ ) {
			int temp = 300;//temp local variable for adjusting height during car initialization
					
			for ( int j = 0; j < car[i].length; j++ ) {
						
				car[i][j] = new carSprite( (i * 300), gameProperties.SCREEN_HEIGHT - temp, 100, 100, gameProperties.CAR_IMAGE, false);
				car[i][j].setFrog(frog);
						
				if (j != 1 ) {
					car[i][j].setStepSpeed(gameProperties.STEP_FAST);
					car[i][j].setStepDirection(1);

				} else {
					car[i][j].setStepSpeed(gameProperties.STEP_SLOW);
					car[i][j].setStepDirection(2);

				}

				temp += 100;
			}
		}	
				
		//set up a multi-array of log sprites
		log = new logSprite[4][3];
		//loop through all logs
		for ( int i = 0; i < log.length; i++ ) {
			int temp = 700;//temp local variable for adjusting height during log initialization
					
			for ( int j = 0; j < log[i].length; j++ ) {
				log[i][j] = new logSprite( (i * 300), gameProperties.SCREEN_HEIGHT - temp, 100, 100, gameProperties.LOG_IMAGE, false);
				log[i][j].setFrog(frog);
				//logLabel[i][j] = new JLabel();
				if (j != 1 ) {
					log[i][j].setStepSpeed(gameProperties.STEP_FAST);
					log[i][j].setStepDirection(1);
				} else {
					log[i][j].setStepSpeed(gameProperties.STEP_SLOW);
					log[i][j].setStepDirection(2);
				}

				temp += 100;
			}
		}
		//set up score label
		//score = scoreDB.getScore();

		final int SERVER_PORT = 5556;
		Thread serverThread = new Thread ( new Runnable () {
			
			public void run ( ) {
				
				synchronized(this) {

					ServerSocket server;
					try {
						
						server = new ServerSocket(SERVER_PORT);
						System.out.println("Waiting for clients to connect...");
						
						while(true) {
							Socket s = server.accept();
							
							ServerService myService = new ServerService (s, frog, log, car, scoreDB);
							Thread serverServiceThread = new Thread(myService);
							serverServiceThread.start();
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		
				}
			}
		});
		serverThread.start( );
	}
}
 