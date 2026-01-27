// Stocks functionality
let allStocks = [];
let lowStocks = [];
let outOfStocks = [];
let currentStockId = null;
let currentProductId = null;

document.addEventListener('DOMContentLoaded', function() {
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
    const tbody = document.getElementById('allStocksTableBody');

    if (stocks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No stocks found</td></tr>';
        return;
    }

    tbody.innerHTML = stocks.map(stock => {
        const statusBadge = getStockStatusBadge(stock.quantity, stock.minimumStock);

        return `
            <tr>
                <td>${stock.productId}</td>
                <td><strong>${stock.productName}</strong></td>
                <td><code>${stock.productSku || '-'}</code></td>
                <td><span class="badge bg-info">${stock.categoryName || '-'}</span></td>
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
    }).join('');
}

function displayLowStocks(stocks) {
    const tbody = document.getElementById('lowStocksTableBody');

    if (stocks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No low stock items</td></tr>';
        return;
    }

    tbody.innerHTML = stocks.map(stock => `
        <tr class="table-warning">
            <td><strong>${stock.productName}</strong></td>
            <td><code>${stock.productSku || '-'}</code></td>
            <td><span class="badge bg-info">${stock.categoryName || '-'}</span></td>
            <td><span class="badge bg-warning">${stock.quantity}</span></td>
            <td><span class="badge bg-secondary">${stock.minimumStock}</span></td>
            <td>
                <button class="btn btn-sm btn-success" onclick="openAdjustModal(${stock.productId}, 'add')" title="Add Stock">
                    <i class="bi bi-plus-circle"></i> Add Stock
                </button>
            </td>
        </tr>
    `).join('');
}

function displayOutOfStocks(stocks) {
    const tbody = document.getElementById('outStocksTableBody');

    if (stocks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No out of stock items</td></tr>';
        return;
    }

    tbody.innerHTML = stocks.map(stock => `
        <tr class="table-danger">
            <td><strong>${stock.productName}</strong></td>
            <td><code>${stock.productSku || '-'}</code></td>
            <td><span class="badge bg-info">${stock.categoryName || '-'}</span></td>
            <td><span class="badge bg-secondary">${stock.minimumStock}</span></td>
            <td>
                <button class="btn btn-sm btn-success" onclick="openAdjustModal(${stock.productId}, 'add')" title="Add Stock">
                    <i class="bi bi-plus-circle"></i> Add Stock
                </button>
            </td>
        </tr>
    `).join('');
}

function getStockStatusBadge(quantity, minimumStock) {
    if (quantity === 0) {
        return '<span class="badge bg-danger"><i class="bi bi-x-circle"></i> Out of Stock</span>';
    } else if (quantity <= minimumStock) {
        return '<span class="badge bg-warning"><i class="bi bi-exclamation-triangle"></i> Low Stock</span>';
    } else {
        return '<span class="badge bg-success"><i class="bi bi-check-circle"></i> In Stock</span>';
    }
}

async function openUpdateModal(stockId) {
    try {
        const response = await axios.get(`${API_ENDPOINTS.stocks}/${stockId}`);
        const stock = response.data;

        currentStockId = stock.id;
        document.getElementById('stockId').value = stock.id;
        document.getElementById('stockProductId').value = stock.productId;
        document.getElementById('stockProductName').value = stock.productName;
        document.getElementById('currentQuantity').value = stock.quantity;
        document.getElementById('minimumStock').value = stock.minimumStock;

        const modal = new bootstrap.Modal(document.getElementById('updateStockModal'));
        modal.show();
    } catch (error) {
        handleError(error);
    }
}

async function saveStockUpdate() {
    const form = document.getElementById('updateStockForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const stockId = document.getElementById('stockId').value;
    const stockData = {
        minimumStock: parseInt(document.getElementById('minimumStock').value)
    };

    try {
        await axios.put(`${API_ENDPOINTS.stocks}/${stockId}`, stockData);
        showAlert('Stock settings updated successfully!', 'success');

        // Close modal and reload stocks
        const modal = bootstrap.Modal.getInstance(document.getElementById('updateStockModal'));
        modal.hide();
        loadAllStocks();
        loadLowStocks();
        loadOutOfStocks();
    } catch (error) {
        handleError(error);
    }
}

async function openAdjustModal(productId, type) {
    try {
        // Get product and stock info
        const productResponse = await axios.get(`${API_ENDPOINTS.products}/${productId}`);
        const product = productResponse.data;

        currentProductId = productId;
        document.getElementById('adjustProductId').value = productId;
        document.getElementById('adjustType').value = type;
        document.getElementById('adjustProductName').value = product.name;
        document.getElementById('adjustCurrentQuantity').value = product.stockQuantity || 0;
        document.getElementById('adjustQuantity').value = '';
        document.getElementById('adjustReason').value = '';

        // Update modal header based on type
        const modalHeader = document.getElementById('adjustModalHeader');
        const modalTitle = document.getElementById('adjustModalTitle');
        const saveBtn = document.getElementById('adjustSaveBtn');

        if (type === 'add') {
            modalHeader.className = 'modal-header bg-success text-white';
            modalTitle.innerHTML = '<i class="bi bi-plus-circle"></i> Add Stock';
            saveBtn.className = 'btn btn-success';
            saveBtn.innerHTML = '<i class="bi bi-plus-circle"></i> Add Stock';
        } else {
            modalHeader.className = 'modal-header bg-warning';
            modalTitle.innerHTML = '<i class="bi bi-dash-circle"></i> Reduce Stock';
            saveBtn.className = 'btn btn-warning';
            saveBtn.innerHTML = '<i class="bi bi-dash-circle"></i> Reduce Stock';
        }

        const modal = new bootstrap.Modal(document.getElementById('adjustStockModal'));
        modal.show();
    } catch (error) {
        handleError(error);
    }
}

async function saveStockAdjustment() {
    const form = document.getElementById('adjustStockForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const productId = document.getElementById('adjustProductId').value;
    const type = document.getElementById('adjustType').value;
    const quantity = parseInt(document.getElementById('adjustQuantity').value);
    const reason = document.getElementById('adjustReason').value;

    try {
        let endpoint;
        if (type === 'add') {
            endpoint = `${API_ENDPOINTS.stocks}/product/${productId}/add`;
        } else {
            endpoint = `${API_ENDPOINTS.stocks}/product/${productId}/reduce`;
        }

        await axios.post(`${endpoint}?quantity=${quantity}${reason ? '&reason=' + encodeURIComponent(reason) : ''}`);

        const message = type === 'add' ? 'Stock added successfully!' : 'Stock reduced successfully!';
        showAlert(message, 'success');

        // Close modal and reload stocks
        const modal = bootstrap.Modal.getInstance(document.getElementById('adjustStockModal'));
        modal.hide();
        loadAllStocks();
        loadLowStocks();
        loadOutOfStocks();
    } catch (error) {
        handleError(error);
    }
}
