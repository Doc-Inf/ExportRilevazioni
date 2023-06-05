package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.MouseControllerBot;
import view.AppLogger.DebugMode;
import static view.AppLogger.*;

public class ExportRilevazioniGUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1607003797146656821L;
	private static final int FRAME_WIDTH = 500;
	private static final int FRAME_HEIGHT = 750;
	private static final int MARGIN_HUGE = 65;
	private static final int MARGIN_MEDIUM = 30;
	private static final int MARGIN_SMALL = 20;
	private static final int MARGIN_XSMALL = 15;
	private static final int MARGIN_XXSMALL = 10;
	private static final int TEXT_FIELD_LENGTH = 5;
	private static final int LOG_AREA_WIDTH = FRAME_WIDTH-80;
	private static final int LOG_AREA_HEIGHT = 300;
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
	private JLabel automatizzaLabel;
	private JLabel intervalloTemporaleLabel;
	private Integer[] intervalliTemporaliArray = {1, 5, 10, 15, 30, 60 };
	private JComboBox<Integer> intervalloTemporaleComboBox;
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
		yClickCursoreLabel = new JLabel("Y: ");
		yClickCursoreLabel.setToolTipText("Scegli la Y del punto in cui avverrà il click del mouse");
		yClickCursoreTextField = new JTextField(TEXT_FIELD_LENGTH);
		yClickCursoreTextField.setToolTipText("Scegli la Y del punto in cui avverrà il click del mouse");
		delayLabel = new JLabel("Delay: ");
		delayTextField = new JTextField(TEXT_FIELD_LENGTH);
		delayTextField.setText("0");
		delayTextField.setToolTipText("Inserisci un valore in secondi del ritardo con cui si azionerà il click del mouse");
		addButton = new JButton("Add");
		addButton.addActionListener(this);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		automatizzaLabel = new JLabel("Automatizza routine di click");
		automatizzaLabel.setFont(new Font("Serif",Font.ITALIC,24));
		intervalloTemporaleLabel = new JLabel("Scegli intervallo: ");
		intervalloTemporaleComboBox = new JComboBox<>(intervalliTemporaliArray);
		intervalloTemporaleComboBox.setToolTipText("Scegli l'intervallo temporale in minuti con cui ripetere la routine dei click");
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
		mainPanel.add(clearButton);
		mainPanel.add(startButton);		
		mainPanel.add(stopRoutineButton);
		mainPanel.add(automatizzaLabel);
		mainPanel.add(intervalloTemporaleLabel);
		mainPanel.add(intervalloTemporaleComboBox);
		mainPanel.add(startRoutineButton);
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
		layout.putConstraint(SpringLayout.NORTH, clickCursoreLabel, MARGIN_HUGE, SpringLayout.NORTH, xCorrenteCursoreLabel);
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
		layout.putConstraint(SpringLayout.NORTH, clearButton, MARGIN_MEDIUM, SpringLayout.SOUTH, xClickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, clearButton, MARGIN_MEDIUM, SpringLayout.EAST, addButton);
		layout.putConstraint(SpringLayout.NORTH, startButton, MARGIN_MEDIUM, SpringLayout.SOUTH, xClickCursoreLabel);
		layout.putConstraint(SpringLayout.WEST, startButton, MARGIN_MEDIUM, SpringLayout.EAST, clearButton);
		//Riga 6
		layout.putConstraint(SpringLayout.NORTH, automatizzaLabel, MARGIN_HUGE, SpringLayout.NORTH, addButton);
		layout.putConstraint(SpringLayout.WEST, automatizzaLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, intervalloTemporaleLabel, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, intervalloTemporaleLabel, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.NORTH, intervalloTemporaleComboBox, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, intervalloTemporaleComboBox, MARGIN_XXSMALL, SpringLayout.EAST, intervalloTemporaleLabel);
		layout.putConstraint(SpringLayout.NORTH, startRoutineButton, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, startRoutineButton, MARGIN_XXSMALL, SpringLayout.EAST, intervalloTemporaleComboBox);
		layout.putConstraint(SpringLayout.NORTH, stopRoutineButton, MARGIN_XSMALL, SpringLayout.SOUTH, automatizzaLabel);
		layout.putConstraint(SpringLayout.WEST, stopRoutineButton, MARGIN_XXSMALL, SpringLayout.EAST, startRoutineButton);
		
		//Riga 7
		layout.putConstraint(SpringLayout.NORTH, logArea, MARGIN_MEDIUM, SpringLayout.SOUTH, intervalloTemporaleLabel);
		layout.putConstraint(SpringLayout.WEST, logArea, MARGIN_MEDIUM, SpringLayout.WEST, mainPanel);
		
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
				MouseControllerBot.add(new Point(x,y),delay);
				log("Click aggiunto con successo");
			}catch(NumberFormatException ecc) {
				JOptionPane.showMessageDialog(this, "I valori inseriti per X e Y non sono validi", "Errore dati in ingresso", JOptionPane.ERROR_MESSAGE);
				log("I valori per X e Y inseriti non sono validi, dati immessi: X = " + xs + "   Y = " + ys);
			}	
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


	
	

}
