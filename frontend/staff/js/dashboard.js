// Staff Dashboard functionality
document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkStaffRole(); // Only staff can access
  setUserDisplay(); // Set user display name
  setupLogoutHandler(); // Setup logout functionality
  showWelcomeMessageIfFirstLogin(); // Show welcome message only on first login
  loadDashboardData();
});

function checkStaffRole() {
  const userRole = localStorage.getItem("inventori_user_role");
  if (userRole === "ADMIN") {
    // Redirect to admin dashboard if admin
    window.location.href = "../admin/dashboard.html";
    return false;
  }
  return true;
}

function showWelcomeMessageIfFirstLogin() {
  // Check if this is the first login in this session
  const isFirstLogin = sessionStorage.getItem("isFirstLogin");

  if (isFirstLogin === "true") {
    // Show welcome message
    const user = JSON.parse(localStorage.getItem("inventori_user") || "{}");
    alert(`Selamat datang, ${user.fullName || user.username}! Anda dapat mengelola produk, stok, dan supplier.`);

    // Clear the flag so it doesn't show again on next visit
    sessionStorage.removeItem("isFirstLogin");
  }
}

async function loadDashboardData() {
  try {
    // Load all data in parallel
    const [products, lowStocks, outOfStocks] = await Promise.all([axios.get(API_ENDPOINTS.products), axios.get(`${API_ENDPOINTS.stocks}/low-stock`), axios.get(`${API_ENDPOINTS.stocks}/out-of-stock`)]);

    // Update statistics
    document.getElementById("totalProducts").textContent = products.data.length;
    document.getElementById("lowStockItems").textContent = lowStocks.data.length;
    document.getElementById("outOfStockItems").textContent = outOfStocks.data.length;

    // Load tables
    loadLowStockTable(lowStocks.data);
    loadOutOfStockTable(outOfStocks.data);
    loadRecentProducts(products.data);
  } catch (error) {
    handleError(error);
  }
}

function loadLowStockTable(stocks) {
  const tbody = document.getElementById("lowStockTable");

  if (stocks.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Tidak ada produk dengan stok rendah</td></tr>';
    return;
  }

  tbody.innerHTML = stocks
    .map(
      (stock) => `
        <tr>
            <td>${stock.productName}</td>
            <td>
                <span class="badge bg-warning text-dark">${stock.quantity}</span>
            </td>
            <td>
                <span class="badge bg-secondary">${stock.minimumStock}</span>
            </td>
        </tr>
    `,
    )
    .join("");
}

function loadOutOfStockTable(stocks) {
  const tbody = document.getElementById("outOfStockTable");

  if (stocks.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Tidak ada produk yang habis</td></tr>';
    return;
  }

  tbody.innerHTML = stocks
    .map(
      (stock) => `
        <tr>
            <td>${stock.productName || "-"}</td>
            <td><code>${stock.productSku || "-"}</code></td>
            <td><span class="badge bg-info">${stock.categoryName || "-"}</span></td>
        </tr>
    `,
    )
    .join("");
}

function loadRecentProducts(products) {
  const tbody = document.getElementById("recentProductsTable");

  if (products.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Belum ada produk</td></tr>';
    return;
  }

  // Show first 10 products (already sorted by createdAt desc from backend)
  const recentProducts = products.slice(0, 10);

  tbody.innerHTML = recentProducts
    .map((product) => {
      const stockBadge = getStockBadge(product.stockQuantity, product.minimumStock);

      return `
            <tr>
                <td>${product.name}</td>
                <td><code>${product.sku || "-"}</code></td>
                <td><span class="badge bg-info">${product.categoryName || "-"}</span></td>
                <td>${product.supplierName || "-"}</td>
                <td>${formatCurrency(product.price)}</td>
                <td>${stockBadge}</td>
            </tr>
        `;
    })
    .join("");
}

function getStockBadge(quantity, minimumStock) {
  if (quantity === 0) {
    return '<span class="badge bg-danger">Habis</span>';
  } else if (quantity <= minimumStock) {
    return '<span class="badge bg-warning text-dark">Rendah</span>';
  } else {
    return '<span class="badge bg-success">Tersedia</span>';
  }
}
