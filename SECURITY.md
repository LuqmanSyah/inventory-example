# Security Guidelines - Inventory Management System

## 🔒 Menyimpan Credentials dengan Aman

### ⚠️ JANGAN PERNAH commit credentials ke GitHub!

File-file yang **TIDAK BOLEH** di-commit:
- ❌ `application.properties` dengan password asli
- ❌ `.env` file
- ❌ `application-local.properties`
- ❌ `application-dev.properties`
- ❌ File apapun yang berisi password, API keys, tokens

---

## 📋 Best Practices untuk GitHub

### 1. Gunakan Environment Variables

File `application.properties` sudah diupdate untuk menggunakan environment variables:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/inventory_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
```

### 2. Buat File Local (Tidak di-commit)

**Opsi A: Menggunakan `.env` file**

Buat file `.env` di root project (sudah ada di `.gitignore`):

```bash
DB_URL=jdbc:postgresql://localhost:5432/inventory_db
DB_USERNAME=postgres
DB_PASSWORD=your_real_password
SERVER_PORT=8080
```

**Opsi B: Menggunakan `application-local.properties`**

Buat file `src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=postgres
spring.datasource.password=your_real_password
```

Lalu jalankan dengan:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 3. Gunakan Template Files

✅ **COMMIT** file-file ini ke GitHub:
- `application-example.properties` - Template tanpa password asli
- `env.example` - Template environment variables
- `.gitignore` - Sudah dikonfigurasi untuk exclude sensitive files

### 4. Update README dengan Instruksi Setup

Tambahkan di README.md:

```markdown
## Setup

1. Copy `env.example` ke `.env`:
   ```bash
   cp env.example .env
   ```

2. Edit `.env` dan isi dengan credentials Anda:
   ```bash
   DB_PASSWORD=your_real_password
   ```

3. Jalankan aplikasi:
   ```bash
   ./mvnw spring-boot:run
   ```
```

---

## 🛠️ Cara Menggunakan Environment Variables

### Di Development (Local)

**Windows:**
```cmd
set DB_URL=jdbc:postgresql://localhost:5432/inventory_db
set DB_USERNAME=postgres
set DB_PASSWORD=your_password
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/inventory_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
./mvnw spring-boot:run
```

**Atau gunakan .env file dengan plugin:**

Tambahkan ke `pom.xml`:
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <environmentVariables>
            <DB_URL>jdbc:postgresql://localhost:5432/inventory_db</DB_URL>
            <DB_USERNAME>postgres</DB_USERNAME>
            <DB_PASSWORD>your_password</DB_PASSWORD>
        </environmentVariables>
    </configuration>
</plugin>
```

### Di Production

**1. Heroku:**
```bash
heroku config:set DB_URL=your_production_db_url
heroku config:set DB_USERNAME=your_username
heroku config:set DB_PASSWORD=your_password
```

**2. AWS:**
- Gunakan AWS Secrets Manager
- Atau set di Environment Variables di Elastic Beanstalk

**3. Docker:**
```bash
docker run -e DB_URL=... -e DB_USERNAME=... -e DB_PASSWORD=... your-image
```

**4. Kubernetes:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  db-password: <base64-encoded-password>
```

---

## 📦 Sebelum Push ke GitHub

### Checklist:

- [ ] Pastikan `.gitignore` sudah benar
- [ ] Hapus password dari `application.properties`
- [ ] Gunakan environment variables `${VAR_NAME:default_value}`
- [ ] Commit file template: `application-example.properties`, `env.example`
- [ ] Test dengan credentials dari environment variables
- [ ] Review semua file yang akan di-commit: `git status`
- [ ] Cek tidak ada credentials: `git diff`

### Perintah Git:

```bash
# 1. Cek status
git status

# 2. Review perubahan
git diff

# 3. Add files (JANGAN gunakan git add .)
git add src/
git add pom.xml
git add README.md
git add .gitignore
git add env.example
# etc...

# 4. Commit
git commit -m "Initial commit"

# 5. Push
git push origin main
```

### ⚠️ Jika Sudah Terlanjur Commit Password

Jika sudah terlanjur commit password ke GitHub:

**1. Hapus dari history:**
```bash
# Install BFG Repo Cleaner
# https://rtyley.github.io/bfg-repo-cleaner/

# Hapus file dari history
bfg --delete-files application.properties

# Atau replace passwords
bfg --replace-text passwords.txt

# Force push
git reflog expire --expire=now --all
git gc --prune=now --aggressive
git push --force
```

**2. Ganti semua passwords:**
- Ganti password database Anda
- Revoke semua API keys
- Update credentials di semua environments

**3. Report ke GitHub:**
- Jika sudah public, consider credentials compromised

---

## 🔐 Frontend Security

### API Configuration

File `frontend/js/config.js` berisi:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

**Untuk production:**

```javascript
const API_BASE_URL = process.env.API_URL || 'https://api.yourapp.com';
```

**Atau buat config berbeda per environment:**

```javascript
const environments = {
  development: 'http://localhost:8080/api',
  production: 'https://api.yourapp.com',
  staging: 'https://staging-api.yourapp.com'
};

const API_BASE_URL = environments[process.env.NODE_ENV] || environments.development;
```

---

## 🛡️ Additional Security Recommendations

### 1. Aktifkan HTTPS di Production
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_PASSWORD}
```

### 2. Gunakan Strong Passwords
- Minimal 16 karakter
- Kombinasi huruf besar, kecil, angka, simbol
- Gunakan password manager

### 3. Database Security
```properties
# Read-only user untuk reporting
spring.datasource.hikari.read-only=false

# Connection pool limits
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 4. Aktifkan Security Headers
Tambahkan di `SecurityConfig.java`:
```java
http
    .headers()
    .contentSecurityPolicy("default-src 'self'")
    .and()
    .xssProtection()
    .and()
    .frameOptions().deny();
```

### 5. Rate Limiting
Gunakan Spring Security atau nginx untuk rate limiting.

### 6. Audit Logging
Log semua operasi sensitif:
```java
@Slf4j
public class AuditService {
    public void logAction(String user, String action) {
        log.info("User: {}, Action: {}", user, action);
    }
}
```

---

## 📚 Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [GitHub Security Best Practices](https://docs.github.com/en/code-security)
- [Git Secrets Tool](https://github.com/awslabs/git-secrets)

---

## 🚨 Incident Response

Jika terjadi security breach:

1. **Immediate Actions:**
   - Revoke semua credentials
   - Change all passwords
   - Disable compromised accounts
   - Check access logs

2. **Investigation:**
   - Identify what was exposed
   - Check when it was exposed
   - Assess the damage

3. **Recovery:**
   - Generate new credentials
   - Update all systems
   - Notify affected parties if necessary

4. **Prevention:**
   - Review security policies
   - Update security measures
   - Train team members

---

## 📧 Contact

Jika menemukan security vulnerability, silakan report ke:
- Email: security@yourapp.com
- Private disclosure preferred
- Do not create public issues for security vulnerabilities

---

**Remember: Security is not a one-time task, it's a continuous process!** 🔒