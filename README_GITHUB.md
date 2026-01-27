# 📦 Inventory Management System

A full-stack web application for managing inventory, products, categories, suppliers, and stock levels. Built with Spring Boot (Backend) and HTML/Bootstrap/Axios (Frontend).

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🌟 Features

### ✨ Core Functionality
- **Product Management**: Full CRUD operations with search and filtering
- **Category Management**: Organize products into categories
- **Supplier Management**: Maintain supplier information and contacts
- **Stock Management**: Track inventory levels, add/reduce stock
- **Low Stock Alerts**: Automatic warnings for products below minimum threshold
- **Dashboard**: Real-time statistics and insights

### 🎨 User Interface
- Responsive design (Mobile, Tablet, Desktop)
- Clean and modern UI with Bootstrap 5
- Real-time search and filtering
- Color-coded status indicators
- Interactive modals and forms
- Toast notifications for user feedback

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Modern web browser
- Python 3 or Node.js (for frontend server)

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/inventory-management-system.git
cd inventory-management-system
```

#### 2. Setup Database

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE inventory_db;

# Exit
\q
```

#### 3. Configure Environment Variables

Copy the example environment file:

```bash
cp env.example .env
```

Edit `.env` and set your database credentials:

```bash
DB_URL=jdbc:postgresql://localhost:5432/inventory_db
DB_USERNAME=postgres
DB_PASSWORD=your_password_here
SERVER_PORT=8080
```

**OR** set environment variables directly:

**Windows:**
```cmd
set DB_PASSWORD=your_password
```

**Linux/Mac:**
```bash
export DB_PASSWORD=your_password
```

#### 4. Run Backend

```bash
# Build and run
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

Backend will start at: `http://localhost:8080`

#### 5. Run Frontend

**Option A: Using Python**
```bash
cd frontend
python -m http.server 8000
```

**Option B: Using Node.js**
```bash
cd frontend
npx http-server -p 8000
```

**Option C: Using VS Code Live Server**
1. Install "Live Server" extension
2. Right-click `index.html`
3. Select "Open with Live Server"

Frontend will be available at: `http://localhost:8000`

#### 6. Access the Application

Open your browser and navigate to: `http://localhost:8000`

## 📁 Project Structure

```
inventory-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/inventoryexample/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── repository/      # Spring Data Repositories
│   │   │   ├── service/         # Business Logic
│   │   │   └── InventoryExampleApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-example.properties
│   └── test/                    # Test files
├── frontend/
│   ├── index.html              # Dashboard
│   ├── products.html           # Products page
│   ├── categories.html         # Categories page
│   ├── suppliers.html          # Suppliers page
│   ├── stocks.html             # Stock management
│   ├── js/
│   │   ├── config.js          # API configuration
│   │   ├── dashboard.js       # Dashboard logic
│   │   ├── products.js        # Products logic
│   │   ├── categories.js      # Categories logic
│   │   ├── suppliers.js       # Suppliers logic
│   │   └── stocks.js          # Stock management logic
│   ├── README.md              # Frontend documentation
│   └── FEATURES.md            # Detailed features
├── .gitignore
├── pom.xml
├── env.example                 # Environment variables template
├── QUICKSTART.md              # Quick start guide
├── SECURITY.md                # Security guidelines
└── README.md                  # This file
```

## 🔧 Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Libraries**: 
  - Lombok (Boilerplate reduction)
  - Bean Validation (Data validation)
  - Spring Web (REST API)

### Frontend
- **HTML5** - Semantic markup
- **Bootstrap 5.3** - UI framework
- **Axios** - HTTP client
- **Vanilla JavaScript** - No framework dependencies
- **Bootstrap Icons** - Icon library

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### Products
```
GET    /products              - Get all products
GET    /products/{id}         - Get product by ID
POST   /products              - Create new product
PUT    /products/{id}         - Update product
DELETE /products/{id}         - Delete product
GET    /products/search?name={name}  - Search products
GET    /products/category/{categoryId} - Get by category
GET    /products/supplier/{supplierId} - Get by supplier
```

#### Categories
```
GET    /categories            - Get all categories
GET    /categories/{id}       - Get category by ID
POST   /categories            - Create category
PUT    /categories/{id}       - Update category
DELETE /categories/{id}       - Delete category
```

#### Suppliers
```
GET    /suppliers             - Get all suppliers
GET    /suppliers/{id}        - Get supplier by ID
POST   /suppliers             - Create supplier
PUT    /suppliers/{id}        - Update supplier
DELETE /suppliers/{id}        - Delete supplier
GET    /suppliers/search?name={name} - Search suppliers
```

#### Stocks
```
GET    /stocks                - Get all stocks
GET    /stocks/{id}           - Get stock by ID
GET    /stocks/product/{productId} - Get stock by product
GET    /stocks/low-stock      - Get low stock items
GET    /stocks/out-of-stock   - Get out of stock items
PUT    /stocks/{id}           - Update stock settings
POST   /stocks/product/{productId}/add?quantity={qty} - Add stock
POST   /stocks/product/{productId}/reduce?quantity={qty} - Reduce stock
```

