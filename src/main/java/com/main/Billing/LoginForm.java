package com.main.Billing;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.main.Billing.utility.AuthenticationUser;
import com.main.Billing.utility.PreAuthenticator;
import com.sun.javafx.application.LauncherImpl;

@SuppressWarnings({ "serial", "restriction" })
public class LoginForm extends JFrame implements ActionListener {

	private JLabel labelUsername = new JLabel("Enter username: ");
	private JLabel labelPassword = new JLabel("Enter password: ");
	private JTextField textUsername = new JTextField(50);
	private JPasswordField fieldPassword = new JPasswordField(50);
	private JButton buttonLogin = new JButton("Login");
	
	public LoginForm() {
		super("Login to Billing Portal");

		// create a new panel with GridBagLayout manager
		JPanel newPanel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 10, 10, 10);

		// add components to the panel
		constraints.gridx = 0;
		constraints.gridy = 0;
		newPanel.add(labelUsername, constraints);

		constraints.gridx = 1;
		newPanel.add(textUsername, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		newPanel.add(labelPassword, constraints);

		constraints.gridx = 1;
		newPanel.add(fieldPassword, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		newPanel.add(buttonLogin, constraints);

		newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Login Panel"));

		add(newPanel);

		buttonLogin.addActionListener(this);

		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (PreAuthenticator.checkApplicationRunning()) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(null, "Application Already Running...");
			return;
		} else {
			try {
				if (!PreAuthenticator.validateAuthorizedSystem()) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null, "System not authorized.. Please Contact with Admin...");
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		String userValue = textUsername.getText();
		String passValue = fieldPassword.getText();
		if (AuthenticationUser.validatingUser(userValue, passValue)) {
			setVisible(false);
			dispose();
			LauncherImpl.launchApplication(App.class, PreLoader.class, null);
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(null, "User is not authenticated...");
		}
	}
}
