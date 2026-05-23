# ⚡ API Rate Limiter & Request Monitoring System

A **production-style backend system** built with Java + Spring Boot + MySQL that protects API endpoints from abuse by enforcing per-token request limits, logging all traffic, and providing a real-time monitoring dashboard.

> Inspired by how real-world APIs like Stripe, GitHub, and Twitter protect their infrastructure.

---

## 🚀 Features

- **Token-based Authentication** — Users register and receive a unique API token
- **Configurable Rate Limits** — Set max requests per minute per user (default: 10/min)
- **HTTP 429 Enforcement** — Returns standard `Too Many Requests` response on limit exceeded
- **Complete Request Logging** — Every request logged as `ALLOWED` or `BLOCKED` with timestamp and endpoint
- **Real-time Dashboard** — Live stats showing total, allowed, and blocked requests per token
- **Admin Log View** — Full system-wide request history across all tokens

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8 |
| API Style | REST |
| Frontend | HTML / CSS / Vanilla JS |
| Build Tool | Maven |

---

## 📁 Project Structure

```
src/main/java/com/nupur/ratelimiter/
├── model/
│   ├── ApiUser.java          # User entity with token and rate limit config
│   └── RequestLog.java       # Log entity for every incoming request
├── repository/
│   ├── ApiUserRepository.java
│   └── RequestLogRepository.java
├── service/
│   └── RateLimiterService.java   # Core rate limiting logic
├── controller/
│   ├── AuthController.java       # Registration endpoint
│   └── ApiController.java        # Protected endpoints + stats
src/main/resources/
├── static/
│   └── index.html            # Monitoring dashboard
└── application.properties    # DB config + rate limit settings
```

---

## ⚙️ How to Run Locally

### Prerequisites
- Java 17+
- Maven 3.x
- MySQL 8

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/api-rate-limiter.git
cd api-rate-limiter
```

**2. Set up the database**
```sql
CREATE DATABASE ratelimiter_db;
```

**3. Configure your credentials**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ratelimiter_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

**4. Run the application**
```bash
mvn spring-boot:run
```

**5. Open the dashboard**
```
http://localhost:8080/index.html
```

---

## 📬 API Endpoints

### Register a user
```http
POST /auth/register
Content-Type: application/json

{ "username": "nupur" }
```
**Response:**
```json
{
  "message": "User registered successfully",
  "username": "nupur",
  "token": "a1b2c3d4-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
}
```

---

### Call a protected endpoint
```http
GET /api/data
X-API-TOKEN: your-token-here
```
**Response (within limit):**
```json
{ "message": "Success! Here is your data.", "status": 200 }
```
**Response (limit exceeded):**
```json
{
  "error": "Rate limit exceeded",
  "message": "You have exceeded your request limit. Please wait and try again.",
  "status": 429
}
```

---

### View your request stats
```http
GET /api/stats
X-API-TOKEN: your-token-here
```
**Response:**
```json
{
  "username": "nupur",
  "requestLimit": 10,
  "totalRequests": 15,
  "allowedRequests": 10,
  "blockedRequests": 5,
  "recentLogs": [ ... ]
}
```

---

### Admin — all system logs
```http
GET /api/admin/logs
```

---

## 🧠 How the Rate Limiting Works

The core algorithm uses a **fixed window counter** approach:

1. A request arrives with an API token in the header
2. The system looks up the user associated with that token
3. It counts how many requests that token made in the **last N minutes** (configurable)
4. If `count >= limit` → respond with **HTTP 429**, log as `BLOCKED`
5. If `count < limit` → process the request, log as `ALLOWED`
6. Every request is persisted to MySQL regardless of outcome

```
Request In
    │
    ▼
Valid Token? ──No──▶ 401 Unauthorized
    │
   Yes
    │
    ▼
Count requests in last 1 min
    │
    ├── count >= limit ──▶ Log BLOCKED ──▶ 429 Too Many Requests
    │
    └── count < limit  ──▶ Log ALLOWED ──▶ 200 OK + Response Data
```

---

## 📈 Future Improvements

- **Redis integration** — In-memory request counting for high-throughput scenarios
- **Sliding window algorithm** — More accurate than fixed window at boundary conditions
- **Per-endpoint limits** — Different limits for different routes
- **JWT authentication** — Replace simple token with signed JWT
- **Docker support** — Containerize the app + MySQL with docker-compose

---

## 👩‍💻 Author

**Nupur Patwardhan**
B.E. Information Technology | PCCOE, Pune (2027 Batch)
📧 patwardhannupur1@gmail.com
