package com.shifz.wordbird.utils;

import com.shifz.wordbird.database.Connection;
import com.shifz.wordbird.database.Preference;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


/**
 * Created by theapache64 on 7/12/16.
 */
public class MailHelper {

    private static final String KEY_GMAIL_USERNAME = "gmail_username";
    private static final String KEY_GMAIL_PASSWORD = "gmail_password";
    private static final String KEY_ADMIN_EMAIL = "admin_email";

    private static String gmailUsername, gmailPassword;


    public static boolean sendMail(String email, final String subject, String message) {


        if (gmailUsername == null || gmailPassword == null) {
            final Preference preference = Preference.getInstance();

            gmailUsername = preference.getString(KEY_GMAIL_USERNAME);
            gmailPassword = preference.getString(KEY_GMAIL_PASSWORD);
        }

        final Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmailUsername, gmailPassword);
            }
        });

        Message mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(gmailUsername));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return false;
    }


}

