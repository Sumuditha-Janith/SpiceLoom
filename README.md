# SpiceLoom – Taste the Tradition

A full-stack Spring Boot e-commerce application for browsing, ordering, and reviewing exotic spices.  
Built with Spring Boot 3.x, Spring Security (JWT), MySQL, Bootstrap 5, and jQuery.

---

## Prerequisites

- **Java 17**  
- **Maven** (3.6+)  
- **MySQL** (running on `localhost:3306`)  
- **Gmail Account** (for password reset OTPs – optional but recommended)  
- **ImgBB Account** (free image hosting API key – optional, but needed for product image uploads)

---

## Environment Variables (.env file)

Sensitive data is kept out of the codebase using environment variables.  
The project reads from a `.env` file (or system environment variables).

### 1. Create a `.env` file

In the project root (same folder as `pom.xml`), create a file named **`.env`**:

```env
# JWT
JWT_SECRET_KEY=YourSuperSecretKeyForJwtGenerationMakeItLongEnough
JWT_EXPIRATION=360000000

# Database
DB_URL=jdbc:mysql://localhost:3306/spiceloom_db?createDatabaseIfNotExist=true
DB_USERNAME=root
DB_PASSWORD=Ijse@1234

# Mail (Gmail SMTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# OTP expiry (in milliseconds)
OTP_EXPIRATION=300000

# ImgBB API Key (image upload)
IMGBB_API_KEY=AddYourImgBBApiKeyHere
```

> **Note:**  
> - For Gmail, you **must** use an [App Password](https://support.google.com/accounts/answer/185833?hl=en) – never your regular password.  
> - Replace `your-email@gmail.com` and `your-app-password` with your real credentials.  
> - You can get a free ImgBB API key at [https://api.imgbb.com](https://api.imgbb.com). The one above is only for demonstration; **never commit real keys to a public repository**.

### 2. How environment variables are loaded

The `application.properties` file uses placeholders like `${JWT_SECRET_KEY}`.  
If you run the application **directly from the command line**, export the variables manually beforehand:

```bash
export JWT_SECRET_KEY=YourSuperSecretKeyForJwtGenerationMakeItLongEnough
export JWT_EXPIRATION=360000000
# ... other exports
mvn spring-boot:run
```

If you use an **IDE** (IntelliJ, Eclipse), you can:

- **IntelliJ Ultimate:** Add a `.env` file via the *EnvFile* plugin, or set them in the run configuration.  
- **VS Code:** Use the *Spring Boot Dashboard* extension and add environment variables in `launch.json`.  
- **Eclipse:** Set them in the run configuration under "Environment" tab.

For the `.env` file to be automatically loaded, you need a plugin or configuration. The `.env` file itself is not read by Spring Boot automatically – it’s intended to be used with IDE plugins or by exporting the variables beforehand.

---

## Database Setup

The application uses MySQL. The database schema is created automatically by Hibernate (`ddl-auto: update`).  
Just make sure your MySQL server is running and the credentials match the `.env` file.

If you don’t have the `spiceloom_db` database, the first run will create it.

---

## Running the Application

```bash
# Clone the repository
git clone https://github.com/Sumuditha-Janith/SpiceLoom.git
cd spiceloom

# Ensure environment variables are set (see above)

# Build and run
mvn clean install
mvn spring-boot:run
```

The application will be available at:

```
http://localhost:8080
```

---

## Default Admin Account

On startup, the system automatically creates an admin account if it doesn’t exist:

| Field    | Value                     |
|----------|---------------------------|
| Username | `Admin`                   |
| Password | `Admin@123`               |
| Email    | `admin@gmail.com`         |
| Role     | ADMIN                     |

**Change the password after first login.**

---

## Security & Important Notes

- The `.env` file is **never committed** to version control (already in `.gitignore`).  
- If you accidentally committed it before adding to `.gitignore`, remove it from Git tracking and **rotate all exposed keys**.  
- Never share real API keys, database passwords, or JWT secrets in public repositories.  
- For production, consider using a dedicated secrets manager (HashiCorp Vault, AWS Secrets Manager) instead of a plain `.env` file.

---

## Features Overview

- User registration, login, and OTP-based password reset (via Gmail)  
- JWT authentication with role-based access (Admin / Customer)  
- Admin product CRUD with ImgBB image upload  
- Special sale events (product-specific or global discounts)  
- Customer product catalog with sorting and filtering  
- Shopping cart, simulated checkout, order management  
- Order status management (Pending → Dispatched → Delivered)  
- Product reviews (5-star rating + comments)  
- PDF receipts for customers and sales reports for admins (iText 7)

---

## Tech Stack

- **Backend:** Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, Hibernate, MySQL  
- **Frontend:** HTML5, CSS3, Bootstrap 5, JavaScript, jQuery  
- **PDF Generation:** iText 7  
- **Image Hosting:** ImgBB API

---

## Frontend Structure

All frontend files are in `src/main/resources/FrontEnd/`:

- `index.html` – Landing page  
- `html/` – Login, register, dashboards, catalog, cart, orders, admin pages  
- `css/` – Custom styles  
- `js/` – JavaScript modules per page

Spring Boot serves them directly; no separate frontend server needed.

---

## Contact / Issues

For any problems, open an issue on the repository or contact the development team.

Enjoy exploring exotic spices with SpiceLoom!
