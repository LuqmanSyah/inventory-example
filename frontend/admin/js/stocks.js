// Admin Stocks functionality - Full access to stock management
let allStocks = [];
let lowStocks = [];
let outOfStocks = [];
let currentStockId = null;
let currentProductId = null;

document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkAdminRole(); // Verify user is admin
  setUserDisplay(); // Set user display name
  setupLogoutHandler(); // Setup logout functionality
  loadAllStocks();
  loadLowStocks();
  loadOutOfStocks();
});

async function loadAllStocks() {
  try {
    const response = await axios.get(API_ENDPOINTS.stocks);
    allStocks = response.data;
    displayAllStocks(allStocks);
  } catch (error) {
    handleError(error);
  }
}

async function loadLowStocks() {
  try {
    const response = await axios.get(`${API_ENDPOINTS.stocks}/low-stock`);
    lowStocks = response.data;
    displayLowStocks(lowStocks);
  } catch (error) {
    handleError(error);
  }
}

async function loadOutOfStocks() {
  try {
    const response = await axios.get(`${API_ENDPOINTS.stocks}/out-of-stock`);
    outOfStocks = response.data;
    displayOutOfStocks(outOfStocks);
  } catch (error) {
    handleError(error);
  }
}

function displayAllStocks(stocks) {
  const tbody = document.getElementById("allStocksTableBody");

  if (stocks.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No stocks found</td></tr>';
    return;
  }

  tbody.innerHTML = stocks
    .map((stock) => {
      const statusBadge = getStockStatusBadge(stock.quantity, stock.minimumStock);

      return `
            <tr>
                <td>${stock.productId}</td>
                <td><strong>${stock.productName}</strong></td>
                <td><code>${stock.productSku || "-"}</code></td>
                <td><span class="badge bg-info">${stock.categoryName || "-"}</span></td>
                <td><strong>${stock.quantity}</strong></td>
                <td>${stock.minimumStock}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-success" onclick="openAdjustModal(${stock.productId}, 'add')" title="Add Stock">
                            <i class="bi bi-plus-circle"></i>
                        </button>
                        <button class="btn btn-warning" onclick="openAdjustModal(${stock.productId}, 'reduce')" title="Reduce Stock">
                            <i class="bi bi-dash-circle"></i>
                        </button>
                        <button class="btn btn-primary" onclick="openUpdateModal(${stock.id})" title="Update Settings">
                            <i class="bi bi-gear"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    })
    .join("");
}

function displayLowStocks(stocks) {
  const tbody = document.getElementById("lowStocksTableBody");

  if (stocks.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No low stock items</td></tr>';
    return;
  }

  tbody.innerHTML = stocks
    .map(
      (stock) => `
        <tr class="table-warning">
            <td><strong>${stock.productName}</strong></td>
            <td><code>${stock.productSku || "-"}</code></td>
            <td><span class="badge bg-info">${stock.categoryName || "-"}</span></td>
            <td><span class="badge bg-warning">${stock.quantity}</span></td>
            <td><span class="badge bg-secondary">${stock.minimumStock}</span></td>
            <td>
                <button class="btn btn-sm btn-success" onclick="openAdjustModal(${stock.productId}, 'add')" title="Add Stock">
                    <i class="bi bi-plus-circle"></i> Add Stock
                </button>
            </td>
        </tr>
    `,
    )
    .join("");
}

function displayOutOfStocks(stocks) {
  const tbody = document.getElementById("outStocksTableBody");

  if (stocks.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No out of stock items</td></tr>';
    return;
  }

  tbody.innerHTML = stocks
    .map(
      (stock) => `
        <tr class="table-danger">
            <td><strong>${stock.productName}</strong></td>
            <td><code>${stock.productSku || "-"}</code></td>
            <td><span class="badge bg-info">${stock.categoryName || "-"}</span></td>
            <td><span class="badge bg-secondary">${stock.minimumStock}</span></td>
            <td>
                <button class="btn btn-sm btn-success" onclick="openAdjustModal(${stock.productId}, 'add')" title="Add Stock">
                    <i class="bi bi-plus-circle"></i> Restock
                </button>
            </td>
        </tr>
    `,
    )
    .join("");
}

function getStockStatusBadge(quantity, minimumStock) {
  if (quantity === 0) {
    return '<span class="badge bg-danger">Out of Stock</span>';
  } else if (quantity <= minimumStock) {
    return '<span class="badge bg-warning">Low Stock</span>';
  } else {
    return '<span class="badge bg-success">In Stock</span>';
  }
}

async function openUpdateModal(stockId) {
  try {
    const stock = allStocks.find((s) => s.id === stockId);
    if (!stock) {
      showAlert("Stock not found", "danger");
      return;
    }

    currentStockId = stockId;
    document.getElementById("stockId").value = stock.id;
    document.getElementById("stockProductId").value = stock.productId;
    document.getElementById("stockProductName").value = stock.productName;
    document.getElementById("currentQuantity").value = stock.quantity;
    document.getElementById("minimumStock").value = stock.minimumStock;

    const modal = new bootstrap.Modal(document.getElementById("updateStockModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

async function openAdjustModal(productId, type) {
  try {
    const stock = allStocks.find((s) => s.productId === productId);
    if (!stock) {
      showAlert("Stock not found", "danger");
      return;
    }

    currentProductId = productId;
    document.getElementById("adjustProductId").value = productId;
    document.getElementById("adjustType").value = type;
    document.getElementById("adjustProductName").value = stock.productName;
    document.getElementById("adjustCurrentQuantity").value = stock.quantity;
    document.getElementById("adjustQuantity").value = "";
    document.getElementById("adjustReason").value = "";

    const header = document.getElementById("adjustModalHeader");
    const title = document.getElementById("adjustModalTitle");
    const saveBtn = document.getElementById("adjustSaveBtn");

    if (type === "add") {
      header.className = "modal-header bg-success text-white";
      title.textContent = "Add Stock";
      saveBtn.className = "btn btn-success";
      saveBtn.innerHTML = '<i class="bi bi-plus-circle"></i> Add Stock';
    } else {
      header.className = "modal-header bg-warning";
      title.textContent = "Reduce Stock";
      saveBtn.className = "btn btn-warning";
      saveBtn.innerHTML = '<i class="bi bi-dash-circle"></i> Reduce Stock';
    }

    const modal = new bootstrap.Modal(document.getElementById("adjustStockModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

async function saveStockUpdate() {
  const stockId = document.getElementById("stockId").value;
  const minimumStock = parseInt(document.getElementById("minimumStock").value);

  try {
    await axios.put(`${API_ENDPOINTS.stocks}/${stockId}`, {
      minimumStock: minimumStock,
    });

    showAlert("Stock settings updated successfully!", "success");
    bootstrap.Modal.getInstance(document.getElementById("updateStockModal")).hide();

    // Reload all stock data
    loadAllStocks();
    loadLowStocks();
    loadOutOfStocks();
  } catch (error) {
    handleError(error);
  }
}

async function saveStockAdjustment() {
  const productId = document.getElementById("adjustProductId").value;
  const type = document.getElementById("adjustType").value;
  const quantity = parseInt(document.getElementById("adjustQuantity").value);
  const reason = document.getElementById("adjustReason").value;

  if (!quantity || quantity <= 0) {
    showAlert("Please enter a valid quantity", "danger");
    return;
  }

  try {
    const endpoint = type === "add" ? `${API_ENDPOINTS.stocks}/product/${productId}/add` : `${API_ENDPOINTS.stocks}/product/${productId}/reduce`;

    await axios.post(endpoint, null, {
      params: {
        quantity: quantity,
      },
    });

    showAlert(`Stock ${type === "add" ? "added" : "reduced"} successfully!`, "success");
    bootstrap.Modal.getInstance(document.getElementById("adjustStockModal")).hide();

    // Reload all stock data
    loadAllStocks();
    loadLowStocks();
    loadOutOfStocks();
  } catch (error) {
    handleError(error);
  }
}
