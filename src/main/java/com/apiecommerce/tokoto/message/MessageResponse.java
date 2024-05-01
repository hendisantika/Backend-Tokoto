package com.apiecommerce.tokoto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    private String id;
    private String sender;
    private String recepient;
    private String content;
    private String attachmentName;
    private String pathAttachment;
    private LocalDateTime createdAt;
}
