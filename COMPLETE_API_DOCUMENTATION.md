# SkillBridge Complete API Documentation



## Overview
Complete implementation of the Authentication API with JWT-based authentication and password reset functionality. No email verification required - users can login immediately after registration.

---

## 📋 Implemented Endpoints

### 1. **POST /api/auth/register**
**Register a new user**

**Request Body:**
```json
{
  "fullName": "John Doe",
  "rollNumber": "2023001",
  "email": "john@example.com",
  "password": "password123",
  "department": "Computer Science",
  "semester": 4,
  "bio": "A student interested in skill exchange"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "user": {
      "id": 1,
      "fullName": "John Doe",
      "rollNumber": "2023001",
      "email": "john@example.com",
      "department": "Computer Science",
      "semester": 4,
      "isVerified": true,
      "createdAt": "2024-01-15T10:30:00"
    }
  }
}
```

**Validation Rules:**
- Full name: 2-100 characters (required)
- Roll number: required, unique
- Email: valid email format (required), unique
- Password: minimum 6 characters (required)

---

### 2. **POST /api/auth/login**
**Authenticate user and get tokens**

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "fullName": "John Doe",
      "email": "john@example.com",
      "isVerified": true
    }
  }
}
```

**Requirements:**
- User must be registered
- Valid credentials required

---

### 3. **POST /api/auth/refresh-token**
**Get new access token using refresh token**

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Requirements:**
- Valid, non-expired refresh token
- Refresh token not revoked

---

### 4. **POST /api/auth/forgot-password**
**Send password reset email**

**Request Body:**
```json
{
  "email": "john@example.com"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "If the email exists, a password reset link will be sent",
  "data": null
}
```

**Security Note:** Always returns 200 OK regardless of whether email exists (prevents email enumeration)

---

### 5. **POST /api/auth/reset-password**
**Reset password with token from email**

**Request Body:**
```json
{
  "token": "uuid-token-from-email",
  "newPassword": "newPassword123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": null
}
```

**Password Requirements:**
- Minimum 6 characters
- Will be hashed before storage
---




## Overview
Complete implementation of all SkillBridge APIs including Users, Skills, Exchanges, and Analytics endpoints with full CRUD operations and advanced features.

---

##  API Endpoints Summary

### Authentication API (5 endpoints)
- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/refresh-token
- POST /api/auth/forgot-password
- POST /api/auth/reset-password

### Users API (5 endpoints)
- GET /api/users/profile
- PUT /api/users/profile
- GET /api/users/{id}
- GET /api/users/search
- GET /api/users/matches

### Skills API (4 endpoints)
- GET /api/skills
- POST /api/skills
- GET /api/skills/categories
- GET /api/skills/popular

### Exchanges API (5 endpoints)
- POST /api/exchanges/request
- GET /api/exchanges/pending
- PUT /api/exchanges/{id}/respond
- GET /api/exchanges/history
- POST /api/exchanges/{id}/rate

### Analytics API (3 endpoints)
- GET /api/analytics/skills-demand
- GET /api/analytics/user-stats
- GET /api/analytics/exchanges-trend

---

##  Users API

### 1. Get Current User's Profile
**Endpoint:** `GET /api/users/profile`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "fullName": "John Doe",
    "rollNumber": "2023001",
    "email": "john@example.com",
    "department": "Computer Science",
    "semester": 4,
    "bio": "A student interested in skill exchange",
    "profilePicUrl": "https://example.com/pic.jpg",
    "isVerified": true,
    "averageRating": 4.5,
    "reviewCount": 5,
    "completedExchanges": 3,
    "offeredSkills": [
      {
        "id": 1,
        "name": "Java",
        "category": "Programming",
        "proficiencyLevel": 4,
        "skillType": "OFFERING"
      }
    ],
    "seekingSkills": [
      {
        "id": 2,
        "name": "Python",
        "category": "Programming",
        "proficiencyLevel": 2,
        "skillType": "SEEKING"
      }
    ],
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

---

### 2. Update User Profile
**Endpoint:** `PUT /api/users/profile`

**Authentication:** Required

**Request Body:**
```json
{
  "fullName": "Jane Doe",
  "department": "Data Science",
  "semester": 5,
  "bio": "Passionate about data science and ML",
  "profilePicUrl": "https://example.com/newpic.jpg"
}
```

**Response (200 OK):**
Same as GET profile endpoint

---

### 3. Get User by ID
**Endpoint:** `GET /api/users/{id}`

**Parameters:**
- `id` (path, required): User ID

**Response (200 OK):**
Same as GET profile endpoint

**Example:**
```bash
GET /api/users/5
```

---

### 4. Search Users
**Endpoint:** `GET /api/users/search?q=query&page=0&size=10`

**Parameters:**
- `q` (query, required): Search query (name, email, or roll number)
- `page` (query, optional): Page number (default: 0)
- `size` (query, optional): Page size (default: 10)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Users found",
  "data": {
    "content": [
      {
        "id": 1,
        "fullName": "John Doe",
        "email": "john@example.com",
        "averageRating": 4.5,
        "profilePicUrl": "https://example.com/pic.jpg"
      }
    ],
    "pageable": {
      "offset": 0,
      "pageSize": 10,
      "pageNumber": 0
    },
    "totalElements": 25,
    "totalPages": 3
  }
}
```

