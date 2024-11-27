//Trevor Boudreau, w0483725
//prog2200/3288 2024

package froggerGame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class gamePrepServer {
	
	//private frogSprite frog;
	//private logSprite log[][];
	//private carSprite car[][];

	//private scoreSQL scoreDB;
	//int score = 0;

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub

		frogSprite frog;
		logSprite log[][];
		carSprite car[][];
		
		//scoreSQL scoreDB;
		score score = new score(0);
		
		//set up score db
		//scoreDB = new scoreSQL();
		//scoreDB.createDB();

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
		//logLabel = new JLabel[4][3];
		//logImage = new ImageIcon(getClass().getResource(gameProperties.LOG_IMAGE));
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
				
		//start car and log threads (service)
		/*
		for ( int i = 0; i < car.length; i++ ) {
			for ( int j = 0; j < car[i].length; j++ ) {
				car[i][j].runThread();
			}
		}
		for ( int i = 0; i < log.length; i++ ) {
			for ( int j = 0; j < log[i].length; j++ ) {
				log[i][j].runThread();
			}
		}
		*/
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
							System.out.println("client connected");
							
							ServerService myService = new ServerService (s, frog, log, car, score);
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
	
	/*
	
	public void gameWin() {
		
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
		scoreDB.addScore();
	}
	
	public void gameLose() {
		
		System.out.println("GAME LOSE");
		
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
		
		//this.frogLabel.setIcon( new ImageIcon( getClass().getResource(gameProperties.FROG_DEAD_IMAGE) ) );	
				
		//prevent player from moving
		//content.setFocusable(false);
				
		//show visibility button
		//restartBtn.setVisible(true);
		
		//update score
		scoreDB.minusScore();
	}
	
	public void gameStart() {
		
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
		
		//frogLabel.setIcon( frogImage );
		
		score = scoreDB.getScore();
		//scoreLabel.setText("Score: " + score);
		
	}
	
/*
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
		
		//current x and y of frog before step
		int x = frog.getX();
		int y = frog.getY();
			
		//new x or y for each direction key (UP, DOWN, LEFT, RIGHT)
		if ( e.getKeyCode()==KeyEvent.VK_UP) {
				
			//MOVE UP ONE STEP
			y -= gameProperties.STEP;
				
		} else if ( e.getKeyCode()==KeyEvent.VK_DOWN) {
				
			//prevent frog from moving under lower perimeter
			if (y + gameProperties.STEP < gameProperties.SCREEN_HEIGHT) {
				y += gameProperties.STEP;
			}
				
		} else if ( e.getKeyCode()==KeyEvent.VK_LEFT) {
		
			//MOVE LEFT ONE STEP
			x -= gameProperties.STEP;
				
			//Wrap character to other side if he goes off screen
			if (x + frog.getWidth() < 0) { x = gameProperties.SCREEN_WIDTH; }
				
		} else if ( e.getKeyCode()==KeyEvent.VK_RIGHT) {
				
			//MOVE RIGHT ONE STEP
			x += gameProperties.STEP;
				
			//Wrap character to other side if he goes off screen
			if (x >= gameProperties.SCREEN_WIDTH) { x = -1 * frog.getWidth(); }
				
		} else {
				
			//for all other keys, DONT move character 
			return;
		}
			
		//move the frog to new spot with new x and y
		frog.setX(x);
		frog.setY(y);
			
		//System.out.println("frog x: " + frog.getX() + " hitbox x: " + frog.getHitboxX() + "frog y: " + frog.getY() + " hitbox y: " + frog.getHitboxY());	
			
		//move the label with it
		frogLabel.setLocation( frog.getX() , frog.getY() );
			
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	
		//END GAME WHEN ENDZONE IS REACHED
		if (frog.getY() < 100) {
			
			gameWin();
			
		} else {
			
			//IF ANd CAR HAS STOPPED, END GAME
			//temp variable to break out of nested loop
			boolean breakOut = false;
			//temp variable to flag if one log is intersecting
			boolean collision = false;
			//temp variable to flag if one log is intersecting
			boolean intersect = false;
			
			for ( int i = 0; i < car.length; i++ ) {
				for ( int j = 0; j < car[i].length; j++ ) {
						
					if ( car[i][j].getIsMoving() == false ) {
						collision = true;
						breakOut = true;
						break;
					}
				
							
					if (breakOut == true) { break; }
				}
			}
					
			//IF FROG IS NOT INTERSECTING WITH LOG, END GAME
			for ( int i = 0; i < log.length; i++ ) {
				for ( int j = 0; j < log[i].length; j++ ) {
							
							
					if (log[i][j].isIntersecting() == true ) {
								
						intersect = true;
								
						breakOut = true;
						break;
					}
						
					if (breakOut == true) { break; }
				}
			}
					
			if (intersect != true) {
				gameLose();
			}
			
			if (collision == true) {
				gameLose();
			}
			
		}
			
			
	}
	
	


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		System.out.println("btn clicked");
		
		
		
		if (e.getSource() == restartBtn){
			gameStart();
		}
		
		
		
	}
	
	*/

}
 