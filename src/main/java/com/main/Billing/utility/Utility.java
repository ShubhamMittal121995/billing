package com.main.Billing.utility;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.main.Billing.HibernateUtil;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class Utility {
	private static Logger log = LoggerFactory.getLogger(Utility.class);
	public static Stage piStage = new Stage();
	public static JWindow window = null;
	
	public static Alert getAlert(String title, String header, String content, Stage ownerWindow, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		if (header != null) {
			alert.setHeaderText(header);
		}
		alert.setContentText(content);
		if (ownerWindow != null) {
			alert.initOwner(ownerWindow);
		}
		alert.showAndWait();
		return alert;
	}

	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	public static void setDateFormat(DatePicker value) {
		value.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			@Override
			public String toString(LocalDate localDate) {
				if (localDate == null)
					return "";
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String dateString) {
				if (dateString == null || dateString.trim().isEmpty()) {
					return null;
				}
				return LocalDate.parse(dateString, dateTimeFormatter);
			}
		});
	}

	public static String getFormattedDate(Date value) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		return sdf.format(value);
	}

	public void startProgressIndicator() {
		ProgressIndicator PI = new ProgressIndicator();
		StackPane root = new StackPane();
		root.getChildren().add(PI);
		Scene scene = new Scene(root, 300, 200);
		piStage.setScene(scene);
		piStage.initStyle(StageStyle.TRANSPARENT);
		piStage.show();
	}
	
	public static void showLoader() {
		Icon imgIcon = new ImageIcon(Utility.class.getClass().getResource("/loader.gif"));
		window = new JWindow();
	    window.getContentPane().add(new JLabel(imgIcon, SwingConstants.CENTER));
	    window.setBounds(500, 200, 300, 300);
	    window.setBackground(new Color(0,0,0,0));
	    window.setVisible(true);
	}
	
	public static void closeLoader() {
		window.setVisible(false);
		window.dispose();
	}

	public static boolean createBackupToSql() throws Exception {
		try {
			Connection con = HibernateUtil.getConnection();
			Statement stmt = con.createStatement();
			
			StringBuilder sb = new StringBuilder("SCRIPT ");
			if(PropertyUtil.BACKUP_PATH != null) {
				sb.append(" TO '").append(PropertyUtil.BACKUP_PATH).append("' ");
			}
			sb.append(" COMPRESSION LZF ");
			sb.append(" CIPHER ").append(PropertyUtil.CIPHER).append(" PASSWORD '").append(PropertyUtil.CIPHER_PWD).append("'");
			
			log.info("Backup Query : " + sb.toString());
			return stmt.execute(sb.toString());
		} catch (Exception e) {
			log.error("Error caught while creating a back up ", e);
			throw new Exception(e);
		}
	}

	public static boolean restoreBackupToDB(String path) throws Exception {
		try {
			Connection con = HibernateUtil.getConnection();
			//Before Restoring Drop All Objects from DB
			con.prepareStatement("DROP ALL OBJECTS").execute();
			
			//Now Restore 
			StringBuilder sb = new StringBuilder("RUNSCRIPT FROM ");
			sb.append("'").append(path).append("' COMPRESSION LZF CIPHER ").append(PropertyUtil.CIPHER).append(" PASSWORD '").append(PropertyUtil.CIPHER_PWD).append("'");
			log.info("Restore Query : " + sb.toString());
			con.prepareStatement(sb.toString()).execute();
			return true;
		} catch (Exception e) {
			log.error("Error caught while restoring a backup ", e);
			throw new Exception(e);
		}
	}
	
	public static void displayPDF(String filePath) {
		if (Desktop.isDesktopSupported()) {
			try {
				File file = new File(filePath);
				if(file.exists()) {
					Desktop.getDesktop().open(file);
				} else {
					getAlert("Error", "File Not Found", "File is not located at location", null, AlertType.ERROR);
				}
			} catch (Exception ex) {
				log.error("Error caught while displaying PDF ", ex);
			}
		}
	}
	
}
