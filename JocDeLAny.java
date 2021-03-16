import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.awt.event.ActionEvent;

class gameHandler {
	private String board[][] = new String[4][4]; //El array que contindra quins caracters son els que volem per a jugar
	private int scoreActual; //Score de la Sessio
	private int maxScore = readHighScore(); //HighScore llegit de el fitxer
	private boolean gameStarted = false; //Per a fer track de si estem a una partida
	private JocDeLAny actualGame; //MIra el joc actual

	public gameHandler(JocDeLAny actualGame) { // Agafem quina es la partida que estem tractant
		this.actualGame = actualGame;
	}

	private void switchStartButton() {
		// TODO Metode que cambia el estat del boto de Començar
		if (gameStarted) {
			actualGame.changeTxtButtonStart("Reiniciar pantalla");
		} else {
			actualGame.changeTxtButtonStart("Comen\u00E7ar");
		}
	}

	public void startGame() { // Inicialitza tot el que calgui per a un nou tauler, sigui primer tauler o
								// refresh
		// TODO Auto-generated method stub
		if (!gameStarted) {
			gameStarted = true;
			scoreActual = 0;
		}
		updateTextHighScore();
		switchStartButton();
		restartButtons();
		reloadBoard();
	}

	JButton actualBtn;

	private void restartButtons() { // Refrescar totes les fields button
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				actualBtn = actualGame.getButtonFromArray(i, j);
				actualBtn.setEnabled(true);
				actualBtn.setText("?");
			}
		}

	}

	private void disableButtons() { // Desactiva tots els fiels button
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				actualBtn = actualGame.getButtonFromArray(i, j);
				actualBtn.setEnabled(false);
			}
		}
	}

	private void reloadBoard() { //CArrega tota la taula amb els seus minims de cada fitxer
		// W entre 1 i 3
		// X 2
		Random rand = new Random();
		int counterX = 0;
		int counterW = 0;
		int numActual;
		String carActual;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				numActual = rand.nextInt(3);

				if (numActual == 2 && counterW < 3) {
					carActual = "W";
					counterW++;
				} else if (numActual == 1 && counterX < 2) {
					carActual = "X";
					counterX++;
				} else {
					carActual = "0";
				}

				board[i][j] = carActual;
			}
		}
		while(counterX != 2 || counterW < 1) {
			while (counterX != 2) { //Si no té dos en forçara un
			int randomRow = rand.nextInt(board.length);
			int randomCol = rand.nextInt(board[randomRow].length);
			String carComprov = board[randomRow][randomCol];
			if (carComprov != "X") {
				board[randomRow][randomCol] = "X";
				counterX++;
			}

		}
		while (counterW < 1) { //Tindrá un com a minim
			int randomRow = rand.nextInt(board.length);
			int randomCol = rand.nextInt(board[randomRow].length);
			String carComprov = board[randomRow][randomCol];
			if (carComprov != "W") {
				board[randomRow][randomCol] = "W";
				counterW++;
			}
		}
		}
		
	}

	private String checkField(int row, int col) {
		return board[row][col];
	}

	public void gameOver() throws FileNotFoundException { //Carrega el popup de la puntuacio, desactiva tot i comprova si hi ha nova highscore
		// TODO Auto-generated method stub
		if (gameStarted) {
			loadPopUp();
		}
		gameStarted = false;
		disableButtons();
		switchStartButton();
		if (scoreActual > maxScore) {
			maxScore = scoreActual;
			updateTextHighScore();
			writeHighScore();
		}

	}

	public void loadPopUp() { // Creem el missatge de pop up i el carreguem amb JOptionPane

		String missatge = "Felicitats has fet ";
		if (scoreActual == 1) {
			missatge += scoreActual + " punt";
		} else {
			missatge += scoreActual + " punts";
		}
		JOptionPane.showMessageDialog(actualGame, missatge);

	}

	public void checkValid(JButton button, int row, int col) throws FileNotFoundException { // Aqui donarem la puntuacio
		String caracActual = checkField(row, col); // Aconsegueix que es el que te el boto
		button.setText(caracActual); // Canvia texte
		button.setEnabled(false); // Desactiva el botó
		if (caracActual.equals("0")) {
			scoreActual++;
		} else if (caracActual.equals("W")) {
			scoreActual *= 2;
		} else { // Acaba la partida
			gameOver();
		}
		actualGame.changeTxtScore(scoreActual);
	}

	private void writeHighScore() throws FileNotFoundException { // Aqui escriurá al fitxer de highscores
		updateTextHighScore();

		PrintWriter sw = new PrintWriter("highscore.txt");
		sw.print(maxScore);
		sw.close();
	}

	public int readHighScore() { // Legueix el fitxer i segons si esta ben formulat o no, lleguirá el int que li
									// diguin
		int data = 0;
		try {
			File myObj = new File("highscore.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				data = myReader.nextInt();
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Ha hagut un error al llegir/crear fitxer, el record es posara a 0");
		}

		return data;
	}

	private void updateTextHighScore() { // Cambia el texte de highscore
		actualGame.changeTxtRecord(maxScore);
	}
}

