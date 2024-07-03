# FloraVista Login & Register API

Welcome to the FloraVista Authentication API. This API provides endpoints for user registration and login.

## Endpoints

### 1. Register

- **URL:** `https://capstone-project-flora-vista.et.r.appspot.com/register`
- **Method:** `POST`
- **Description:** This endpoint allows a new user to register.

#### Request Body:
```json
{
  "name": "varchar",
  "email": "test@example.com",
  "password": "varchar"
}
```
**Response:**
- **Success:**
```json
{
  "message": "User registered successfully"
}
```
- **Error:**
```json
{
    "statusCode": 400,
    "error": "Bad Request",
    "message": "Invalid request payload input"
}
```

### 2. Login

- **URL:** `https://capstone-project-flora-vista.et.r.appspot.com/login`
- **Method:** `POST`
- **Description:** This endpoint allows a registered user to log in.

#### Request Body:
```json
{
    "email": "test@example.com",
    "password": "varchar"
}
```

**Response:**
- **Success:**
```json
{
    "user": {
        "id": number,
        "name": "varchar",
        "email": "test@example.com",
        "created_at": "date-time"
    },
    "message": "Login successful",
    "token": "random token"
}
```
- **Error:**
```json
{
    "message": "Invalid email or password"
}
```




