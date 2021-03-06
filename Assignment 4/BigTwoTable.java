import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 * The BigTwoTable class implements the CardGameTable interface. 
 * It is used to build a GUI for the Big Two card game and handle all user actions.
 * 
 * @author Pranav Talwar
 */
public class BigTwoTable implements CardGameTable{
	
	/*
	 * A card game associated with this table.
	 */
	private CardGame game;
	
	/*
	 * A boolean array indicating which cards are being selected.
	 */
	private boolean[] selected;
	
	/*
	 * An integer specifying the index of the active player.
	 */
	private int activePlayer;
	
	/*
	 * The main window of the application.
	 */
	private JFrame frame;
	
	/*
	 * A panel for showing the cards of each player and the cards played on the table.
	 */
	private JPanel bigTwoPanel;
	
	/*
	 * A “Play” button for the active player to play the selected cards.
	 */
	private JButton playButton;
	
	/*
	 * A “Pass” button for the active player to pass his/her turn to the next player.
	 */
	private JButton passButton;
	
	/*
	 * A text area for showing the current game status as well as end of game messages.
	 */
	private JTextArea msgArea;
	
	/*
	 * A 2D array storing the images for the faces of the cards.
	 */
	private Image[][] cardImages;
	
	/*
	 * An image for the backs of the cards.
	 */
	private Image cardBackImage;
	
	/*
	 * An array storing the images for the avatars.
	 */
	private Image[] avatars;
	
	/*
	 * A private method to load the various images used in the application.
	 */
	private void loadImages() {
		avatars = new Image[4];
		avatars[0] = new ImageIcon("src/avatars/superman_128.png").getImage();
		avatars[1] = new ImageIcon("src/avatars/green_lantern_128.png").getImage();
		avatars[2] = new ImageIcon("src/avatars/wonder_woman_128.png").getImage();
		avatars[3] = new ImageIcon("src/avatars/batman_128.png").getImage();
		
		cardBackImage = new ImageIcon("src/cards/b.gif").getImage();
		
		char[] suit = {'d','c','h','s'};
		
		char[] rank = {'a', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k'};
		
		cardImages = new Image[4][13];
		for(int j=0;j<13;j++) {
			for(int i=0;i<4;i++) {
				String location = new String();
				location="src/cards/" + rank[j] + suit[i] + ".gif";
				cardImages[i][j] = new ImageIcon(location).getImage();
			}
		}
	}
	
	/*
	 * A private method to setup the GUI of the application.
	 */
	private void guiSetup() {
		
		//initializing the frame
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setTitle("Big Two Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Set menu and adding items to it
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("Game");
		menuBar.add(menu);
		
		JMenuItem restart = new JMenuItem("Restart");
		menu.add(restart);
		JMenuItem quit = new JMenuItem("Quit");
		menu.add(quit);
		
		restart.addActionListener(new RestartMenuItemListener());
		quit.addActionListener(new QuitMenuItemListener());
		
		//Buttons Panel
		JPanel buttonsPanel = new JPanel();
		
		//Buttons inside panel
		playButton = new JButton("Play");
		playButton.addActionListener(new PlayButtonListener());
		passButton = new JButton("Pass");
		passButton.addActionListener(new PassButtonListener());
		
		//Adding buttons to panel
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel.add(playButton);
		buttonsPanel.add(Box.createHorizontalStrut(20));
		buttonsPanel.add(passButton);
		
		//Playing area
		bigTwoPanel = new BigTwoPanel();
		bigTwoPanel.setLayout(new BoxLayout(bigTwoPanel, BoxLayout.Y_AXIS));
		
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		Font font = new Font("TimesRoman", Font.ITALIC, 15);
		msgArea.setFont(font);
		
		JScrollPane scroll = new JScrollPane (msgArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(525, 0));
		DefaultCaret caret = (DefaultCaret)msgArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		frame.getContentPane().add(BorderLayout.EAST, scroll);
		
		frame.add(BorderLayout.CENTER, bigTwoPanel);
		frame.add(BorderLayout.SOUTH, buttonsPanel);
		frame.setSize(1500,900);  
	    frame.setVisible(true); 
	}
	
	/**
	 * A constructor for creating a BigTwoTable. The parameter game is a reference to a card game associates with this table.
	 * 
	 * @param game A Card Game of BigTwo type to play through this GUI.
	 */
	public BigTwoTable(CardGame game) {
		
		this.game = game;
		selected = new boolean[13];
		setActivePlayer(game.getCurrentIdx()); 
		loadImages();
		guiSetup();	
	}
	
	/**
	 * A setter method that sets the index of the active player (i.e., the current player).
	 * 
	 * @param activePlayer an int value representing the index of the active player
	 */
	public void setActivePlayer(int activePlayer) {
		this.activePlayer = activePlayer;
	}
	
	/**
	 * Returns an array of indices of the cards selected.
	 * 
	 * @return an array of indices of the cards selected by the user.
	 */
	public int[] getSelected() {
		
		int counter = 0;
		for(int i=0;i<selected.length;i++)
		{
			if(selected[i]==true) {
				counter++;
			}
		}
		int[] result;
		if(counter==0) {
			return null;
		}
		result = new int[counter];
		int counter2 = 0;
		for(int i=0;i<selected.length;i++) {
			if(selected[i]==true) {
				result[counter2] = i;
				counter2++;
			}
		}
		return result;	
	}
	
	/**
	 * A method that resets the list of selected cards to an empty list.
	 */
	public void resetSelected() {
		for(int i=0;i<selected.length;i++) {
			selected[i] = false;
		}
	}
	
	/**
	 * A method that repaints the GUI.
	 */
	public void repaint() {
		resetSelected();
		frame.repaint();
	}
	
	/**
	 * A method that prints the specified string to the message area of the card game table.
	 * 
	 * @param msg the string to be printed to the message area of the card game table.
	 */
	public void printMsg(String msg) {
		msgArea.append(msg+"\n");
	}
	
	/**
	 * A method that clears the message area of the card game table.
	 */
	public void clearMsgArea() {
		msgArea.setText(null);
	}
	
	/**
	 * A method that resets the GUI.
	 */
	public void reset() {
		this.resetSelected();
		this.clearMsgArea();
		this.enable();
	}
	
	/**
	 * A method that enables user interactions.
	 */
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		bigTwoPanel.setEnabled(true);
	}
	
	/**
	 * A method that disables user interactions.
	 */
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		bigTwoPanel.setEnabled(false);
	}
	
