# 📁 File Service API

The **File Service** handles product file storage using **MinIO** and provides endpoints for uploading, previewing, and downloading files.

It is part of the **Microservice Grid** ecosystem and works through the **API Gateway** with authentication via **Keycloak**.

---

## 🛠️ Endpoints Overview

### 1️⃣ Upload Product Image

Uploads an image for a product identified by SKU.

**Request**
```
POST /api/v1/files/upload/product/{sku}
```

*Path Parameter*

| Name | Type   | Description    |
|------|--------|----------------|
| sku  | String | Product SKU    |

**Form Data**

| Name | Type          | Description      |
|------|---------------|----------------|
| file | MultipartFile | Image file to upload |

**Response**
``` json 
    {
      "objectName": "string",   // Full path in MinIO
      "contentType": "string",  // MIME type of the file
      "size": 12345,            // File size in bytes
      "presignedUrl": "string"  // Temporary URL for direct access (valid 1 hour)
    }
```
**Notes**
  -  Use the returned objectName for preview or download.    
  -  Endpoints are secured via Keycloak JWT tokens.    
  -  Works through the API Gateway.

### 2️⃣ Preview Product Image
Generates a presigned URL to preview a product image.
**Request**
```
GET /api/v1/files/preview
```
*Query Parameter*

| Name       | Type   | Description                                      |
|------------|--------|--------------------------------------------------|
| objectName | String | Full object path in MinIO (from upload response) |

**Response**
```json 
200 OK
string   // Presigned URL valid for 1 hour
```

**Notes**
 - Use the exact objectName returned from the upload endpoint.
 - Endpoints are secured via Keycloak JWT tokens.
 - Works through the API Gateway.

 ### 3️⃣ Download Product Image
Download a product image as a file attachment.
 
**Request**
```
GET /api/v1/files/download
```
*Query Parameter*

| Name         | Type   | Description                      |
|--------------|--------|----------------------------------|
| objectName   | String | Full path in MinIO (from upload) |
	
**Response**
Returns file as attachment
````
 Headers
Content-Disposition: attachment; filename="filename.jpg"
Content-Type: application/octet-stream
````
**Notes**
- Use the exact objectName returned by the upload endpoint.
- Endpoints are secured via Keycloak JWT tokens.
- Works through the API Gateway.
- Presigned URLs are valid for 1 hour.
---

## 🔐 Security
- Authentication: Keycloak (OAuth2 / OpenID Connect)

- Authorization: Role-based access (ADMIN / CLIENT)

- Service-to-service: Client credentials flow
---
## 🚀 Running the Project

The **File Service** is part of the **Microservice Grid** and runs together with other microservices using
**Docker Compose**.

### 1️⃣ Clone the Repository

```sh
git clone https://github.com/Andrij72/MicroserviceGrid.git

# Go to the project root
cd MicroserviceGrid
````
### 2️⃣ Start all Microservices
```sh
docker-compose -f docker-compose.orchestrator.yml up -d
```
    |  This will launch all microservices including File Service, Product Service, Order Service, Inventory Service, and others.
    |  The services communicate through the API Gateway and authentication is handled via Keycloak.
---

## 🛠 Technology Stack

- Java 21 / Spring Boot 3
- Spring WebFlux (Reactive)
- MinIO for object storage
- MySQL for File Service metadata
- Keycloak for authentication/authorization
- Spring Security / OAuth2
- Docker / Docker Compose`
---

## 👨‍💻 Author
Andrii Kulynch

📅 Version: 2.0

