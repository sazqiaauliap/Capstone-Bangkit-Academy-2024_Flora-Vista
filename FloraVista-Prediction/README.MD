
# Plant, Disease, and Soil Prediction API

This API allows users to upload images to predict plant types, diseases, and soil types using machine learning models.

## Endpoints

### 1. Predict Plant Type

**URL:** `/predict/plant`

**Method:** `POST`

**Description:** This endpoint predicts the type of plant based on the uploaded image.

**Request:**

- **Content-Type:** `multipart/form-data`
- **Parameters:**
  - `image`: The image file of the plant.

**Response:**

- **Success (200):**

  ```json
  {
    "message": "Prediksi tanaman berhasil.",
    "data": {
      "hasil": "APPLE",
      "skorKepercayaan": 95.0,
      "isAboveThreshold": true,
      "Family": "Rosaceae",
      "Description": "Buah manis yang dapat dimakan yang dihasilkan oleh pohon apel.",
      "Benefit": [
        "1. Tinggi serat",
        "2. Kaya akan vitamin C",
        "3. Membantu mengatur gula darah"
      ]
    }
  }

## Error Responses

### Failure (400)
```json
{
  "error": "Data gambar tidak ditemukan"
}
```
### Failure (500)
```json
{
  "error": "File model tanaman tidak ditemukan di model/model_plant_classification.h5"
}
```
##
### 2. Predict Plant Disease

**URL:** `/predict/disease`

**Method:** `POST`

**Description:** This endpoint predicts the type of plant disease based on the uploaded image.

**Request:**

- **Content-Type:** `multipart/form-data`
- **Parameters:**
  - `image`: The image file of the diseased plant.

**Response:**

- **Success (200):**

  ```json
  {
    "message": "Prediksi penyakit tanaman berhasil.",
  "data": {
    "hasil": "BLACK ROT",
    "skorKepercayaan": 92.0,
    "isAboveThreshold": true,
    "Description": "Penyakit yang disebabkan oleh jamur pada tanaman cruciferous, biasanya terlihat sebagai bercak-bercak hitam pada daun.",
    "Treatment": "Pengendalian dengan menjaga kebersihan, menggunakan bibit yang bebas penyakit, dan menghindari kondisi lembab."
    }
  }
  ```
## Error Responses

### Failure (400)
```json
{
  "error": "Data gambar tidak ditemukan"
}
```
### Failure (500)
```json
{
  "error": "File model penyakit tidak ditemukan di model/model_disease_full.h5"
}
```
##
### 3. Predict Soil Type

**URL:** `/predict/soil`

**Method:** `POST`

**Description:** This endpoint predicts the type of soil based on the uploaded image.

**Request:**

- **Content-Type:** `multipart/form-data`
- **Parameters:**
  - `image`: The image file of the soil.

**Response:**

- **Success (200):**

  ```json
  {
  "message": "Prediksi jenis tanah berhasil.",
  "data": {
    "hasil": "ALUVIAL",
    "skorKepercayaan": 90.0,
    "isAboveThreshold": true,
    "Description": "Tanah aluvial merupakan tanah yang terbentuk dari endapan sungai atau aliran air. Umumnya sangat subur dan kaya akan unsur hara.",
    "Suitable_Plants": [
      "Padi",
      "Jagung",
      "Sayuran berdaun hijau seperti bayam dan kangkung"
    ]
    }
  }
  ```
## Error Responses

### Failure (400)
```json
{
  "error": "Data gambar tidak ditemukan"
}
```
### Failure (500)
```json
{
  "error": "File model tanah tidak ditemukan di model/model_soil_classification.h5"
}
```
##

### Running the API
To run the API, use the following command:
```bash
python3 app.py
```
