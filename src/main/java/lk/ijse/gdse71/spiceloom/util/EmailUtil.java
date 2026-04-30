package lk.ijse.gdse71.spiceloom.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("SpiceLoom - Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\nThis OTP is valid for 5 minutes.");
        mailSender.send(message);
    }
}