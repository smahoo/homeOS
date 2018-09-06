package de.smahoo.homeos.automation;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class SendEmailAction extends SendAction{

	private static final String SMTP_AUTH_USER = "mail@bumble-online.de";	
	private static final String SMTP_AUTH_PWD = "antares";
	private static final String SMTP_HOST_NAME = "smtp.1und1.de";
	private static final String FROM_MAIL_ADDRESS = "alarmservice@smahoo.de";
	
	String toAddress = null;
	String subject = null;
	String message = null;
	
	public SendEmailAction(String address, String subject, String message){
		toAddress = address;
		this.subject = subject;
		this.message = message;
	}
	
	@Override
	public void onAction() throws Exception {
		sendEmail();		
	}

		
	protected void sendEmail(){
		
		try {
			Properties props = System.getProperties();
		
			props.put("mail.smtp.host",SMTP_HOST_NAME);
			//props.put( "mail.smtp.user" ,SMTP_AUTH_USER );
			
			// Use TLS
		    props.put("mail.smtp.auth" , "true" );
		    props.put("mail.smtp.starttls.enable" , "true" );
		  //  props.put("mail.smtp.password" , SMTP_AUTH_PWD );
		    Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getDefaultInstance(props,auth);
			
			Message msg = new MimeMessage(session);
		
		
			msg.setFrom(new InternetAddress(FROM_MAIL_ADDRESS));
			msg.setRecipient(RecipientType.TO, new InternetAddress(toAddress));
			msg.setSubject(subject);
			msg.setText(message);
			msg.setHeader("X-Mailer","smahoo Gateway Message");
			msg.setSentDate(new Date());
			
			Transport.send(msg);
			
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator { 
		public PasswordAuthentication getPasswordAuthentication() { 
			String username = SMTP_AUTH_USER; 
			String password = SMTP_AUTH_PWD; 
			
			return new PasswordAuthentication(username, password); 
		} 
	}
	
}
