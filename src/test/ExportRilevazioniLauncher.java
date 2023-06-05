package test;

import javax.swing.SwingUtilities;

import view.ExportRilevazioniGUI;

public class ExportRilevazioniLauncher {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> new ExportRilevazioniGUI());
	}

}