**Example:**
```bash
GET /api/users/search?q=John&page=0&size=5
```

---

### 5. Find Skill Matches
**Endpoint:** `GET /api/users/matches?page=0&size=10`

**Authentication:** Required

**Description:** Returns users who offer the skills you're seeking.

**Parameters:**
- `page` (query, optional): Page number (default: 0)
- `size` (query, optional): Page size (default: 10)

**Response (200 OK):**
Returns paginated list of matching users (same structure as search)

**Example:**
```bash
GET /api/users/matches?page=0&size=10
```

---

##  Skills API

### 1. Get All Skills
**Endpoint:** `GET /api/skills?page=0&size=10`

**Parameters:**
- `page` (query, optional): Page number (default: 0)
- `size` (query, optional): Page size (default: 10)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Skills retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Java",
        "category": "Programming",
        "description": "Java programming language",
        "popularityScore": 95
      }
    ],
    "pageable": {
      "offset": 0,
      "pageSize": 10,
      "pageNumber": 0
    },
    "totalElements": 150,
    "totalPages": 15
  }
}
```

---

### 2. Create Skill (Admin Only)
**Endpoint:** `POST /api/skills`

**Authentication:** Required (Admin role)

**Request Body:**
```json
{
  "name": "Python",
  "category": "Programming",
  "description": "Python programming language"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Skill created successfully",
  "data": {
    "id": 1,
    "name": "Python",
    "category": "Programming",
    "description": "Python programming language",
    "popularityScore": 0
  }
}
```

---

### 3. Get Skill Categories
**Endpoint:** `GET /api/skills/categories`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Categories retrieved successfully",
  "data": [
    "Programming",
    "Design",
    "Marketing",
    "Language",
    "Business"
  ]
}
```

---

### 4. Get Popular Skills
**Endpoint:** `GET /api/skills/popular`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Popular skills retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Java",
      "category": "Programming",
      "description": "Java programming language",
      "popularityScore": 95
    },
    {
      "id": 2,
      "name": "Python",
      "category": "Programming",
      "description": "Python programming language",
      "popularityScore": 92
    }
  ]
}
```

---

## 💱 Exchanges API

### 1. Create Exchange Request
**Endpoint:** `POST /api/exchanges/request`

**Authentication:** Required

**Request Body:**
```json
{
  "providerId": 2,
  "skillOfferedId": 1,
  "skillRequestedId": 3
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Exchange request created successfully",
  "data": {
    "id": 1,
    "requester": {
      "id": 1,
      "fullName": "John Doe",
      "email": "john@example.com"
    },
    "provider": {
      "id": 2,
      "fullName": "Jane Doe",
      "email": "jane@example.com"
    },
    "skillOffered": {
      "id": 1,
      "name": "Java",
      "category": "Programming"
    },
    "skillRequested": {
      "id": 3,
      "name": "Python",
      "category": "Programming"
    },
    "status": "PENDING",
    "createdAt": "2024-01-20T10:30:00"
  }
}
```

---

### 2. Get Pending Exchanges
**Endpoint:** `GET /api/exchanges/pending?page=0&size=10`

**Authentication:** Required

**Description:** Returns pending exchange requests for the current user (as provider).

**Parameters:**
- `page` (query, optional): Page number (default: 0)
- `size` (query, optional): Page size (default: 10)

**Response (200 OK):**
Paginated list of Exchange DTOs with status = PENDING

---

### 3. Respond to Exchange Request
**Endpoint:** `PUT /api/exchanges/{id}/respond`

**Authentication:** Required

**Parameters:**
- `id` (path, required): Exchange ID

**Request Body:**
```json
{
  "status": "ACCEPTED",
  "sessionLink": "https://meet.google.com/abc-def-ghi"
}
```

**Possible Status Values:**
- `ACCEPTED` - Accept the exchange request
- `REJECTED` - Reject the exchange request
- `COMPLETED` - Mark exchange as completed

**Response (200 OK):**
Updated Exchange DTO with new status

---

### 4. Get Exchange History
**Endpoint:** `GET /api/exchanges/history?page=0&size=10`

**Authentication:** Required

**Description:** Returns completed/cancelled exchanges for the current user.

**Parameters:**
- `page` (query, optional): Page number (default: 0)
- `size` (query, optional): Page size (default: 10)

**Response (200 OK):**
Paginated list of Exchange DTOs with status = COMPLETED or CANCELLED

---

### 5. Rate Exchange
**Endpoint:** `POST /api/exchanges/{id}/rate`

**Authentication:** Required

**Parameters:**
- `id` (path, required): Exchange ID

**Request Body:**
```json
{
  "rating": 5,
  "feedback": "Great session! Learned a lot about Python."
}
```

**Response (200 OK):**
Updated Exchange DTO with rating and feedback

**Notes:**
- Only the requester can rate an exchange
- Exchange must be COMPLETED to be rated
- Rating must be between 1-5

---

##  Analytics API

### 1. Get Skills Demand
**Endpoint:** `GET /api/analytics/skills-demand`

**Authentication:** Required

**Description:** Returns skills with high demand vs supply ratio.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Skills demand retrieved successfully",
  "data": [
    {
      "skillName": "Python",
      "demandCount": 25,
      "offeringCount": 5,
      "popularityScore": 92,
      "demandRatio": 5.0
    },
    {
      "skillName": "Machine Learning",
      "demandCount": 30,
      "offeringCount": 3,
      "popularityScore": 88,
      "demandRatio": 10.0
    }
  ]
}
```

