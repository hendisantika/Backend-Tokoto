# API Product Specivication v1.6.0

## Create Product :

- Endpoint : `/api/products/create`
- Method : `POST`
- Request Header : `Content-Type: Multipart/form-data`
- JWT Token : `Authorization: Bearer {token}`
- Request Body : `Multipart/form-data`
- Response Body : `JSON`

### Request Body :

```json
{
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "file": "Multipart/form-data",
  "rating": 5.0
}
```

### Response Body (Success) :

`201 Created!`

```json
{
  "id": "UUID-String",
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "imageName": "Hashed file name",
  "filePath": "http://localhost:8000/images/{imageName}",
  "rating": 5.0
}
```

### Response Body (Error / Forbidden) :

`400 Bad Request, 401 Unauthorized, 403 Forbidden`

```json
{
  "error": "Bad Request / Unauthorized / Forbidden"
}
```

## Update Product :

- Endpoint : `/api/products/update`
- Method : `PATCH`
- Request Header : `Content-Type: Multipart/form-data`
- JWT Token : `Authorization: Bearer {token}`
- Request Body : `Multipart/form-data`
- Response Body : `JSON`

### Request Body :

```json
{
  "id": "UUID-String",
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "file": "Multipart/form-data",
  "rating": 5.0
}
```

### Response Body (Success) :

`200 OK!`

```json
{
  "id": "UUID-String",
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "imageName": "Hashed file name",
  "filePath": "http://localhost:8000/images/{imageName}",
  "rating": 5.0
}
```

### Response Body (Error / Forbidden) :

`400 Bad Request, 401 Unauthorized, 403 Forbidden`

```json
{
  "error": "Bad Request / Unauthorized / Forbidden"
}
```

## Delete Product :

- Endpoint : `/api/products/delete`
- Method : `DELETE`
- Request Header : `Content-Type: Multipart/form-data`
- JWT Token : `Authorization: Bearer {token}`
- Response Body : `JSON`

### Response Body (Success):

`204 No Content!`

```json
{
  "success": "No Content"
}
```

### Response Body (Error / Forbidden / Not Found) :

`400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found`

```json
{
  "error": "Bad Request / Unauthorized / Forbidden / Not Found"
}
```

## Get Product By ID :

- Endpoint : `GET /api/products/find/byId/details?id={id}`
- Response Body : `JSON`

### Response Body (Success):

`200 OK!`

```json
{
  "id": "UUID-String",
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "imageName": "Hashed file name",
  "filePath": "http://localhost:8000/images/{imageName}",
  "rating": 5.0
}
```

### Response Body (Error / Forbidden / Not Found) :

`400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found`

```json
{
  "error": "Bad Request / Unauthorized / Forbidden / Not Found"
}
```

## Get Product By Price :

- Endpoint : `GET /api/products/find/byPrice/details?price={price}`
- Response Body : `JSON`
- Example : `GET /api/products/find/byPrice/details?price=10.000`

### Response Body (Success):

`200 OK!`

```json
{
  "id": "UUID-String",
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "imageName": "Hashed file name",
  "filePath": "http://localhost:8000/images/{imageName}",
  "rating": 5.0
}
```

### Response Body (Error Not Found) :

`404 Not Found`

```json
{
  "error": "Not Found"
}
```

## Get Product By Title :

- Endpoint : `GET /api/products/find/byTitle/details?title={title}`
- Response Body : `JSON`
- Example : `GET /api/products/find/byTitle/details?title=burger`

### Response Body (Success):

`200 OK!`

```json
{
  "id": "UUID-String",
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "imageName": "Hashed file name",
  "filePath": "http://localhost:8000/images/{imageName}",
  "rating": 5.0
}
```

### Response Body (Error / Forbidden / Not Found) :

`400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found`

```json
{
  "error": "Bad Request / Unauthorized / Forbidden / Not Found"
}
```

### Get All Product :

- Endpoint : `GET /api/products/list`
- Response Body : `JSON`

### Response Body (Success):

`200 OK!`

```json
{
  "content": [
    {
      "id": "UUID-String",
      "title": "Burger",
      "units": ["Pcs", "Pack"],
      "categories": ["Makanan Berat", "Makanan Ringan"],
      "price": 10.0,
      "qty": 20,
      "description": "Burger adalah makanan yang bergizi tinggi",
      "details": {
        "alat": "string",
        "bahan": "string"
      },
      "imageName": "Hashed file name",
      "filePath": "http://localhost:8000/images/{imageName}",
      "rating": 5.0
    }
  ],
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "empty": false,
  "pageable": {
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    }
  }
}
```

### Response Body (Error Not Found) :

`404 Not Found`

```json
{
  "error": "Not Found"
}
```

## Request Product By Import CSV :

- Endpoint : `http://192.168.1.4:8000/api/products/import`
- Method : `POST`
- Request Header : `Content-Type : Multipart/form-data`
- JWT Token : `Authorization: Bearer {token}`
- Request Body : `Multipart/form-data`
- Response Body : `JSON`

### Request Body :

```json
{
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "file": "Multipart/form-data",
  "rating": 5.0
}
```

### Response Body (Success) :

`201 Created`

```json
{
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "file": "Multipart/form-data",
  "rating": 5.0
}
```

### Response Body (Error) :

`403 Forbidden || 404 Bad Request`

```json
{
  "error": "Forbidden || Bad Request"
}
```

## Request Product By Export CSV :

- Endpoint : `http://192.168.1.4:8000/api/products/export`
- Method : `POST`
- Request Header : `Content-Type : Multipart/form-data`
- JWT Token : `Authorization: Bearer {token}`
- Response Body : `JSON`

### Response Body (Success) :

`201 Created`

```json
{
  "title": "Burger",
  "units": ["Pcs", "Pack"],
  "categories": ["Makanan Berat", "Makanan Ringan"],
  "price": 10.0,
  "qty": 20,
  "description": "Burger adalah makanan yang bergizi tinggi",
  "details": "Details barang",
  "file": "Multipart/form-data",
  "rating": 5.0
}
```

### Response Body (Error) :

`403 Forbidden || 404 Not Found`

```json
{
  "error": "Forbidden || Not Found"
}
```
