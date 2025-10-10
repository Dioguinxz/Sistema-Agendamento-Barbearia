package com.example.SistemaBarbearia.service;

import com.example.SistemaBarbearia.entity.Agendamento;
import com.example.SistemaBarbearia.entity.Usuario;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Envia um e-mail de boas-vindas para um novo usuário.
     *
     * @param novoUsuario O objeto do usuário recém-criado.
     */
    @Async
    public void enviarEmailBoasVindas(Usuario novoUsuario) {
        logger.info("Iniciando processo de envio de e-mail de boas-vindas para o usuário: {}", novoUsuario.getEmail());
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmail);
            helper.setTo(novoUsuario.getEmail());
            helper.setSubject("Bem-vindo à Barbearia!");

            String corpoEmail = montarCorpoHtmlBoasVindas(novoUsuario);
            helper.setText(corpoEmail, true);

            mailSender.send(mimeMessage);
            logger.info("E-mail de boas-vindas enviado com sucesso para: {}", novoUsuario.getEmail());

        } catch (Exception e) {
            logger.error("Falha ao enviar e-mail de boas-vindas para {}: {}", novoUsuario.getEmail(), e.getMessage(), e);
        }
    }

    /**
     * Envia um e-mail de confirmação em HTML para o cliente e uma notificação para o barbeiro.
     * O método é assíncrono, rodando em uma thread separada para não bloquear a resposta da API.
     *
     * @param agendamento   O objeto do agendamento recém-criado.
     * @param emailCliente  O e-mail do cliente que agendou.
     * @param emailBarbeiro O e-mail do barbeiro que foi agendado.
     */
    @Async
    public void enviarEmailConfirmacaoAgendamento(Agendamento agendamento, String emailCliente, String emailBarbeiro) {
        logger.info("Iniciando processo de envio de e-mail de confirmação para o agendamento ID: {}", agendamento.getId());
        try {
            //Envio para o Cliente
            logger.debug("Preparando e-mail de confirmação para o cliente: {}", emailCliente);
            MimeMessage mimeMessageCliente = mailSender.createMimeMessage();
            MimeMessageHelper helperCliente = new MimeMessageHelper(mimeMessageCliente, "utf-8");

            helperCliente.setFrom(fromEmail);
            helperCliente.setTo(emailCliente);
            helperCliente.setSubject("✅ Agendamento Confirmado na Barbearia!");

            String corpoEmailCliente = montarCorpoHtmlConfirmacaoCliente(agendamento);
            helperCliente.setText(corpoEmailCliente, true);

            mailSender.send(mimeMessageCliente);
            logger.info("E-mail de confirmação enviado com sucesso para o cliente: {}", emailCliente);

            //Envio para o Barbeiro
            logger.debug("Preparando e-mail de notificação para o barbeiro: {}", emailBarbeiro);
            MimeMessage mimeMessageBarbeiro = mailSender.createMimeMessage();
            MimeMessageHelper helperBarbeiro = new MimeMessageHelper(mimeMessageBarbeiro, "utf-8");

            helperBarbeiro.setFrom(fromEmail);
            helperBarbeiro.setTo(emailBarbeiro);
            helperBarbeiro.setSubject("🗓️ Novo Agendamento Recebido!");

            String corpoEmailBarbeiro = montarCorpoHtmlNotificacaoBarbeiro(agendamento);
            helperBarbeiro.setText(corpoEmailBarbeiro, true);

            mailSender.send(mimeMessageBarbeiro);
            logger.info("E-mail de notificação enviado com sucesso para o barbeiro: {}", emailBarbeiro);

        } catch (Exception e) {
            // Registra o erro com detalhes do agendamento para facilitar a depuração
            logger.error("Falha ao enviar e-mails de confirmação para o agendamento ID {}: {}", agendamento.getId(), e.getMessage(), e);
        }
    }


    private String montarCorpoHtmlBoasVindas(Usuario usuario) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;700&display=swap');"
                + "body { font-family: 'Poppins', sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 20px auto; background-color: #1a1a1a; color: #eeeeee; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.2); }"
                + ".header { background-color: #2c2c2c; padding: 25px 20px; text-align: center; font-size: 24px; font-weight: bold; color: #ffffff; }"
                + ".content { padding: 30px; text-align: center; }"
                + ".content p { line-height: 1.6; font-size: 16px; }"
                + ".cta-button { display: inline-block; background-color: #f4f4f4; color: #1a1a1a; padding: 12px 25px; margin-top: 20px; text-decoration: none; font-weight: bold; border-radius: 5px; }"
                + ".footer { padding: 20px; text-align: center; font-size: 12px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>Seja Bem-vindo!</div>"
                + "<div class='content'>"
                + "<p>Olá, <strong>" + usuario.getNome() + "</strong>!</p>"
                + "<p>Sua conta foi criada com sucesso. Estamos felizes em ter você conosco.</p>"
                + "<p>Seu e-mail de acesso é: <strong>" + usuario.getEmail() + "</strong></p>"
                + "<a href='#' class='cta-button'>Agendar Meu Horário</a>"
                + "</div>"
                + "<div class='footer'>Barbearia</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    private String montarCorpoHtmlConfirmacaoCliente(Agendamento agendamento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataHoraFormatada = agendamento.getHorario().format(formatter);

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;700&display=swap');"
                + "body { font-family: 'Poppins', sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 20px auto; background-color: #1a1a1a; color: #eeeeee; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.2); }"
                + ".header { background-color: #2c2c2c; padding: 25px 20px; text-align: center; font-size: 24px; font-weight: bold; color: #ffffff; }"
                + ".content { padding: 30px; }"
                + ".content p { line-height: 1.6; font-size: 16px; }"
                + ".details { background-color: #252525; padding: 20px; margin-top: 20px; border-radius: 5px; border-left: 5px solid #555; }"
                + ".details p { margin: 10px 0; }"
                + ".details strong { color: #ffffff; }"
                + ".footer { padding: 20px; text-align: center; font-size: 12px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>Agendamento Confirmado!</div>"
                + "<div class='content'>"
                + "<p>Olá, <strong>" + agendamento.getNomeCliente() + "</strong>!</p>"
                + "<p>Seu agendamento em nossa barbearia foi confirmado com sucesso. Abaixo estão os detalhes:</p>"
                + "<div class='details'>"
                + "<p><strong>Serviço:</strong> " + agendamento.getTipoServico() + "</p>"
                + "<p><strong>Barbeiro:</strong> " + agendamento.getNomeBarbeiro() + "</p>"
                + "<p><strong>Data e Hora:</strong> " + dataHoraFormatada + "</p>"
                + "</div>"
                + "<p style='margin-top: 30px;'>Caso precise remarcar ou cancelar, por favor, acesse nosso sistema ou entre em contato.</p>"
                + "<p>Até breve!</p>"
                + "</div>"
                + "<div class='footer'>Barbearia</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    private String montarCorpoHtmlNotificacaoBarbeiro(Agendamento agendamento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataHoraFormatada = agendamento.getHorario().format(formatter);

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;700&display=swap');"
                + "body { font-family: 'Poppins', sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }"
                + ".header { background-color: #2c2c2c; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; }"
                + ".content { padding: 30px; }"
                + ".content p { line-height: 1.6; font-size: 16px; }"
                + ".details { background-color: #f9f9f9; padding: 20px; margin-top: 20px; border-left: 5px solid #444; }"
                + ".details p { margin: 10px 0; font-size: 16px; }"
                + ".details strong { color: #2c2c2c; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>🗓️ Novo Agendamento</div>"
                + "<div class='content'>"
                + "<p>Olá, <strong>" + agendamento.getNomeBarbeiro() + "</strong>,</p>"
                + "<p>Você recebeu um novo agendamento na sua agenda. Confira os detalhes:</p>"
                + "<div class='details'>"
                + "<p><strong>Cliente:</strong> " + agendamento.getNomeCliente() + "</p>"
                + "<p><strong>Serviço:</strong> " + agendamento.getTipoServico() + "</p>"
                + "<p><strong>Data e Hora:</strong> " + dataHoraFormatada + "</p>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}