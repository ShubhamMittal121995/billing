package com.main.Billing;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {
	
	public static void main(String args[]) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
	}
}
