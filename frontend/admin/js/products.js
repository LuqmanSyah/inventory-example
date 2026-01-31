// Admin Products functionality - Full CRUD access
let allProducts = [];
let categories = [];
let suppliers = [];
let deleteProductId = null;

document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkAdminRole(); // Verify user is admin
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

    // Populate category select in modal
    const productCategory = document.getElementById("productCategory");
    productCategory.innerHTML = '<option value="">Select Category</option>' + categories.map((cat) => `<option value="${cat.id}">${cat.name}</option>`).join("");
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

    // Populate supplier select in modal
    const productSupplier = document.getElementById("productSupplier");
    productSupplier.innerHTML = '<option value="">Select Supplier</option>' + suppliers.map((sup) => `<option value="${sup.id}">${sup.name}</option>`).join("");
  } catch (error) {
    handleError(error);
  }
}

function displayProducts(products) {
  const tbody = document.getElementById("productsTableBody");

  if (products.length === 0) {
    tbody.innerHTML = '<tr><td colspan="9" class="text-center text-muted">No products found</td></tr>';
    return;
  }

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
                <td>
                    <button class="btn btn-sm btn-warning" onclick="openEditModal(${product.id})" title="Edit">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="openDeleteModal(${product.id})" title="Delete">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
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

function openAddModal() {
  document.getElementById("modalTitle").textContent = "Add Product";
  document.getElementById("productForm").reset();
  document.getElementById("productId").value = "";
  document.getElementById("productSku").value = "";
  document.getElementById("productSku").disabled = true;
}

async function openEditModal(id) {
  try {
    const response = await axios.get(`${API_ENDPOINTS.products}/${id}`);
    const product = response.data;

    document.getElementById("modalTitle").textContent = "Edit Product";
    document.getElementById("productId").value = product.id;
    document.getElementById("productName").value = product.name;
    document.getElementById("productSku").value = product.sku || "";
    document.getElementById("productSku").disabled = true;
    document.getElementById("productDescription").value = product.description || "";
    document.getElementById("productCategory").value = product.categoryId || "";
    document.getElementById("productSupplier").value = product.supplierId || "";
    document.getElementById("productPrice").value = product.price;

    const modal = new bootstrap.Modal(document.getElementById("productModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

async function saveProduct() {
  const id = document.getElementById("productId").value;
  const productData = {
    name: document.getElementById("productName").value,
    description: document.getElementById("productDescription").value,
    categoryId: parseInt(document.getElementById("productCategory").value) || null,
    supplierId: parseInt(document.getElementById("productSupplier").value) || null,
    price: parseFloat(document.getElementById("productPrice").value),
  };

  try {
    if (id) {
      // Update existing product
      await axios.put(`${API_ENDPOINTS.products}/${id}`, productData);
      showAlert("Product updated successfully!", "success");
    } else {
      // Create new product
      await axios.post(API_ENDPOINTS.products, productData);
      showAlert("Product created successfully!", "success");
    }

    // Close modal and reload products
    bootstrap.Modal.getInstance(document.getElementById("productModal")).hide();
    loadProducts();
  } catch (error) {
    handleError(error);
  }
}

function openDeleteModal(id) {
  deleteProductId = id;
  const product = allProducts.find((p) => p.id === id);
  document.getElementById("deleteProductName").textContent = product ? product.name : "";

  const modal = new bootstrap.Modal(document.getElementById("deleteModal"));
  modal.show();
}

async function confirmDelete() {
  if (!deleteProductId) return;

  try {
    await axios.delete(`${API_ENDPOINTS.products}/${deleteProductId}`);
    showAlert("Product deleted successfully!", "success");

    bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
    loadProducts();
  } catch (error) {
    handleError(error);
  }

  deleteProductId = null;
}
