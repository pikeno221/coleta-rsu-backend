package io.projetocoletarsu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SmtpEmailService extends AbstractEmailService {


    @Autowired
    private MailSender mailSender;

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    @Override
    public void enviarEmail(SimpleMailMessage msg) {
        try {
            log.info("Enviando de email...");
            mailSender.send(msg);
            log.info("Email enviado");
        } catch (Exception e) {
            System.out.print(e);

        }


    }
}