package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.MouseControllerBot;
import model.PressioneMouse;
import model.PressioneTasto;
import view.AppLogger.DebugMode;
import static view.AppLogger.*;

public class ExportRilevazioniGUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1607003797146656821L;
	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 700;
	private static final int MARGIN_HUGE = 50;
	private static final int MARGIN_MEDIUM = 30;
	private static final int MARGIN_SMALL = 20;
	private static final int MARGIN_XSMALL = 15;
	private static final int MARGIN_XXSMALL = 10;
	private static final int TEXT_FIELD_LENGTH = 5;
	private static final int LOG_AREA_WIDTH = (FRAME_WIDTH/2);
	private static final int LOG_AREA_HEIGHT = 550;
	private static final String CONFIG_DIR = "config";
	private static final String FILE_MAPPATURA_CLICK = "mappa_click.txt"; 
	private JPanel mainPanel;
	private SpringLayout layout;
	private JLabel posizioneCorrenteCursoreLabel;
	private JLabel xCorrenteCursoreLabel;
	private JTextField xCorrenteCursoreTextField;
	private JLabel yCorrenteCursoreLabel;
	private JTextField yCorrenteCursoreTextField;
	private JLabel clickCursoreLabel;
	private JLabel xClickCursoreLabel;
	private JTextField xClickCursoreTextField;
	private JLabel yClickCursoreLabel;
	private JTextField yClickCursoreTextField;
	private JLabel delayLabel;
	private JTextField delayTextField;
	private JButton addButton;
	private JButton clearButton;
	private JButton startButton;
	
	private JLabel keyPressLabel;
	private JLabel keyLabel;
	private JTextField keyPressTextField;
	private JLabel delaykeyPressLabel;
	private JTextField delaykeyPressTextField;
	private JButton addkeyPressButton;
	
	private JLabel automatizzaLabel;
	private JLabel intervalloTemporaleLabel;
	private Integer[] intervalliTemporaliArray = {1, 5, 10, 15, 30, 60 };
	private JComboBox<Integer> intervalloTemporaleComboBox;
	private JButton pathFileMappatureButton;
	private JFileChooser pathFileMappatureChooser;
	private JButton loadMappatureButton;
	private String fileMappature = "./" + CONFIG_DIR + "/" + FILE_MAPPATURA_CLICK;
	private JButton startRoutineButton;
	private JButton stopRoutineButton;
	private JTextArea textArea;
	private JScrollPane logArea;
	
	private Boolean terminate = false;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> serviceHandler;
	
	public ExportRilevazioniGUI() {
		super("Export Rilevazioni - Sviluppato dall'ing. Bonifazi Andrea");
		setSize(FRAME_WIDTH,FRAME_HEIGHT);
		createComponents();
		layoutComponents();
		
		center();
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		new Thread(()->{
					while(!terminate) {
						Point p = MouseControllerBot.getMousePosition();
						xCorrenteCursoreTextField.setText(""+p.getX());
						yCorrenteCursoreTextField.setText(""+p.getY());
					}
				}).start();
		log("Creazione GUI terminata");
	}
	
	private void center() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (int)((d.getWidth()-getWidth())/2), (int)((d.getHeight()-getHeight())/2) );
	}
	
	private void createComponents() {
		mainPanel = (JPanel) getContentPane();
		mainPanel.setBackground(Color.WHITE);
		layout = new SpringLayout();
		setLayout(layout);
		posizioneCorrenteCursoreLabel = new JLabel("Posizione corrente del cursore");
		posizioneCorrenteCursoreLabel.setFont(new Font("Serif",Font.ITALIC,24));
		xCorrenteCursoreLabel = new JLabel("X: ");
		xCorrenteCursoreTextField = new JTextField(TEXT_FIELD_LENGTH);
		xCorrenteCursoreTextField.setEditable(false);
		yCorrenteCursoreLabel = new JLabel("Y: ");
		yCorrenteCursoreTextField = new JTextField(TEXT_FIELD_LENGTH);
		yCorrenteCursoreTextField.setEditable(false);
		clickCursoreLabel = new JLabel("Programma nuovo Click");
		clickCursoreLabel.setFont(new Font("Serif",Font.ITALIC,24));
		xClickCursoreLabel = new JLabel("X: ");	
		xClickCursoreLabel.setToolTipText("Scegli la X del punto in cui avverrà il click del mouse");
		xClickCursoreTextField = new JTextField(TEXT_FIELD_LENGTH);
		xClickCursoreTextField.setToolTipText("Scegli la X del punto in cui avverrà il click del mouse");
		xClickCursoreTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		yClickCursoreLabel = new JLabel("Y: ");
		yClickCursoreLabel.setToolTipText("Scegli la Y del punto in cui avverrà il click del mouse");
		yClickCursoreTextField = new JTextField(TEXT_FIELD_LENGTH);
		yClickCursoreTextField.setToolTipText("Scegli la Y del punto in cui avverrà il click del mouse");
		yClickCursoreTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		delayLabel = new JLabel("Delay: ");
		delayTextField = new JTextField(TEXT_FIELD_LENGTH);
		delayTextField.setText("0");
		delayTextField.setToolTipText("Inserisci un valore in secondi del ritardo con cui si azionerà il click del mouse");
		delayTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		addButton = new JButton("Add Click");
		addButton.addActionListener(this);
		addButton.setMnemonic(KeyEvent.VK_A);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		
		keyPressLabel = new JLabel("Key Press");
		keyPressLabel.setFont(new Font("Serif",Font.ITALIC,24));
		keyLabel = new JLabel("Key:");
		keyLabel.setPreferredSize(new Dimension(40,20));
		keyPressTextField = new JTextField(TEXT_FIELD_LENGTH);
		delaykeyPressLabel = new JLabel("Delay:");
		delaykeyPressLabel.setPreferredSize(new Dimension(40,20));
		delaykeyPressTextField = new JTextField(TEXT_FIELD_LENGTH);;
		delaykeyPressTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		addkeyPressButton = new JButton("Add KeyPress");
		addkeyPressButton.addActionListener(this);
		
		automatizzaLabel = new JLabel("Automatizza routine di click");
		automatizzaLabel.setFont(new Font("Serif",Font.ITALIC,24));
		intervalloTemporaleLabel = new JLabel("Scegli intervallo: ");
		intervalloTemporaleComboBox = new JComboBox<>(intervalliTemporaliArray);
		intervalloTemporaleComboBox.setToolTipText("Scegli l'intervallo temporale in minuti con cui ripetere la routine dei click");
		pathFileMappatureButton = new JButton("File Mappature");
		pathFileMappatureButton.addActionListener(this);
		pathFileMappatureChooser = new JFileChooser("./" + CONFIG_DIR );
		loadMappatureButton = new JButton("Carica Mappature");
		loadMappatureButton.addActionListener(this);
		
		startRoutineButton = new JButton("Start Routine");
		startRoutineButton.addActionListener(this);
		stopRoutineButton = new JButton("Stop Routine");
		stopRoutineButton.addActionListener(this);
		stopRoutineButton.setEnabled(false);
		textArea = new JTextArea();
		textArea.setEditable(false);
		logArea = new JScrollPane(textArea);
		logArea.setPreferredSize(new Dimension(LOG_AREA_WIDTH,LOG_AREA_HEIGHT));
		
		mainPanel.add(posizioneCorrenteCursoreLabel);
		mainPanel.add(xCorrenteCursoreLabel);
		mainPanel.add(xCorrenteCursoreTextField);
		mainPanel.add(yCorrenteCursoreLabel);
		mainPanel.add(yCorrenteCursoreTextField);		
		mainPanel.add(clickCursoreLabel);
		mainPanel.add(xClickCursoreLabel);
		mainPanel.add(xClickCursoreTextField);
		mainPanel.add(yClickCursoreLabel);
		mainPanel.add(yClickCursoreTextField);	
		mainPanel.add(delayLabel);
		mainPanel.add(delayTextField);
		mainPanel.add(addButton);		
		mainPanel.add(startButton);
		
		mainPanel.add(keyPressLabel);
		mainPanel.add(keyLabel);
		mainPanel.add(keyPressTextField);
		mainPanel.add(delaykeyPressLabel);
		mainPanel.add(delaykeyPressTextField);
		mainPanel.add(addkeyPressButton);
		
		mainPanel.add(stopRoutineButton);
		mainPanel.add(automatizzaLabel);
		mainPanel.add(intervalloTemporaleLabel);
		mainPanel.add(intervalloTemporaleComboBox);
		mainPanel.add(startRoutineButton);
		mainPanel.add(clearButton);
		mainPanel.add(pathFileMappatureButton);
		mainPanel.add(loadMappatureButton);
		mainPanel.add(logArea);
		
		AppLogger.setLogArea(textArea);
		AppLogger.setDebugMode(DebugMode.CONSOLE_GUI_FILE);
	}
	
	private void layoutComponents() {
		//Riga 1
		layout.putConstraint(SpringLayout.NORTH, posizioneCorrenteCursoreLabel, MARGIN_MEDIUM, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.WEST, posizioneCorrenteCursoreLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		//Riga 2
		layout.putConstraint(SpringLayout.NORTH, xCorrenteCursoreLabel, MARGIN_XSMALL, SpringLayout.SOUTH, posizioneCorrenteCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, xCorrenteCursoreLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, xCorrenteCursoreTextField, MARGIN_XSMALL, SpringLayout.SOUTH, posizioneCorrenteCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, xCorrenteCursoreTextField, MARGIN_MEDIUM, SpringLayout.EAST, xCorrenteCursoreLabel);
		layout.putConstraint(SpringLayout.NORTH, yCorrenteCursoreLabel, MARGIN_XSMALL, SpringLayout.SOUTH, posizioneCorrenteCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, yCorrenteCursoreLabel, MARGIN_MEDIUM, SpringLayout.EAST, xCorrenteCursoreTextField);
		layout.putConstraint(SpringLayout.NORTH, yCorrenteCursoreTextField, MARGIN_XSMALL, SpringLayout.SOUTH, posizioneCorrenteCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, yCorrenteCursoreTextField, MARGIN_MEDIUM, SpringLayout.EAST, yCorrenteCursoreLabel);
		//Riga 3
		layout.putConstraint(SpringLayout.NORTH, clickCursoreLabel, MARGIN_HUGE, SpringLayout.SOUTH, xCorrenteCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, clickCursoreLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		//Riga 4
		layout.putConstraint(SpringLayout.NORTH, xClickCursoreLabel, MARGIN_XSMALL, SpringLayout.SOUTH, clickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, xClickCursoreLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, xClickCursoreTextField, MARGIN_XSMALL, SpringLayout.SOUTH, clickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, xClickCursoreTextField, MARGIN_SMALL, SpringLayout.EAST, xClickCursoreLabel);
		layout.putConstraint(SpringLayout.NORTH, yClickCursoreLabel, MARGIN_XSMALL, SpringLayout.SOUTH, clickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, yClickCursoreLabel, MARGIN_MEDIUM, SpringLayout.EAST, xClickCursoreTextField);
		layout.putConstraint(SpringLayout.NORTH, yClickCursoreTextField, MARGIN_XSMALL, SpringLayout.SOUTH, clickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, yClickCursoreTextField, MARGIN_SMALL, SpringLayout.EAST, yClickCursoreLabel);
		layout.putConstraint(SpringLayout.NORTH, delayLabel, MARGIN_XSMALL, SpringLayout.SOUTH, clickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, delayLabel, MARGIN_MEDIUM, SpringLayout.EAST, yClickCursoreTextField);
		layout.putConstraint(SpringLayout.NORTH, delayTextField, MARGIN_XSMALL, SpringLayout.SOUTH, clickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, delayTextField, MARGIN_SMALL, SpringLayout.EAST, delayLabel);
		//Riga 5
		layout.putConstraint(SpringLayout.NORTH, addButton, MARGIN_MEDIUM, SpringLayout.SOUTH, xClickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, addButton, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);		
		layout.putConstraint(SpringLayout.NORTH, startButton, MARGIN_MEDIUM, SpringLayout.SOUTH, xClickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, startButton, MARGIN_MEDIUM, SpringLayout.EAST, addButton);
		
		//Riga 6
		layout.putConstraint(SpringLayout.NORTH, keyPressLabel, MARGIN_HUGE, SpringLayout.SOUTH, addButton);
		layout.putConstraint(SpringLayout.WEST, keyPressLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, keyLabel, MARGIN_XSMALL, SpringLayout.SOUTH, keyPressLabel);
		layout.putConstraint(SpringLayout.WEST, keyLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, keyPressTextField, MARGIN_XSMALL, SpringLayout.SOUTH, keyPressLabel);
		layout.putConstraint(SpringLayout.WEST, keyPressTextField, MARGIN_SMALL, SpringLayout.EAST, keyLabel);
		layout.putConstraint(SpringLayout.NORTH, delaykeyPressLabel, MARGIN_XSMALL, SpringLayout.SOUTH, keyPressLabel);
		layout.putConstraint(SpringLayout.WEST, delaykeyPressLabel, MARGIN_SMALL, SpringLayout.EAST, keyPressTextField);		
		layout.putConstraint(SpringLayout.NORTH, delaykeyPressTextField, MARGIN_XSMALL, SpringLayout.SOUTH, keyPressLabel);
		layout.putConstraint(SpringLayout.WEST, delaykeyPressTextField, MARGIN_SMALL, SpringLayout.EAST, delaykeyPressLabel);
		layout.putConstraint(SpringLayout.NORTH, addkeyPressButton, MARGIN_XSMALL, SpringLayout.SOUTH, keyLabel);
		layout.putConstraint(SpringLayout.WEST, addkeyPressButton, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
			
		
		//Riga 7
		layout.putConstraint(SpringLayout.NORTH, automatizzaLabel, MARGIN_HUGE, SpringLayout.SOUTH, addkeyPressButton);
		layout.putConstraint(SpringLayout.WEST, automatizzaLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, intervalloTemporaleLabel, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, intervalloTemporaleLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, intervalloTemporaleComboBox, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, intervalloTemporaleComboBox, MARGIN_XXSMALL, SpringLayout.EAST, intervalloTemporaleLabel);
		layout.putConstraint(SpringLayout.NORTH, startRoutineButton, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, startRoutineButton, MARGIN_XXSMALL, SpringLayout.EAST, intervalloTemporaleComboBox);
		layout.putConstraint(SpringLayout.NORTH, stopRoutineButton, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, stopRoutineButton, MARGIN_XXSMALL, SpringLayout.EAST, startRoutineButton);
		layout.putConstraint(SpringLayout.NORTH, clearButton, MARGIN_MEDIUM, SpringLayout.SOUTH, intervalloTemporaleLabel);
		layout.putConstraint(SpringLayout.WEST, clearButton, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, pathFileMappatureButton, MARGIN_MEDIUM, SpringLayout.SOUTH, intervalloTemporaleLabel);
		layout.putConstraint(SpringLayout.WEST, pathFileMappatureButton, MARGIN_XSMALL, SpringLayout.EAST, clearButton);
		layout.putConstraint(SpringLayout.NORTH, loadMappatureButton, MARGIN_MEDIUM, SpringLayout.SOUTH, intervalloTemporaleLabel);
		layout.putConstraint(SpringLayout.WEST, loadMappatureButton, MARGIN_XXSMALL, SpringLayout.EAST, pathFileMappatureButton);
		
		//Log Area
		layout.putConstraint(SpringLayout.NORTH, logArea, MARGIN_MEDIUM, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, logArea, -MARGIN_XXSMALL, SpringLayout.EAST, mainPanel);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==addButton) {
			String xs = xClickCursoreTextField.getText();
			String ys = yClickCursoreTextField.getText();
			String delayString = delayTextField.getText();
			try {
				int x = Integer.parseInt(xs);
				int y = Integer.parseInt(ys);
				int delay;
				if(delayString!=null && !delayString.trim().equals("")) {
					delay = Integer.parseInt(delayString);
				}else {
					delay = 0;
				}				
				MouseControllerBot.add(new PressioneMouse(new Point(x,y),delay));
				log("Click aggiunto con successo");
			}catch(NumberFormatException ecc) {
				try {					
					int x = (int)Double.parseDouble(xCorrenteCursoreTextField.getText());
					int y = (int)Double.parseDouble(yCorrenteCursoreTextField.getText());
					log("X = " + x + "\tY: " + y);
					int delay;
					if(delayString!=null && !delayString.trim().equals("")) {
						delay = Integer.parseInt(delayString);
					}else {
						delay = 0;
					}				
					MouseControllerBot.add(new PressioneMouse(new Point(x,y),delay));
					log("Click aggiunto con successo");
				}catch(NumberFormatException ecc2) {
					JOptionPane.showMessageDialog(this, "I valori inseriti per X e Y non sono validi", "Errore dati in ingresso", JOptionPane.ERROR_MESSAGE);
					log("I valori per X e Y inseriti non sono validi, dati immessi: X = " + xs + "   Y = " + ys);
				
				}
			}	
		}
		if(e.getSource()==addkeyPressButton) {
			String keyString = keyPressTextField.getText();
			int key = getKey(keyString);
			String d = delaykeyPressTextField.getText();
			int delayKeyPress;
			if(d!=null && !d.trim().equals("")) {
				delayKeyPress = Integer.parseInt(delaykeyPressTextField.getText());
			}else {
				delayKeyPress = 0;
			}	
			MouseControllerBot.add(new PressioneTasto(key,delayKeyPress));	
			log("Pressione tasto " + key + " aggiunta con successo aggiunto con successo");
		}		
		if(e.getSource()==clearButton) {
			MouseControllerBot.clear();
			textArea.setText("");
			log("Tutte le azioni registrate in memoria sono state cancellate");
		}
		if(e.getSource()==startButton) {
			String xs = xClickCursoreTextField.getText();
			String ys = yClickCursoreTextField.getText();
			try {
				int x = Integer.parseInt(xs);
				int y = Integer.parseInt(ys);
				MouseControllerBot.click(x,y);
			}catch(NumberFormatException ecc) {
				JOptionPane.showMessageDialog(this, "I valori inseriti per X e Y non sono validi", "Errore dati in ingresso", JOptionPane.ERROR_MESSAGE);
				log("I valori per X e Y inseriti non sono validi, dati immessi: X = " + xs + "   Y = " + ys);
			}
			
		}		
		if(e.getSource()==pathFileMappatureButton) {
			int result = pathFileMappatureChooser.showOpenDialog(this);
			switch(result) {
				case JFileChooser.CANCEL_OPTION:{
					log("Scelta del file delle misurazioni, operazione annullata dall'utente");
					break;
				}
				case JFileChooser.APPROVE_OPTION:{
					fileMappature = pathFileMappatureChooser.getSelectedFile().toPath().toString();
					log("File rilevazioni corrente: " + fileMappature);
					break;
				}
				case JFileChooser.ERROR_OPTION:{
					log("Errore in fase di scelta del file delle misurazioni ");
					break;
				}
			}
		}	
		if(e.getSource()==loadMappatureButton) {
			try {
				log("Caricamento mappature azioni dal file: " + Paths.get(fileMappature).toString());
				BufferedReader in = new BufferedReader(new FileReader(Paths.get(fileMappature).toString() ));
				String line = null;
				while( (line = in.readLine()) != null) {
					String[] mappature = line.split(";");
					switch(mappature[0]) {
					case "MOUSE_CLICK":{
						MouseControllerBot.add(new PressioneMouse(new Point(Integer.parseInt(mappature[1]),Integer.parseInt(mappature[2])),Integer.parseInt(mappature[3])));
						log("Add click del mouse - X: " + Integer.parseInt(mappature[1]) + "\tY: " + Integer.parseInt(mappature[2]) + "\tDelay: " + Integer.parseInt(mappature[3]));
						break;
					}
					case "KEY_PRESSED":{
						MouseControllerBot.add( new PressioneTasto(getKey(mappature[1]),Integer.parseInt(mappature[2]) ) );	
						log("Add key press - Key: " + mappature[1] + "\tDelay: " + Integer.parseInt(mappature[2]));
						break;
					}
					}
				}
				log("Caricamento da file di azioni completato");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(e.getSource()==startRoutineButton) {
			
			int intervallo = (int) intervalloTemporaleComboBox.getSelectedItem();
			scheduler = Executors.newScheduledThreadPool(1);
			serviceHandler = scheduler.scheduleAtFixedRate(()-> {
				MouseControllerBot.executeAllClicks();
				log("Eseguito task con intervallo " + intervallo);
			}, 0, intervallo, TimeUnit.MINUTES);
			startRoutineButton.setEnabled(false);
			stopRoutineButton.setEnabled(true);
			log("Tutti i click sono stati eseguiti correttamente");
		}	
		if(e.getSource()==stopRoutineButton) {
			new Thread( () -> {
				serviceHandler.cancel(true);
			    scheduler.shutdown();
			    startRoutineButton.setEnabled(true);
				stopRoutineButton.setEnabled(false);
				log("Task cancellato correttamente");
			}).start();
			
		}
		
		
		
	}


	private int getKey(String value) {
		int result = switch(value) {
		case "CHAR_UNDEFINED"	-> 65535;
		case "KEY_FIRST" ->	400;
		case "KEY_LAST"	-> 402;
		case "KEY_LOCATION_LEFT" -> 2;
		case "KEY_LOCATION_NUMPAD" -> 4;
		case "KEY_LOCATION_RIGHT" -> 3;
		case "KEY_LOCATION_STANDARD" -> 1;
		case "KEY_LOCATION_UNKNOWN" -> 0;
		case "KEY_PRESSED" -> 401;
		case "KEY_RELEASED" -> 402;
		case "KEY_TYPED" -> 400;
		case "0" -> 48;
		case "1" -> 49;
		case "2" -> 50;
		case "3" -> 51;
		case "4" -> 52;
		case "5" -> 53;
		case "6" -> 54;
		case "7" -> 55;
		case "8" -> 56;
		case "9" -> 57;
		case "A" -> 65;
		case "ACCEPT" -> 30;
		case "ADD" -> 107;
		case "AGAIN" -> 65481;
		case "ALL_CANDIDATES" -> 256;
		case "ALPHANUMERIC" -> 240;
		case "ALT" -> 18;
		case "ALT_GRAPH" -> 65406;
		case "AMPERSAND" -> 150;
		case "ASTERISK" -> 151;
		case "AT" -> 512;
		case "B" -> 66;
		case "BACK_QUOTE" -> 192;
		case "BACK_SLASH" -> 92;
		case "BACK_SPACE" -> 8;
		case "BEGIN" -> 65368;
		case "BRACELEFT" -> 161;
		case "BRACERIGHT" -> 162;
		case "C" -> 67;
		case "CANCEL" -> 3;
		case "CAPS_LOCK" -> 20;
		case "CIRCUMFLEX" -> 514;
		case "CLEAR" -> 12;
		case "CLOSE_BRACKET" -> 93;
		case "CODE_INPUT" -> 258;
		case "COLON" -> 513;
		case "COMMA" -> 44;
		case "COMPOSE" -> 65312;
		case "CONTEXT_MENU" -> 525;
		case "CONTROL" -> 17;
		case "CONVERT" -> 28;
		case "COPY" -> 65485;
		case "CUT" -> 65489;
		case "D" -> 68;
		case "DEAD_ABOVEDOT" -> 134;
		case "DEAD_ABOVERING" -> 136;
		case "DEAD_ACUTE" -> 129;
		case "DEAD_BREVE" -> 133;
		case "DEAD_CARON" -> 138;
		case "DEAD_CEDILLA" -> 139;
		case "DEAD_CIRCUMFLEX" -> 130;
		case "DEAD_DIAERESIS" -> 135;
		case "DEAD_DOUBLEACUTE" -> 137;
		case "DEAD_GRAVE" -> 128;
		case "DEAD_IOTA" -> 141;
		case "DEAD_MACRON" -> 132;
		case "DEAD_OGONEK" -> 140;
		case "DEAD_SEMIVOICED_SOUND" -> 143;
		case "DEAD_TILDE" -> 131;
		case "DEAD_VOICED_SOUND" -> 142;
		case "DECIMAL" -> 110;
		case "DELETE" -> 127;
		case "DIVIDE" -> 111;
		case "DOLLAR" -> 515;
		case "DOWN" -> 40;
		case "E" -> 69;
		case "END" -> 35;
		case "ENTER" -> 10;
		case "EQUALS" -> 61;
		case "ESCAPE" -> 27;
		case "EURO_SIGN" -> 516;
		case "EXCLAMATION_MARK" -> 517;
		case "F" -> 70;
		case "F1" -> 112;
		case "F10" -> 121;
		case "F11" -> 122;
		case "F12" -> 123;
		case "F13" -> 61440;
		case "F14" -> 61441;
		case "F15" -> 61442;
		case "F16" -> 61443;
		case "F17" -> 61444;
		case "F18" -> 61445;
		case "F19" -> 61446;
		case "F2" -> 113;
		case "F20" -> 61447;
		case "F21" -> 61448;
		case "F22" -> 61449;
		case "F23" -> 61450;
		case "F24" -> 61451;
		case "F3" -> 114;
		case "F4" -> 115;
		case "F5" -> 116;
		case "F6" -> 117;
		case "F7" -> 118;
		case "F8" -> 119;
		case "F9" -> 120;
		case "FINAL" -> 24;
		case "FIND" -> 65488;
		case "FULL_WIDTH" -> 243;
		case "G" -> 71;
		case "GREATER" -> 160;
		case "H" -> 72;
		case "HALF_WIDTH" -> 244;
		case "HELP" -> 156;
		case "HIRAGANA" -> 242;
		case "HOME" -> 36;
		case "I" -> 73;
		case "INPUT_METHOD_ON_OFF" -> 263;
		case "INSERT" -> 155;
		case "INVERTED_EXCLAMATION_MARK" -> 518;
		case "J" -> 74;
		case "JAPANESE_HIRAGANA" -> 260;
		case "JAPANESE_KATAKANA" -> 259;
		case "JAPANESE_ROMAN" -> 261;
		case "K" -> 75;
		case "KANA" -> 21;
		case "KANA_LOCK" -> 262;
		case "KANJI" -> 25;
		case "KATAKANA" -> 241;
		case "KP_DOWN" -> 225;
		case "KP_LEFT" -> 226;
		case "KP_RIGHT" -> 227;
		case "KP_UP" -> 224;
		case "L" -> 76;
		case "LEFT" -> 37;
		case "LEFT_PARENTHESIS" -> 519;
		case "LESS" -> 153;
		case "M" -> 77;
		case "META" -> 157;
		case "MINUS" -> 45;
		case "MODECHANGE" -> 31;
		case "MULTIPLY" -> 106;
		case "N" -> 78;
		case "NONCONVERT" -> 29;
		case "NUM_LOCK" -> 144;
		case "NUMBER_SIGN" -> 520;
		case "NUMPAD0" -> 96;
		case "NUMPAD1" -> 97;
		case "NUMPAD2" -> 98;
		case "NUMPAD3" -> 99;
		case "NUMPAD4" -> 100;
		case "NUMPAD5" -> 101;
		case "NUMPAD6" -> 102;
		case "NUMPAD7" -> 103;
		case "NUMPAD8" -> 104;
		case "NUMPAD9" -> 105;
		case "O" -> 79;
		case "OPEN_BRACKET" -> 91;
		case "P" -> 80;
		case "PAGE_DOWN" -> 34;
		case "PAGE_UP" -> 33;
		case "PASTE" -> 65487;
		case "PAUSE" -> 19;
		case "PERIOD" -> 46;
		case "PLUS" -> 521;
		case "PREVIOUS_CANDIDATE" -> 257;
		case "PRINTSCREEN" -> 154;
		case "PROPS" -> 65482;
		case "Q" -> 81;
		case "QUOTE" -> 222;
		case "QUOTEDBL" -> 152;
		case "R" -> 82;
		case "RIGHT" -> 39;
		case "RIGHT_PARENTHESIS" -> 522;
		case "ROMAN_CHARACTERS" -> 245;
		case "S" -> 83;
		case "SCROLL_LOCK" -> 145;
		case "SEMICOLON" -> 59;
		case "SEPARATER" -> 108;
		case "SEPARATOR" -> 108;
		case "SHIFT" -> 16;
		case "SLASH" -> 47;
		case "SPACE" -> 32;
		case "STOP" -> 65480;
		case "SUBTRACT" -> 109;
		case "T" -> 84;
		case "TAB" -> 9;
		case "U" -> 85;
		case "UNDEFINED" -> 0;
		case "UNDERSCORE" -> 523;
		case "UNDO" -> 65483;
		case "UP" -> 38;
		case "V" -> 86;
		case "W" -> 87;
		case "WINDOWS" -> 524;
		case "X" -> 88;
		case "Y" -> 89;
		case "Z" -> 90;
		default -> -1;
		};
		return result;
	}
	

}
