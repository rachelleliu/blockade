/* Rachelle Liu
January 19, 2021
ICS3U1 Ms. Strelkovska
final project: version 0.7
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

class Final extends JPanel implements KeyListener, ActionListener, MouseListener {
	//declare variables
	int borderTop = 0, borderLeft = 0, borderRight = 725, borderDown = 725;

	int[] playerBodyX = new int [5000], playerBodyY = new int [5000];
	int playerX = 100, playerY = 100;
	
	int[] aiBodyX = new int [5000], aiBodyY = new int [5000];
	int aiX = 625, aiY = 625;
	
	boolean right = true, left = false, up = false, down = false;
	boolean aiRight = false, aiLeft = true, aiUp = false, aiDown = false;
	
	int counter = 0;
	
	int playerScore = 0, aiScore = 0;
	
	JButton play, instructions, exit; 
	ImageIcon playImg, questionImg, exitImg;
	
	Font arcade, smallArcade;
	
	boolean startMenu = true, gameOver = false, restarting = false, tie = false;
	
	int aiCounter = 0, aiPixels = 0;
	
	Timer timer;
	
	public Final(){  
		this.setLayout(null); 
		
		//make buttons
		playImg = new ImageIcon("assets//playbutton.png");
		questionImg = new ImageIcon("assets//questionmark.png");
		exitImg = new ImageIcon ("assets//exitdoor.png");
		
		timer = new Timer (150, this);
		
		//play button
		play = new JButton(playImg); 
		play.setOpaque(false);
		play.setContentAreaFilled(false);
		play.setBorderPainted(false);
		play.setFocusPainted(false); 
		play.setBounds (321, 335, 150, 200);
		this.add(play);
		play.addActionListener(this);
		play.addKeyListener(this);
		
		//instructions button
		instructions = new JButton(questionImg);
		instructions.setOpaque (false);
		instructions.setContentAreaFilled (false);
		instructions.setBorderPainted (false);
		instructions.setFocusPainted (false);
		instructions.setBounds(155, 370, 140, 140);
		this.add(instructions);
		instructions.addActionListener(this);
		instructions.addKeyListener(this);
		
		//exit button
		exit = new JButton (exitImg);
		exit.setOpaque (false);
		exit.setContentAreaFilled (false);
		exit.setBorderPainted (false);
		exit.setFocusPainted (false);
		exit.setBounds(485, 370, 140, 140);
		this.add(exit);
		exit.addActionListener(this);
		exit.addKeyListener(this);
		
		//add listeners
		addKeyListener(this); 
		addMouseListener(this);  
		
		//start timer
		timer.start ();
	}
	
	public void paintComponent (Graphics g){
		//import font
		File arcadeFile = new File ("assets//PressStart2P.ttf");
		
		try {
			arcade = Font.createFont (Font.TRUETYPE_FONT, arcadeFile).deriveFont (60f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
			ge.registerFont (Font.createFont (Font.TRUETYPE_FONT, arcadeFile));
			
			smallArcade = Font.createFont (Font.TRUETYPE_FONT, arcadeFile).deriveFont (30f);
			ge.registerFont (Font.createFont (Font.TRUETYPE_FONT, arcadeFile));
		}
		
		catch (IOException | FontFormatException e) {
			System.out.println ("error: " + e);
		}
		
		super.paintComponent(g);
		//background
		g.setColor (Color.CYAN);
		g.fillRect (0, 0, 750, 750);
		
		g.setColor (Color.BLACK);
		g.fillRect (25, 25, 700, 700);
		
		g.setFont (arcade); 
		g.setColor (Color.CYAN);
		
		//title screen
		if (startMenu && !gameOver)
			g.drawString("blockade",150, 270);  
			
		//paint snakes
		else {
			for (int i = 0; i < playerBodyX.length; i++) {
				g.fillRect (playerBodyX [i], playerBodyY [i], 25, 25);
				g.fillRect (aiBodyX [i], aiBodyY [i], 25, 25);
			}
		}
		
		//game over message
		if (gameOver) {
			g.drawString ("game over", 115, 270);
			g.setFont (smallArcade);
			if (playerScore > aiScore)
				g.drawString ("you won! play again?", 85, 320);
			else
				g.drawString ("you lost. play again?", 70, 320);
		}
		
		//show scores
		if (restarting) {
			g.setFont (smallArcade);
			g.drawString (String.valueOf (playerScore), 100, 75);
			g.drawString (String.valueOf (aiScore), 625, 600);
		}
		
		//tie message
		if (tie) {
			g.setFont (smallArcade);
			g.drawString ("tie", 334, 370);
		}
	} 

	public void actionPerformed(ActionEvent e) {
		//play button clicked
		if(e.getSource() == play) {
			startMenu = false; 
			gameOver = false;
			restarting = false;
			playerScore = 0;
			aiScore = 0;
		}
		
		//instructions panel
		else if (e.getSource() == instructions)
			JOptionPane.showMessageDialog(null, "Use the WASD or arrow keys to control your snake. \nYour goal is to avoid hitting anything before your opponent can. \nThe surviving snake earns a point; first to 6 points wins.", "how to play",JOptionPane.INFORMATION_MESSAGE ); 
		//exit button clicked
		else if (e.getSource() == exit)
			System.exit(0);
		
		//game ends once score reaches 6
		if (playerScore == 6 || aiScore == 6) {
			endGame ();
		}
		
		//disable buttons during game
		if (!startMenu && !gameOver) {
			disable (play);
			disable (instructions);
			disable (exit);
		}
		
		//get snake movement
		if (!startMenu && !gameOver) {
			if (right) 
				playerX += 25;
			if (left)
				playerX -= 25;
			if (up)
				playerY -= 25;
			if (down)
				playerY += 25;
		
			int aiMove = aiMovement ();

			switch (aiMove) {
				case 1:
					aiX += 25;
					aiRight = true;
					aiLeft = false;
					aiUp = false;
					aiDown = false;
					break;
				case 2:
					aiX -= 25;
					aiRight = false;
					aiLeft = true;
					aiUp = false;
					aiDown = false;
					break;
				case 3:
					aiY -= 25;
					aiRight = false;
					aiLeft = false;
					aiUp = true;
					aiDown = false;
					break;
				case 4:
					aiY += 25;
					aiRight = false;
					aiLeft = false;
					aiUp = false;
					aiDown = true;
					break;
				case 0:
					playerScore++;
					restart ();
					break;
			}

			checkCollision ();
			
			playerBodyX [counter] = playerX;
			playerBodyY [counter] = playerY;
			aiBodyX [counter] = aiX;
			aiBodyY [counter] = aiY;
			
			counter++;
			
			repaint ();
		}
	} 

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {
		//w or up 
		if(e.getKeyCode() == 87 || e.getKeyCode () == 38) {
			if (!down) {
				left = false;
				right = false;
				up = true;
				down = false;
			}
		}
		//a or left
		if (e.getKeyCode() == 65 || e.getKeyCode () == 37) {
			if (!right) {
				left = true;
				right = false;
				up = false;
				down = false;
			}
		}
		//s or down
		if(e.getKeyCode() == 83 || e.getKeyCode () == 40) {
			if (!up) {
				left = false;
				right = false;
				up = false;
				down = true;
			}
		}
		//d or right
		if(e.getKeyCode() == 68 || e.getKeyCode () == 39) {
			if (!left) {
				left = false;
				right = true;
				up = false;
				down = false;
			}
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
	
	//disables buttons
	public void disable (JButton button) {
		button.setEnabled (false);
		button.setVisible (false);
		button.setFocusable (false);
		this.setFocusable (true);
	}
	
	//enables buttons
	public void enable (JButton button) {
		button.setEnabled (true);
		button.setVisible (true);
		button.setFocusable (true);
	}
	
	//if game has ended
	public void endGame () {
		gameOver = true;
		startMenu = true;
		restarting = true;
		enable (play);
		enable (instructions);
		enable (exit);
		repaint ();
	}
	
	//checks for collisions
	public void checkCollision () {
		//head to head
		if (playerX == aiX && playerY == aiY) {
			tie = true;
			restart ();
		}
		
		//player collision
		for (int i = 0; i < playerBodyX.length; i++) {
			if (playerX == playerBodyX [i] && playerY == playerBodyY [i]) {
				aiScore++;
				restart ();
			}
			else if (playerX == borderLeft || playerX == borderRight) {
				aiScore++;
				restart ();
			}
			else if (playerY == borderTop || playerY == borderDown) {
				aiScore++;
				restart ();
			}
			else if (playerX == aiBodyX [i] && playerY == aiBodyY [i]) {
				aiScore++;
				restart ();
			}
		}
		
		//AI collision
		for (int i = 0; i < aiBodyX.length; i++) {
			if (aiX == aiBodyX [i] && aiY == aiBodyY [i]) {
				playerScore++;
				restart ();
			}
			else if (aiX == borderLeft || aiX == borderRight) {
				playerScore++;
				restart ();
			}
			else if (aiY == borderTop || aiY == borderDown) {
				playerScore++;
				restart ();
			}
			else if (aiX == playerBodyX [i] && aiY == playerBodyY [i]) {
				playerScore++;
				restart ();
			}
		} 
	}
	
	//resets snakes
	public void restart () {
		playerBodyX = new int [5000];
		playerBodyY = new int [5000];
		playerX = 100;
		playerY = 100;
		
		aiBodyX = new int [5000];
		aiBodyY = new int [5000];
		aiX = 625;
		aiY = 625;
		
		counter = 0;
		
		left = false;
		right = true;
		up = false;
		down = false;
		
		aiLeft = true;
		aiRight = false;
		aiUp = false;
		aiDown = false;
		
		if (playerScore != 6 && aiScore != 6) {
			timer.stop ();
			restarting = true;
		
			Timer restartDelay = new Timer(2000, e -> {timer.start (); restarting = false; tie = false;});
			restartDelay.setRepeats (false);
			restartDelay.start ();
		}
	} 
	
	//to generate AI movement
	public int aiMovement () {
		boolean alreadyRight = false, alreadyLeft = false, alreadyUp = false, alreadyDown = false;
		boolean canAIRight, canAILeft, canAIUp, canAIDown;
		int trueCount = 0;
		
		//checks if AI can move	
		canAIRight = isRightValid ();
		canAILeft = isLeftValid ();
		canAIUp = isUpValid ();
		canAIDown = isDownValid ();
		
		if (canAIRight)
			trueCount++;
		if (canAILeft)
			trueCount++;
		if (canAIUp)
			trueCount++;
		if (canAIDown)
			trueCount++;
		
		//snake moves in same direction for at least 3 blocks if it's not about to crash
		if (aiPixels < 4 && trueCount > 2) {
			aiCounter = 0;
			aiPixels++;
			
			if (aiRight)
				return 1;
			else if (aiLeft)
				return 2;
			else if (aiUp)
				return 3;
			else if (aiDown)
				return 4;
		}
		
		else
			aiPixels = 0;
	
		//if no moves are possible
		if (aiCounter == 0)
			return 0;
		
		//make array with possible moves
		int[] validMovement = new int [aiCounter];
		
		for (int i = 0; i < aiCounter;) {
			if (!alreadyRight) {
				if (canAIRight) {
					validMovement [i] = 1;
					i++;
				}
				alreadyRight = true;
			}
			else if (!alreadyLeft) {
				if (canAILeft) {
					validMovement [i] = 2;
					i++;
				}
				alreadyLeft = true;
			}
			else if (!alreadyUp) {
				if (canAIUp) {
					validMovement [i] = 3;
					i++;
				}
				alreadyUp = true;
			}
			else if (!alreadyDown) {
				if (canAIDown) {
					validMovement [i] = 4;
					i++;
				}
				alreadyDown = true;
			}
		}
		
		int move = (int) (Math.random () * aiCounter);
	
		aiCounter = 0;
		
		return validMovement [move];
	}
	
	//checks if AI can move right
	public boolean isRightValid () {
		for (int i = 0; i < aiBodyX.length; i++) {
			if (aiX + 25 == aiBodyX [i] && aiY == aiBodyY [i])
				return false;
				
			if (aiX + 25 == playerBodyX [i] && aiY == playerBodyY [i])
				return false;
		}
		
		//border collisions
		if (aiX + 25 == borderRight || aiLeft)
			return false;
		
		aiCounter++;
		return true;
	}
	
	//checks if AI can move left
	public boolean isLeftValid () {
		for (int i = 0; i < aiBodyX.length; i++) {
			if (aiX - 25 == aiBodyX [i] && aiY == aiBodyY [i])
				return false;
			
			if (aiX - 25 == playerBodyX [i] && aiY == playerBodyY [i])
				return false;
		}
		
		//border collisions
		if (aiX - 25 == borderLeft || aiRight)
			return false;
		
		aiCounter++;
		return true;
	}
	
	//checks if AI can move up
	public boolean isUpValid () {
		for (int i = 0; i < aiBodyX.length; i++) {
			if (aiX == aiBodyX [i] && aiY - 25 == aiBodyY [i])
				return false;
			
			if (aiX == playerBodyX [i] && aiY - 25 == playerBodyY [i])
				return false;
		}
		
		//border collisions
		if (aiY - 25 == borderTop || aiDown) 
			return false;
		
		aiCounter++;
		return true;
	}
	
	//checks if AI can move down
	public boolean isDownValid () {
		//collisions
		for (int i = 0; i < aiBodyX.length; i++) {
			if (aiX == aiBodyX [i] && aiY + 25 == aiBodyY [i])
				return false;
			
			if (aiX == playerBodyX [i] && aiY + 25 == playerBodyY [i])
				return false;
		}	
		
		//border collisions
		if (aiY + 25 == borderDown || aiUp)
			return false;
		
		aiCounter++;
		return true;
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("blockade");
		
		f.setResizable (false);
		
		Container cont = f.getContentPane(); 
		cont.setLayout(new BorderLayout());  

		Final bp = new Final(); 
		cont.add(bp, BorderLayout.CENTER); 

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		f.setVisible(true);    
		f.setSize(764, 789); 
		f.setLocationRelativeTo(null);
	}
}