// Dashboard functionality
document.addEventListener('DOMContentLoaded', function() {
    loadDashboardData();
});

async function loadDashboardData() {
    try {
        // Load all data in parallel
        const [products, categories, suppliers, lowStocks, outOfStocks] = await Promise.all([
            axios.get(API_ENDPOINTS.products),
            axios.get(API_ENDPOINTS.categories),
            axios.get(API_ENDPOINTS.suppliers),
            axios.get(`${API_ENDPOINTS.stocks}/low-stock`),
            axios.get(`${API_ENDPOINTS.stocks}/out-of-stock`)
        ]);

        // Update statistics
        document.getElementById('totalProducts').textContent = products.data.length;
        document.getElementById('totalCategories').textContent = categories.data.length;
        document.getElementById('totalSuppliers').textContent = suppliers.data.length;
        document.getElementById('lowStockItems').textContent = lowStocks.data.length;

        // Load tables
        loadLowStockTable(lowStocks.data);
        loadOutOfStockTable(outOfStocks.data);
        loadRecentProducts(products.data);

    } catch (error) {
        handleError(error);
    }
}

function loadLowStockTable(stocks) {
    const tbody = document.getElementById('lowStockTable');

    if (stocks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Tidak ada produk dengan stok rendah</td></tr>';
        return;
    }

    tbody.innerHTML = stocks.map(stock => `
        <tr>
            <td>${stock.productName}</td>
            <td>
                <span class="badge bg-warning">${stock.quantity}</span>
            </td>
            <td>
                <span class="badge bg-secondary">${stock.minimumStock}</span>
            </td>
        </tr>
    `).join('');
}

function loadOutOfStockTable(stocks) {
    const tbody = document.getElementById('outOfStockTable');

    if (stocks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Tidak ada produk yang habis</td></tr>';
        return;
    }

    tbody.innerHTML = stocks.map(stock => `
        <tr>
            <td>${stock.productName}</td>
            <td><code>${stock.productSku || '-'}</code></td>
            <td><span class="badge bg-info">${stock.categoryName || '-'}</span></td>
        </tr>
    `).join('');
}

function loadRecentProducts(products) {
    const tbody = document.getElementById('recentProductsTable');

    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Belum ada produk</td></tr>';
        return;
    }

    // Show last 10 products
    const recentProducts = products.slice(-10).reverse();

    tbody.innerHTML = recentProducts.map(product => {
        const stockBadge = getStockBadge(product.stockQuantity, product.minimumStock);

        return `
            <tr>
                <td>${product.name}</td>
                <td><code>${product.sku || '-'}</code></td>
                <td><span class="badge bg-info">${product.categoryName || '-'}</span></td>
                <td>${product.supplierName || '-'}</td>
                <td>${formatCurrency(product.price)}</td>
                <td>${stockBadge}</td>
            </tr>
        `;
    }).join('');
}

function getStockBadge(quantity, minimumStock) {
    if (quantity === 0) {
        return '<span class="badge bg-danger">Out of Stock</span>';
    } else if (quantity <= minimumStock) {
        return `<span class="badge bg-warning">${quantity}</span>`;
    } else {
        return `<span class="badge bg-success">${quantity}</span>`;
    }
}
