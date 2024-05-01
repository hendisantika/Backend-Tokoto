package com.apiecommerce.tokoto.message;

import com.apiecommerce.tokoto.config.RSAEncryptConverter;
import com.apiecommerce.tokoto.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "messages")
@Builder
public class Message {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false, length = 36, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recepient_id")
    private User recipient;

    @Convert(converter = RSAEncryptConverter.class)
    private String content;

    @Column(nullable = true, name = "attachment_name")
    private String attachmentName;

    @Column(nullable = true, name = "attachment_data")
    private byte[] attachmentData;

    @Column(nullable = true, name = "attachment_type")
    private String attachmentType;

    @Column(nullable = true, name = "attachment_path")
    private String pathAttachment;

    private LocalDateTime createdAt;
}
