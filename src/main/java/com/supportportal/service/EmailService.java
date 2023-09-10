package com.supportportal.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.supportportal.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.TO;
import static javax.mail.Message.RecipientType.CC;

@Service
public class EmailService {

    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createEmail(firstName, password, email);
        Session session = getEmailSession();
        SMTPTransport smtpTransport = (SMTPTransport) session.getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }


    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message msg = new MimeMessage(getEmailSession());
        msg.setFrom(new InternetAddress(FROM_EMAIL));
        msg.setRecipients(TO, InternetAddress.parse(email, false));
        msg.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        msg.setSubject(EMAIL_SUBJECT);
        msg.setText("Hello " + firstName + ", \n \n Your new account password is: " + password + "\n \n The Support Team.");
        msg.setSentDate(new Date());
        msg.saveChanges();
        return msg;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }

}