	/**
	 * An inner class that extends the JPanel class and implements the MouseListener interface. 
	 * Overrides the paintComponent() method inherited from the JPanel class to draw the card game table. 
	 * Implements the mouseClicked() method from the MouseListener interface to handle mouse click events.
	 * 
	 * @author Pranav Talwar
	 */
	class BigTwoPanel extends JPanel implements MouseListener{
		
		// private variables for storing variables to assign positions and for checking clicks as well
		private static final long serialVersionUID = 1L;
		private int nameXCoord = 40;
		private int nameYCoord = 20;
		private int avatarXCoord = 5;
		private int avatarYCoord = 30;
		private int lineXCoord = 160;
		private int lineYCoord = 1600;
		private int downCardYCoord = 43;
		private int upCardYCoord = 23;
		private int cardXCoord = 155;
		private int cardWidth = 40;
		private int cardIncrement = 160;
		
		/*
		 * A private method to get the suit of a particular card of a particular player
		 */
		private int getSuitofPlayer(int Player, int Suit) {
			return game.getPlayerList().get(Player).getCardsInHand().getCard(Suit).getSuit();
		}
		
		/*
		 *  A private method to get the rank of a particular card of a particular player
		 */
		private int getRankofPlayer(int Player, int Rank) {
			return game.getPlayerList().get(Player).getCardsInHand().getCard(Rank).getRank();
		}
		
		/**
		 * BigTwoPanel default constructor which adds the Mouse Listener and sets background of the card table.
		 */
		public BigTwoPanel() {
			this.addMouseListener(this);
		}
		
