package io.projetocoletarsu.service;

import io.projetocoletarsu.model.Usuario;
import org.springframework.mail.SimpleMailMessage;

import java.util.Date;

public abstract class AbstractEmailService implements EmailService {

    //@Value("${default.sender}")
    private String sender = "gabriellgomesbh@gmail.com";

    @Override
    public void enviarSenhaUsuario(Usuario usuario) {
        SimpleMailMessage sm = preparaEmailRecuperarSenha(usuario);
        enviarEmail(sm);
    }


    protected SimpleMailMessage preparaEmailRecuperarSenha(Usuario usuario) {
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setTo(usuario.getEmail());
        sm.setFrom(sender);
        sm.setSubject("Sistema Coleta Residuos - Recuperacao de Senha");
        sm.setSentDate(new Date(System.currentTimeMillis()));
        sm.setText("Olá querido parceiro,\n" +
                "Aqui está sua senha atual: " + usuario.getSenha() + ". Em breve teremos a função de trocar de senha, recomendamos que você execute este passo para \n" +
                "maior segurança! (= \n" +
                "\n" +
                "\n" +
                "Obrigado,\n" +
                "\n" +
                "Equipe RECICLART." );

        return sm;
    }

}
