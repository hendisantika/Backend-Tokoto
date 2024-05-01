# API FETCH IMAGE USERS SPECIFICATION v.1.6.0

## Find Profile User

- Endpoint: `http://192.168.1.4:8000/api/images/{imageName}`
- Method: `GET`
- Response Body: `?`

### Response Body (Sucess) Save User Profile:

![Profile Users Set](https://th.bing.com/th/id/OIP.OgZg7lASs1cUJ_SByYzR5gAAAA?w=158&h=180&c=7&r=0&o=5&pid=1.7)

### Response Body (Success) Default Profile:

![Profile Users Default](https://th.bing.com/th/id/OIP.6UhgwprABi3-dz8Qs85FvwHaHa?rs=1&pid=ImgDetMain)

### Response Body (Error):

`404 Not Found || 401 Bad Request`

```json
{
  "error": "Gambar {imageName} tidak dapat ditemukan || Ekstensi Wajib Input Hanya PNG, png, JPG, jpg, JPEG, jpeg, webp"
}
```
