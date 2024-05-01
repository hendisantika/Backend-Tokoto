package com.apiecommerce.tokoto.email;

public interface EmailService {

    /**
     *
     * @param details untuk menampung email
     * @return
     */
    String sendSimpleMail(EmailDetails details);

    /**
     *
     * @param details untuk menampung email
     * @return
     */
    String sendMailWithAttachment(EmailDetails details);
}
