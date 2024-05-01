# API Gender Specivication v1.6.0

## Introduction

- API Gender Specification
- V1.0.0

## Create New Gender:

- Endpoint : `http://192.168.1.4:8000/api/v1/genders`
- Method : `POST`
- Authentication : `JWT Auth Bearer`
- Enumeration : `Gender Type [Laki-laki | Perempuan]`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "name": "Laki-laki"
}
```

### Response Body (Success):

`201 Created`

```json
{
  "id": 1,
  "name": "Laki-laki"
}
```

### Response Body (Error):

`400 Bad Request`

```json
{
  "message": "Bad Request"
}
```

## Get All Gender

- Endpoint : `http://192.168.1.4:8000/api/v1/genders/list`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):

`200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "name": "Laki-laki"
    },
    {
      "id": 2,
      "name": "Perempuan"
    }
  ]
}
```

### Response Body (Error):

`400 Bad Request`

```json
{
  "message": "Bad Request"
}
```
