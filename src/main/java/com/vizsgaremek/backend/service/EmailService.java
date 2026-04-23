package com.vizsgaremek.backend.service;

import com.vizsgaremek.backend.DTO.MailBodyDTO;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private final String senderEmail = "noreply@netparkolo.hu";

    public void sendSimpleMessage(MailBodyDTO mailBody) {
        Resend resend = new Resend(resendApiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(senderEmail)
                .to(mailBody.to())
                .subject(mailBody.subject())
                .html("<h3>NetParkoló - Jelszó visszaállítás</h3><p>" + mailBody.text() + "</p>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email sikeresen elküldve a Resenddel! ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Kritikus hiba az email küldésekor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}