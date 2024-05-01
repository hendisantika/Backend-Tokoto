package com.apiecommerce.tokoto.message;

import com.apiecommerce.tokoto.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Value("${host}")
    private String HOST;

    @PostMapping("/send/{emailPenerima}")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable String emailPenerima,
            @RequestBody SendMessageRequest request
            ) {
        MessageResponse response = messageService.sendMessage(emailPenerima, request.getIsiPesan());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/sendWithAttachment/{emailPenerima}")
    public ResponseEntity<MessageResponse> sendMessageWithAttachment(
            @PathVariable String emailPenerima,
            @ModelAttribute SendMessageRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        MessageResponse response = messageService.sendMessageWithAttachment(emailPenerima, request.getIsiPesan(), file);
        String imageName = response.getAttachmentName();
        String imageUrl = "http://" + HOST + ":8000/api/images/" + imageName;
        response.setPathAttachment(imageUrl);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
