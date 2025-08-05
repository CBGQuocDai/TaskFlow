package com.backend.ToDoList.utils;

import com.backend.ToDoList.errors.AppException;
import com.backend.ToDoList.enums.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtils {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
        Context context = new Context();
        context.setVariable("otp", body );
        String content = templateEngine.process("email", context );
        try {
            message.setFrom("noreply@taskflow1.click", "Task Flow 1");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
    }
}
