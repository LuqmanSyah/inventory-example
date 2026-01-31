// Staff Products functionality - View only (no add/edit/delete)
let allProducts = [];
let categories = [];
let suppliers = [];

document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkStaffRole(); // Verify user is staff
  setUserDisplay(); // Set user display name
  setupLogoutHandler(); // Setup logout functionality
  loadProducts();
  loadCategories();
  loadSuppliers();
  setupEventListeners();
});

function setupEventListeners() {
  // Search input
  document.getElementById("searchInput").addEventListener("input", filterProducts);

  // Category filter
  document.getElementById("categoryFilter").addEventListener("change", filterProducts);

  // Supplier filter
  document.getElementById("supplierFilter").addEventListener("change", filterProducts);
}

async function loadProducts() {
  try {
    const response = await axios.get(API_ENDPOINTS.products);
    allProducts = response.data;
    displayProducts(allProducts);
  } catch (error) {
    handleError(error);
  }
}

async function loadCategories() {
  try {
    const response = await axios.get(API_ENDPOINTS.categories);
    categories = response.data;

    // Populate category filter
    const categoryFilter = document.getElementById("categoryFilter");
    categoryFilter.innerHTML = '<option value="">All Categories</option>' + categories.map((cat) => `<option value="${cat.id}">${cat.name}</option>`).join("");
  } catch (error) {
    handleError(error);
  }
}

async function loadSuppliers() {
  try {
    const response = await axios.get(API_ENDPOINTS.suppliers);
    suppliers = response.data;

    // Populate supplier filter
    const supplierFilter = document.getElementById("supplierFilter");
    supplierFilter.innerHTML = '<option value="">All Suppliers</option>' + suppliers.map((sup) => `<option value="${sup.id}">${sup.name}</option>`).join("");
  } catch (error) {
    handleError(error);
  }
}

function displayProducts(products) {
  const tbody = document.getElementById("productsTableBody");

  if (products.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No products found</td></tr>';
    return;
  }

  // Staff view - no action buttons
  tbody.innerHTML = products
    .map((product, index) => {
      const stockBadge = getStockBadge(product.stockQuantity, product.minimumStock);

      return `
            <tr>
                <td>${index + 1}</td>
                <td><strong>${product.name}</strong></td>
                <td><code>${product.sku || "-"}</code></td>
                <td>${product.description || "-"}</td>
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
  if (quantity === null || quantity === undefined) {
    return '<span class="badge bg-secondary">N/A</span>';
  }
  if (quantity === 0) {
    return '<span class="badge bg-danger">Out of Stock</span>';
  } else if (minimumStock && quantity <= minimumStock) {
    return `<span class="badge bg-warning">${quantity}</span>`;
  } else {
    return `<span class="badge bg-success">${quantity}</span>`;
  }
}

function filterProducts() {
  const searchTerm = document.getElementById("searchInput").value.toLowerCase();
  const categoryId = document.getElementById("categoryFilter").value;
  const supplierId = document.getElementById("supplierFilter").value;

  let filtered = allProducts;

  // Filter by search term
  if (searchTerm) {
    filtered = filtered.filter(
      (product) => product.name.toLowerCase().includes(searchTerm) || (product.sku && product.sku.toLowerCase().includes(searchTerm)) || (product.description && product.description.toLowerCase().includes(searchTerm)),
    );
  }

  // Filter by category
  if (categoryId) {
    filtered = filtered.filter((product) => product.categoryId == categoryId);
  }

  // Filter by supplier
  if (supplierId) {
    filtered = filtered.filter((product) => product.supplierId == supplierId);
  }

  displayProducts(filtered);
}

function resetFilters() {
  document.getElementById("searchInput").value = "";
  document.getElementById("categoryFilter").value = "";
  document.getElementById("supplierFilter").value = "";
  displayProducts(allProducts);
}
