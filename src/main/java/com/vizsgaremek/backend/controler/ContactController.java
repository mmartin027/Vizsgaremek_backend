package com.vizsgaremek.backend.controler;

import com.vizsgaremek.backend.DTO.MailBodyDTO;
import com.vizsgaremek.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> sendContactMessage(@RequestBody Map<String, String> form) {
        String name = form.getOrDefault("name", "Névtelen");
        String email = form.getOrDefault("email", "nincs email");
        String phone = form.getOrDefault("phone", "nincs telefon");
        String subject = form.getOrDefault("subject", "general");
        String bookingId = form.getOrDefault("bookingId", "");
        String message = form.getOrDefault("message", "");

        String body = "Új kapcsolat üzenet érkezett!\n\n"
                + "Név: " + name + "\n"
                + "Email: " + email + "\n"
                + "Telefon: " + phone + "\n"
                + "Tárgy: " + subject + "\n"
                + "Foglalási azonosító: " + bookingId + "\n\n"
                + "Üzenet:\n" + message;

        MailBodyDTO mailBody = new MailBodyDTO(
                "martinmalaj307@gmail.com",
                "ParkEasy Kapcsolat - " + subject,
                body
        );
        emailService.sendSimpleMessage(mailBody);

        return ResponseEntity.ok(Map.of("success", true));
    }
}