		/**
		 * Draws the avatars, text and cards on card table.
		 * 
		 * @param g Provided by system to allow drawing.
		 */
		public void paintComponent(Graphics g)
		{
			// assigning color to background and the color for the text and the lines
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			this.setBackground(Color.GREEN.darker().darker().darker());
			g.setColor(Color.WHITE);
			int playerCounter = 0;
			
			//painting the four players avatars and their cards
			while(playerCounter<4) {
				if(activePlayer==playerCounter) {
					g.setColor(Color.BLUE);
					g.drawString("Player "+playerCounter, nameXCoord , nameYCoord + 160*playerCounter); 
				}
				else {
					g.drawString("Player "+playerCounter, nameXCoord , nameYCoord + 160*playerCounter); 
				}
				g.setColor(Color.WHITE);
				g.drawImage(avatars[playerCounter], avatarXCoord, avatarYCoord + 160*playerCounter, this);
			    g2.drawLine(0, lineXCoord*(playerCounter+1), lineYCoord, lineXCoord*(playerCounter+1));

			    //cards shown if players is active
			    if (activePlayer == playerCounter) 
			    {
			    	for (int i = 0; i < game.getPlayerList().get(playerCounter).getNumOfCards(); i++) 
			    	{
			    		int suit = getSuitofPlayer(playerCounter, i);
			    		int rank = getRankofPlayer(playerCounter, i);
			    		//if selected then painted in a raised fashion otherwise painted normally
			    		if (selected[i])
			    		{
			    			g.drawImage(cardImages[suit][rank], cardXCoord+cardWidth*i, upCardYCoord+cardIncrement*playerCounter, this);
			    		}	
			    		else
			    		{
			    			g.drawImage(cardImages[suit][rank], cardXCoord+cardWidth*i, downCardYCoord+cardIncrement*playerCounter, this);
			    		}		
			    	}
			    } 
			    //cards not shown if player is not active
			    else
			    {
			    	for (int i = 0; i < game.getPlayerList().get(playerCounter).getCardsInHand().size(); i++)
			    	{
			    		g.drawImage(cardBackImage, cardXCoord+cardWidth*i, downCardYCoord+cardIncrement*playerCounter, this);
			    	}
			    }
				playerCounter++;
			}
		    
			//drawing the part which shows the previous hand played and its type
		    g.drawString("Last Hand on Table", 10, 660);
		     
		    //checking if hands are empty or not
		    if (!game.getHandsOnTable().isEmpty())
		    {
		    	int sizeofHands = game.getHandsOnTable().size();
		    	Hand handOnTable = game.getHandsOnTable().get(sizeofHands - 1);
		    	g.drawString("Hand Type:\n " + game.getHandsOnTable().get(sizeofHands - 1).getType(), 10, 700);
		    	for (int i = 0; i < handOnTable.size(); i++)
	    		{
		    		int suit = handOnTable.getCard(i).getSuit();
		    		int rank = handOnTable.getCard(i).getRank();
	    			g.drawImage(cardImages[suit][rank], 160 + 40*i, 690, this);
	    		}
	    		
	    		g.drawString("Played by " + game.getHandsOnTable().get(sizeofHands-1).getPlayer().getName(), 10, 725);
		    }
		    repaint();
		}
		
