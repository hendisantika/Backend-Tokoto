# API Mail Sender Specivication v1.6.0

## Configuration Your Email Admin In File C:\management_inventaris/src/main/resources/application.properties

## Send Mail No Attachment To Admin:

- Endpoint : `http://192.168.1.4:8000/sendMail`
- Method : `POST`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "recipient": "recipient",
  "msgBody": "message",
  "subject": "subject"
}
```

### Response Body (Success):

`Mail sent successfully!!`

### Response Body (Error):

`Error while sending mail`

## Send Mail With Attachment To Admin:

- Endpoint : `http://192.168.1.4:8000/sendMailWithAttachment`
- Method : `POST`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "recipient": "recipient",
  "msgBody": "message",
  "subject": "subject",
  "attachment": "attachment"
}
```

### Response Body (Success):

`Mail sent successfully!!`

### Response Body (Error):

`Error while sending mail`
