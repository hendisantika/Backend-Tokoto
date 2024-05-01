# API Order Specification v1.6.2

## Menambahkan Product ke dalam Daftar List Order:

- Method : `POST`
- Endpoint : `/api/products/order/checkout/{productId}`
- Authoriztion : `Token : {Bearer} - Mandatory`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "quantity_order": 5
}
```

### Response Body (Success):
`200 OK`
```json
{
  "id": "integer unique",
  "product": "name-product",
  "price": 10.000,
  "quantity_order": 5,
  "totalPrice": "price * quantity_order",
  "user_order": "user_order by token"
}
```

### Response Body (Error):
`404 Not Found || 403 Forbidden || 401 Unauthorization`

```json
{
  "message": "Tidak dapat menemukan product yang anda inginkan || Token Auth telah expired, Harap lakukan Log-In ulang || Diharapkan pengguna wajib Log-In terlebih dahulu untuk melakukan Pemesanan"
}
```

## Konfirmasi Pemesanan (Order):

- Method : `POST`
- Endpoint : `/api/products/checkout/{productId}/confirmed`
- Authorization : `Token : {Bearer} - Mandatory`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`

```json
{
  "message": "Success confirmation your order!"
}
```

### Response Body (Failed):
`403 Forbidden`

```json
{
  "message": "Token telah expired, harap lakukan login sekali lagi!"
}
```