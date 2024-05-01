# API Categories Specivication v1.6.0

## Create new Categories

- Endpoint : `http://192.168.1.4:8000/api/categories`
- Method : `POST`
- Request Header : `JWT Auth Bearer {token}`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body

```json
{
  "nameCategory": "Makanan Ringan"
}
```

### Response Body (Success):

`201 Created`

```json
{
  "id": "uuid-string",
  "nameCategory": "Makanan Ringan"
}
```

### Response Body (Error):

`400 Bad Request`

```json
{
  "error": "Bad Request"
}
```

## Update Categories Name

- Endpoint : `http://192.168.1.4:8000/api/categories/update/{id}`
- Method : `PATCH`
- Request Header : `JWT Auth Bearer {token}`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body

```json
{
  "nameCategory": "Makanan Berat"
}
```

### Response Body (Success):

`200 OK`

```json
{
  "id": "uuid-string",
  "nameCategory": "Makanan Berat"
}
```

### Response Body (Error):

`400 Bad Request`

```json
{
  "error": "Bad Request"
}
```

## Delete Categories

- Endpoint : `http://192.168.1.4:8000/api/categories/delete/{id}`
- Method : `DELETE`
- Request Header : `JWT Auth Bearer {token}`
- Response Body : `JSON`

### Response Body (Success):

`201 No Content`

### Response Body (Error):

`404 Not Found | 403 Forbidden`

## Get Categories By Id

- Endpoint : `http://192.168.1.4:8000/api/categories/get/{id}`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):

`200 OK`

```json
{
  "id": "uuid-string",
  "nameCategory": "Makanan Ringan"
}
```

### Response Body (Error):

`404 Not Found || 403 Forbidden`

```json
{
  "error": "Not Found || Forbidden"
}
```

## Get List All Categories

- Endpoint : `http://192.168.1.4:8000/api/categories/all`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):

`200 OK`

```json
{
  "content": [
    {
      "id": "uuid-string",
      "nameCategory": "Makanan Ringan"
    },
    {
      "id": "uuid-string",
      "nameCategory": "Makanan Berat"
    }
  ]
}
```