		/**
		 * A method used to catch all the mouse click events and perform events/functions accordingly.
		 * It overrides the MouseClicked method of JPanel.
		 * 
		 * @param e This is a MouseEvent object which has been used to get the coordinates of the mouseClick
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			boolean flag = false; 
			int numberofCards = game.getPlayerList().get(activePlayer).getNumOfCards();
			int cardToCheck = numberofCards-1;
			
			if (e.getX() >= (cardXCoord+cardToCheck*40) && e.getX() <= (cardXCoord+cardToCheck*40+73)) 
			{
				if(!selected[cardToCheck] && e.getY() >= (downCardYCoord + cardIncrement*activePlayer) && e.getY() <= (downCardYCoord + cardIncrement*activePlayer+97))
				{
					selected[cardToCheck] = true;
					flag = true;
				} 
				else if (selected[cardToCheck] && e.getY() >= (upCardYCoord + cardIncrement*activePlayer) && e.getY() <= (upCardYCoord + cardIncrement*activePlayer+97))
				{
					selected[cardToCheck] = false;
					flag = true;
				}
			}
			for (cardToCheck = numberofCards-2; cardToCheck >= 0 && !flag; cardToCheck--) 
			{
				if (e.getX() >= (cardXCoord+cardToCheck*cardWidth) && e.getX() <= (cardXCoord+(cardToCheck+1)*cardWidth)) 
				{
					if(!selected[cardToCheck] && e.getY() >= (downCardYCoord+cardIncrement*activePlayer) && e.getY() <= (downCardYCoord+cardIncrement*activePlayer+97))
					{
						selected[cardToCheck] = true;
						flag = true;
					} 
					else if (selected[cardToCheck] && e.getY() >= (upCardYCoord+cardIncrement*activePlayer) && e.getY() <= (upCardYCoord+cardIncrement*activePlayer+97))
					{
						selected[cardToCheck] = false;
						flag = true;
					}
				}
				else if (e.getX() >= (cardXCoord+(cardToCheck+1)*cardWidth) && e.getX() <= (cardXCoord+cardToCheck*cardWidth+73) && e.getY() >= (downCardYCoord+cardIncrement*activePlayer) && e.getY() <= (downCardYCoord+cardIncrement*activePlayer+97)) 
				{
					if (selected[cardToCheck+1] && !selected[cardToCheck]) 
					{
						selected[cardToCheck] = true;
						flag = true;
					}
				}
				else if (e.getX() >= (cardXCoord+(cardToCheck+1)*cardWidth) && e.getX() <= (cardXCoord+cardToCheck*cardWidth+73) && e.getY() >= (upCardYCoord + cardIncrement*activePlayer) && e.getY() <= (upCardYCoord + cardIncrement*activePlayer+97))
				{
					if (!selected[cardToCheck+1] && selected[cardToCheck])
					{
						selected[cardToCheck] = false;
						flag = true;
					}
				}
			}
			this.repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
	
	/**
	 * An inner class that implements the ActionListener interface. 
	 * Implements the actionPerformed() method from the ActionListener interface to handle button-click events for the “Play” button. 
	 * When the “Play” button is clicked, it calls the makeMove() method of CardGame object to make a move.
	 * 
	 * @author Pranav Talwar
	 */
	class PlayButtonListener implements ActionListener{
		
		/**
		 * The function is overridden from the ActionListener Interface 
		 * and is used to perform the requisite function when the button is clicked.
		 * 
		 * @param e This is a ActionEvent object to detect if some user interaction was performed on the given object
		 */
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (getSelected() == null)
			{
				printMsg("Select cards to play.\n");
			}
			else {
			game.makeMove(activePlayer, getSelected());
			}
			repaint();
		}
		
	}
	
	/**
	 * This inner class implements the ActionListener interface and is used to detect the clicks on the passButton 
	 * and call the makeMove function based on the click.
	 *
	 * @author Pranav Talwar
	 **/
	class PassButtonListener implements ActionListener{
		
		/**
		 * The function is overridden from the ActionListener Interface 
		 * and is used to perform the requisite function when the button is clicked.
		 * 
		 * @param e This is a ActionEvent object to detect if some user interaction was performed on the given object.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			game.makeMove(activePlayer, null);
			repaint();
		}	
	}
	
	/**
	 * This inner class implements the actionListener interface for the Restart Menu Item in the JMenuBar to restart the game on click.
	 * 
	 * @author Pranav Talwar
	 */
	class RestartMenuItemListener implements ActionListener{

		/**
		 * The function overrides the ActionPerformed function in ActionListener interface to detect 
		 * the user interaction on the object and carry out necessary functions.
		 * 
		 * @param e This is a ActionEvent object to detect if some user interaction was performed on the given object.
		 */
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			BigTwoDeck deck = new BigTwoDeck();
			deck.shuffle();
			game.start(deck);
			reset();
		}
	}
	
	/**
	 * This inner class implements the actionListener interface for the Quit Menu Item in the JMenuBar to quit the game on click. 
	 * 
	 * @author Pranav Talwar
	 */
	class QuitMenuItemListener implements ActionListener{

		/**
		 * The function overrides the ActionPerformed function in ActionListener interface to detect 
		 * the user interaction on the object and carry out necessary functions.
		 *  
		 *  @param e This is a ActionEvent object to detect if some user interaction was performed on the given object
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			System.exit(0);	
		}
		
		
	}

}
				