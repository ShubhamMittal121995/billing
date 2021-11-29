package com.main.Billing.utility;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class EmailHelper {
	
	public void sendPDFToClient(Map<String,String> inputMap, Stage stage) {
		//Stage piStage = new Stage();
		try {
//			ProgressIndicator PI = new ProgressIndicator();
//			StackPane root = new StackPane();
//			root.getChildren().add(PI);
//			Scene scene = new Scene(root,300,200); 
//			piStage.setScene(scene);
//			piStage.initStyle(StageStyle.UNDECORATED);
//			piStage.show();
			
			String fromEmail = inputMap.get("fromEmail");
			String fromPassword = inputMap.get("fromPassword");
			String toClientAddress = inputMap.get("toClientAddress");
			String fromDate = inputMap.get("fromDate");
			String toDate = inputMap.get("toDate");
			String filePath = inputMap.get("filePath");
			
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.port", "587");
			
			String domain = fromEmail.substring(fromEmail.indexOf('@') + 1);
			if(domain.contains("yahoo")) {
				props.put("mail.smtp.host", "smtp.mail.yahoo.com");
			} else if (domain.contains("gmail")){
				props.put("mail.smtp.host", "smtp.gmail.com");
			}
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, fromPassword);
				}
			});

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(toClientAddress, false));

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toClientAddress));
			msg.setSubject("Bill Generated");
			msg.setContent("Bill Generated", "text/html");
			msg.setSentDate(new Date());

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent("Dear customer,<br>Your bill is generated for from "+fromDate+" to "+toDate+". <br>Please find an attachement. <br><br><br><br><br>Thanks & Regards, <br>M/s Shaheed Filling Station.", "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			MimeBodyPart attachPart = new MimeBodyPart();
			attachPart.attachFile(new File(filePath));
			multipart.addBodyPart(attachPart);
			msg.setContent(multipart);
			Transport.send(msg);
			//piStage.close();
			if(stage != null)
				stage.close();
			Utility.getAlert("Success", "", "Email Sent Successfully", null, AlertType.INFORMATION);
		}catch (Exception e) {
			Utility.getAlert("Error", "Error occured in sending mail", e.getMessage(), null, AlertType.ERROR);
		} finally {
			//piStage.close();
		}
	}	
}