### Example Requests

**Create Product:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "sku": "LAP-001",
    "description": "Gaming Laptop",
    "price": 15000000,
    "categoryId": 1,
    "supplierId": 1
  }'
```

**Add Stock:**
```bash
curl -X POST "http://localhost:8080/api/stocks/product/1/add?quantity=50"
```

## 🎯 Usage Guide

### Initial Setup

1. **Create Categories** (Required first)
   - Navigate to Categories page
   - Click "Add Category"
   - Example: Electronics, Food, Clothing

2. **Create Suppliers** (Required first)
   - Navigate to Suppliers page
   - Click "Add Supplier"
   - Fill in supplier details

3. **Create Products**
   - Navigate to Products page
   - Click "Add Product"
   - Select category and supplier
   - Set price

4. **Manage Stock**
   - Navigate to Stocks page
   - Click "+" to add stock
   - Click "-" to reduce stock
   - Click gear icon to set minimum threshold

### Daily Operations

- **Check Dashboard**: Monitor low stock alerts
- **Update Stock**: Add/reduce stock as needed
- **Search Products**: Use search and filters
- **Manage Inventory**: CRUD operations on all entities

## 🔒 Security

### Important Security Notes

⚠️ **NEVER commit sensitive data to GitHub!**

Files excluded from version control (in `.gitignore`):
- `.env` - Environment variables
- `application-local.properties` - Local configurations
- `*.log` - Log files

### Securing Your Application

1. **Use Environment Variables** for sensitive data
2. **Enable HTTPS** in production
3. **Implement Authentication** (JWT, OAuth2)
4. **Add Authorization** (Role-based access control)
5. **Use Strong Passwords** for database
6. **Enable CORS** properly (already configured)
7. **Validate All Inputs** (backend validation in place)

See [SECURITY.md](SECURITY.md) for detailed security guidelines.

## 🧪 Testing

### Manual Testing

Test API endpoints using:

**Postman:**
- Import Postman collection (if provided)
- Test all CRUD operations

**cURL:**
```bash
# Test connection
curl http://localhost:8080/api/products

# Test create
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","price":1000,"categoryId":1,"supplierId":1}'
```

### Unit Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest
```

## 🐛 Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```properties
# Change port in application.properties
server.port=8081
```

**Database connection error:**
- Check PostgreSQL is running
- Verify credentials in `.env` or environment variables
- Ensure database `inventory_db` exists

**CORS errors:**
- Ensure `CorsConfig.java` is present
- Restart backend after adding CORS config

### Frontend Issues

**Cannot connect to API:**
- Verify backend is running: `curl http://localhost:8080/api/products`
- Check `config.js` has correct API URL
- Open browser console (F12) for errors

**Data not loading:**
- Check browser console for errors
- Verify API endpoints are working
- Test with curl or Postman

## 🚀 Deployment

### Deploy Backend

**Heroku:**
```bash
heroku create your-app-name
heroku addons:create heroku-postgresql:hobby-dev
heroku config:set DB_PASSWORD=your_password
git push heroku main
```

**Docker:**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t inventory-app .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host/db \
  -e DB_PASSWORD=password \
  inventory-app
```

### Deploy Frontend

**GitHub Pages:**
```bash
cd frontend
# Update config.js with production API URL
git add .
git commit -m "Deploy to GitHub Pages"
git push
```

**Netlify / Vercel:**
- Connect GitHub repository
- Set build directory to `frontend`
- Configure environment variables

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Coding Standards

- Follow Java conventions
- Use meaningful variable names
- Add comments for complex logic
- Write unit tests for new features
- Update documentation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap team for the UI toolkit
- PostgreSQL community
- Open source community

## 📞 Support

If you encounter any issues or have questions:

1. Check [QUICKSTART.md](QUICKSTART.md) for setup help
2. Review [Troubleshooting](#-troubleshooting) section
3. Check existing [Issues](https://github.com/yourusername/inventory-management-system/issues)
4. Create a new issue with detailed information

## 🗺️ Roadmap

Future enhancements planned:

- [ ] User authentication & authorization
- [ ] Role-based access control (Admin, User, Viewer)
- [ ] Export to Excel/PDF
- [ ] Import from CSV
- [ ] Advanced analytics dashboard
- [ ] Stock movement history
- [ ] Email notifications for low stock
- [ ] Barcode integration
- [ ] Multi-language support
- [ ] Dark mode
- [ ] REST API documentation (Swagger/OpenAPI)
- [ ] Mobile app (React Native / Flutter)

## 📊 Project Status

Current Version: **1.0.0**

Status: **Active Development**

Last Updated: **January 2024**

---

**⭐ If you find this project helpful, please give it a star!**

**Made with ❤️ using Spring Boot and Bootstrap**