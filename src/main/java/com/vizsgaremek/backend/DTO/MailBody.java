package com.vizsgaremek.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

public record MailBody (String to,String subject,String text){
}