---

### 2. Get User Statistics
**Endpoint:** `GET /api/analytics/user-stats`

**Authentication:** Required

**Description:** Returns platform-wide user statistics.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User statistics retrieved successfully",
  "data": {
    "totalUsers": 250,
    "verifiedUsers": 200,
    "usersWithSkills": 180,
    "activeUsers": 95,
    "averageSkillsPerUser": 3.45,
    "averageRating": 4.32
  }
}
```

**Metrics Explanation:**
- `totalUsers`: Total registered users
- `verifiedUsers`: Users who have verified their email
- `usersWithSkills`: Users with at least one skill (offering or seeking)
- `activeUsers`: Users with exchanges in the last 30 days
- `averageSkillsPerUser`: Average number of skills per user
- `averageRating`: Platform-wide average rating (1-5)

---

### 3. Get Exchanges Trend
**Endpoint:** `GET /api/analytics/exchanges-trend`

**Authentication:** Required

**Description:** Returns distribution of exchange statuses.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Exchange trends retrieved successfully",
  "data": [
    {
      "status": "COMPLETED",
      "count": 150,
      "percentage": 50.0
    },
    {
      "status": "PENDING",
      "count": 90,
      "percentage": 30.0
    },
    {
      "status": "ACCEPTED",
      "count": 45,
      "percentage": 15.0
    },
    {
      "status": "REJECTED",
      "count": 15,
      "percentage": 5.0
    }
  ]
}
```

---

## 🛠️ Common Usage Examples

### Complete Exchange Flow
```bash
# 1. Find matching users
curl -X GET http://localhost:8080/api/users/matches \
  -H "Authorization: Bearer <token>"

# 2. Create exchange request
curl -X POST http://localhost:8080/api/exchanges/request \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "providerId": 5,
    "skillOfferedId": 1,
    "skillRequestedId": 3
  }'

# 3. Provider checks pending exchanges
curl -X GET http://localhost:8080/api/exchanges/pending \
  -H "Authorization: Bearer <token>"

# 4. Provider responds to request
curl -X PUT http://localhost:8080/api/exchanges/1/respond \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACCEPTED",
    "sessionLink": "https://meet.google.com/xyz"
  }'

# 5. After session, requester rates the exchange
curl -X POST http://localhost:8080/api/exchanges/1/rate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "rating": 5,
    "feedback": "Great experience!"
  }'

# 6. View exchange history
curl -X GET http://localhost:8080/api/exchanges/history \
  -H "Authorization: Bearer <token>"
```

---

## Security & Permissions

### Public Endpoints (No Auth Required)
- GET /api/skills
- GET /api/skills/categories
- GET /api/skills/popular
- GET /api/users/search
- GET /api/users/{id}

### Authenticated Endpoints (All Others)
- POST /api/users/* requests require valid JWT token
- All /api/exchanges/* require authentication
- All /api/analytics/* require authentication

### Admin-Only Endpoints
- POST /api/skills (create skill)

---

##  Database Schema

### Key Tables
- **users** - User accounts and profiles
- **skills** - Available skills
- **user_skills** - User's offered/seeking skills
- **exchanges** - Skill exchange requests
- **reviews** - Ratings and feedback on exchanges
- **refresh_tokens** - JWT refresh token management
- **email_verification_tokens** - Email verification
- **password_reset_tokens** - Password reset flow

---

### Using Swagger UI
Visit: `http://localhost:8080/swagger-ui.html`

---

##  Pagination

All list endpoints support pagination with query parameters:
- `page`: Zero-indexed page number (default: 0)
- `size`: Number of items per page (default: 10)

Response format includes:
```json
{
  "content": [...],
  "pageable": {
    "offset": 0,
    "pageSize": 10,
    "pageNumber": 0
  },
  "totalElements": 45,
  "totalPages": 5,
  "last": false,
  "numberOfElements": 10,
  "first": true
}
```

---

## 🐛 Error Handling

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### Common HTTP Status Codes
- 200 OK - Successful request
- 201 Created - Resource created successfully
- 400 Bad Request - Invalid input or validation error
- 401 Unauthorized - Missing or invalid authentication token
- 403 Forbidden - User doesn't have permission
- 404 Not Found - Resource not found
- 500 Internal Server Error - Server error

---
