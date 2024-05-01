# FEATURE SUPPLY PRODUCT API SPEC v.1.6.2

## Update Supply:

- Method : `POST`
- Endpoint : `http://192.168.1.4:8000/api/products/supply/{productId}`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Supply Product:

```json
{
  "quantity": 10,
  "supplyFrom": "Dearly Febriano",
  "type": ["IN", "OUT"]
}
```

### Response Supply Product (Success):

`201 Created`

```json
{
  "id": "uuid-string unique",
  "product": "Italian Pizza",
  "quantity": 10,
  "supplyFrom": "Dearly Febriano",
  "type": ["IN", "OUT"],
  "supplyDate": "2024-01-23"
}
```

### Response Supply Product (Failed):

`403 Forbidden || 402 Bad Request`

```json
{
  "message": "Invalid Input Request Supply Product"
}
```

## Remove Quanity Product:

- Method : `POST`
- Endpoint : `http://192.168.1.4:8000/api/products/supply/pay/{productId}`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body

```json
{
  "quantity": 10,
  "supplyFrom": "Dearly Febriano",
  "type": ["IN", "OUT"]
}
```

### Response Body (Success):

`201 Created`

```json
{
  "id": "uuid-string unique",
  "product": "Italian Pizza",
  "quantity": 10,
  "supplyFrom": "Dearly Febriano",
  "type": ["IN", "OUT"],
  "supplyDate": "2024-01-23"
}
```

### Response Remove Quantity Product (Failed):

`403 Forbidden || 402 Bad Request`

```json
{
  "message": "Invalid Input Request Supply Product"
}
```

## Get All Supply Products

- Method : `GET`
- Endpoint : `http://192.168.1.4:8000/api/products/supply/all`
- Response Body : `JSON`

### Response Body (Success):

`200 OK!`

```json
{
  "content": [
    {
      "id": "uuid-string unique",
      "product": "Italian Pizza",
      "quantity": 10,
      "supplyFrom": "Dearly Febriano",
      "type": ["IN", "OUT"],
      "supplyDate": "2024-02-20"
    },
    {
      "id": "uuid-string unique",
      "product": "Pizza Mozarela",
      "quantity": 7,
      "supplyFrom": "Arvan Gibran",
      "type": ["IN", "OUT"],
      "supplyDate": "2024-01-23"
    }
  ]
}
```

### Response Body (Failed):

`403 Forbidden || 402 Bad Request`

```json
{
  "message": "Invalid Input Request Supply Product"
}
```