public class JocDeLAny extends JFrame {

	private JPanel contentPane;
	private JTextField puntuacio;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JocDeLAny frame = new JocDeLAny();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private JButton buttonArray[][] = new JButton[4][4]; //El array de botons que no conté informacio pero aguantara els events i interfaç
	private gameHandler thisGameHandler = new gameHandler(this); //El que portará la partida
	private JLabel recordLabel = new JLabel("Record : " + String.valueOf(thisGameHandler.readHighScore()) + " punts"); // Carreguem
																														// highscore
	private JButton startRestartbtn = new JButton("Comen\u00E7ar");

	public JocDeLAny() {
		super("The new Game of the year");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setResizeWeight(0.5);
		contentPane.add(splitPane);

		JPanel panelRight = new JPanel();
		splitPane.setRightComponent(panelRight);
		GridBagLayout gbl_panelRight = new GridBagLayout();
		gbl_panelRight.columnWidths = new int[] { 0, 0 };
		gbl_panelRight.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelRight.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelRight.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panelRight.setLayout(gbl_panelRight);

		GridBagConstraints gbc_recordLabel = new GridBagConstraints();
		gbc_recordLabel.anchor = GridBagConstraints.WEST;
		gbc_recordLabel.insets = new Insets(0, 0, 5, 0);
		gbc_recordLabel.gridx = 0;
		gbc_recordLabel.gridy = 1;
		panelRight.add(recordLabel, gbc_recordLabel);

		JLabel puntsLabel = new JLabel("Punts:");
		puntsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_puntsLabel = new GridBagConstraints();
		gbc_puntsLabel.anchor = GridBagConstraints.WEST;
		gbc_puntsLabel.insets = new Insets(0, 0, 5, 0);
		gbc_puntsLabel.gridx = 0;
		gbc_puntsLabel.gridy = 3;
		panelRight.add(puntsLabel, gbc_puntsLabel);

		puntuacio = new JTextField();
		puntuacio.setEditable(false);
		puntuacio.setText(String.valueOf(0));
		GridBagConstraints gbc_puntuacio = new GridBagConstraints();
		gbc_puntuacio.insets = new Insets(0, 0, 5, 0);
		gbc_puntuacio.anchor = GridBagConstraints.EAST;
		gbc_puntuacio.fill = GridBagConstraints.HORIZONTAL;
		gbc_puntuacio.gridx = 0;
		gbc_puntuacio.gridy = 4;
		panelRight.add(puntuacio, gbc_puntuacio);
		puntuacio.setColumns(10);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setEnabled(false);
		GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
		gbc_splitPane_1.ipady = 20;
		gbc_splitPane_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_splitPane_1.gridx = 0;
		gbc_splitPane_1.gridy = 5;
		panelRight.add(splitPane_1, gbc_splitPane_1);

		JButton sortirBtn = new JButton("Sortir");
		sortirBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					thisGameHandler.gameOver();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		splitPane_1.setLeftComponent(sortirBtn);

		startRestartbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisGameHandler.startGame();
			}
		});
		splitPane_1.setRightComponent(startRestartbtn);

		JPanel panelLeft = new JPanel();
		splitPane.setLeftComponent(panelLeft);

		panelLeft.setLayout(new GridLayout(0, 4, 0, 0));

		for (int i = 0; i < buttonArray.length; i++) { // Crearem tots els boyons amb el seu event corresponent
			for (int j = 0; j < buttonArray[i].length; j++) {
				buttonArray[i][j] = new JButton("?");
				buttonArray[i][j].setEnabled(false);
				buttonArray[i][j].addActionListener(new ButtonActionListener(i, j, thisGameHandler, this));
				// Cridem a un action listener nou que manara quina es la ubicacio del boto a la
				// funcio de comprovar que hi ha
				panelLeft.add(buttonArray[i][j]);
			}
		}

	}

	// Getter i Setters
	public void changeTxtRecord(int newInt) {
		String newRec = "Record : " + newInt + " punts";
		recordLabel.setText(newRec);
		return;
	}

	public void changeTxtButtonStart(String text) {
		startRestartbtn.setText(text);
		return;
	}

	public void changeTxtScore(int newInt) {
		puntuacio.setText(String.valueOf(newInt));
		return;
	}

	public JButton getButtonFromArray(int row, int col) {
		return buttonArray[row][col];
	}

	public void setFieldButtonArray(int row, int col, String caracter) {
		buttonArray[row][col].setText(caracter);
	}

}

class ButtonActionListener implements ActionListener { // Aquest aguanta el action de cada botó i les seves dades
														// corresponents
	private int row;
	private int col;
	private gameHandler currentGameHandler;
	private JocDeLAny actualGame;

	public ButtonActionListener(int i, int j, gameHandler gameH, JocDeLAny actual) {
		this.row = i;
		this.col = j;
		currentGameHandler = gameH;
		actualGame = actual;
	}

	public void actionPerformed(ActionEvent e) {
		JButton boto = actualGame.getButtonFromArray(row, col);
		try {
			currentGameHandler.checkValid(boto, row, col);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
