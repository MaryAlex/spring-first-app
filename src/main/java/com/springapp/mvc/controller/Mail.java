package com.springapp.mvc.controller;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.springapp.mvc.config.MailConfig;
import com.springapp.mvc.model.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class Mail {

	public static void generateAndSendEmail(User user) throws MessagingException {

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(MailConfig.class);
		ctx.refresh();
		JavaMailSenderImpl mailSender = ctx.getBean(JavaMailSenderImpl.class);
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage);
		mailMsg.setFrom("nimachka1@gmail.com");
		mailMsg.setTo(user.getEmail());
		mailMsg.setSubject("Registration");
		mailMsg.setText("<a href='http://localhost:8080/issecret?secret="+ user.getSecret() + "'>Click me</a>", true);
		mailSender.send(mimeMessage);
	}
}
