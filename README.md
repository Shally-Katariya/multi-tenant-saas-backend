# 🧠 Multi-Tenant SaaS Backend

> **Secure · Scalable · Production-Oriented**

A production-grade backend that solves the core SaaS challenge:
**How can multiple organizations share one system — without ever accessing each other's data?**

---

## 🚀 Why This Project?

Most backend projects stop at CRUD.  
This one goes further — enforcing **tenant isolation at the ORM level**, so no manual filtering is ever needed.

✅ No data leakage  
✅ No manual filtering errors  
✅ No insecure assumptions  

---

## 🏗️ Architecture Overview

**Single Application · Shared Database · Strong Isolation**

Each request carries full identity and tenant context through the entire stack.

```
Client → JWT → JwtFilter → SecurityContext
                    ↓
              TenantContext (ThreadLocal)
                    ↓
            Hibernate Filter (auto WHERE clause)
                    ↓
               PostgreSQL
```

---

## 🔄 Request Lifecycle

1. User logs in → receives a signed **JWT**
2. JWT contains: `username`, `tenantId`, `role`
3. Every request:
   - Token is validated
   - Tenant is extracted
   - Role is assigned
4. Hibernate automatically appends:
```sql
   WHERE tenant_id = currentTenant
```

👉 **Zero manual filtering. Isolation enforced at ORM level.**

---

## 🏢 Multi-Tenancy Strategy

| Strategy | Tradeoff |
|---|---|
| Separate DB | Expensive, hard to scale |
| Separate Schema | Moderate complexity |
| **Shared Schema** ✅ | Efficient and scalable |

**Chosen:** Shared Database · Shared Schema · Row-level isolation

---

## 🔐 Security Design

### ❌ Problem
Using headers like `X-Tenant-ID` → **can be spoofed**

### ✅ Solution
Tenant is extracted from the **signed JWT** — tamper-proof by design.

```json
{
  "sub": "username",
  "tenantId": "company1",
  "role": "ADMIN"
}
```

---

## 🧩 Low-Level Design

### `JwtFilter`
- Intercepts every request
- Extracts and validates JWT
- Sets `Authentication` + `TenantContext`

### `TenantContext` (ThreadLocal)
- Stores tenant ID per request thread
- Prevents cross-request leakage

### Hibernate Filter
- Auto-modifies all queries:
```sql
  SELECT * FROM users WHERE tenant_id = ?
```
- Eliminates human error entirely

### RBAC Layer
```java
@PreAuthorize("hasRole('ADMIN')")
```

| Role | Permissions |
|---|---|
| `ADMIN` | Create + Read |
| `USER` | Read Only |

---

## 📌 API Reference

### 🔐 Authentication
```
POST /api/auth/login
```

### 👤 Users
```
POST /api/users   →  ADMIN only
GET  /api/users   →  ADMIN, USER
```

---

## 🧪 Testing Guide

**Step 1 — Login**
```json
{
  "username": "user",
  "tenantId": "company1"
}
```

**Step 2 — Use the token**
```
Authorization: Bearer <token>
```

**Step 3 — Validate isolation**

| Test | Expected |
|---|---|
| USER creates user | ❌ 403 Forbidden |
| ADMIN creates user | ✅ 201 Created |
| Cross-tenant access | ❌ Blocked |

---

## ⚠️ Problems Solved

| Problem | Solution |
|---|---|
| Manual tenant filtering errors | Hibernate auto-filter |
| Cross-tenant data leakage | Row-level isolation |
| Header-based spoofing | JWT-based tenant extraction |
| Session scaling limitations | Stateless JWT auth |

---

## 🛠️ Tech Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=JSON%20web%20tokens&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)

- **Java + Spring Boot** — Core framework
- **Spring Security** — Auth & RBAC
- **Hibernate (JPA)** — ORM-level isolation
- **PostgreSQL** — Relational database
- **JWT** — Stateless authentication
- **Docker** — Containerization

---

## 🚀 Future Enhancements

- [ ] Refresh token support
- [ ] Redis caching layer
- [ ] Rate limiting
- [ ] Multi-schema tenancy option
- [ ] Microservices architecture

---

## 🎤 Key Learnings

> Multi-tenancy is an **architectural challenge**, not just a coding task.

- Automation beats manual enforcement — always
- Stateless systems scale better
- ORM-level isolation is more reliable than application-level filtering

---

## 👩‍💻 Author

**Shally Katariya**  
B.Tech CSE (Cybersecurity)
