// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// API Endpoints
const API_ENDPOINTS = {
    products: `${API_BASE_URL}/products`,
    categories: `${API_BASE_URL}/categories`,
    suppliers: `${API_BASE_URL}/suppliers`,
    stocks: `${API_BASE_URL}/stocks`
};

// Axios default configuration
axios.defaults.headers.common['Content-Type'] = 'application/json';

// Utility functions
const formatCurrency = (amount) => {
    return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        minimumFractionDigits: 0
    }).format(amount);
};

const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('id-ID', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
};

const showAlert = (message, type = 'success') => {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    alertDiv.style.zIndex = '9999';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);

    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
};

const handleError = (error) => {
    console.error('Error:', error);
    let message = 'Terjadi kesalahan pada server';

    if (error.response) {
        if (error.response.data && error.response.data.message) {
            message = error.response.data.message;
        } else if (error.response.status === 404) {
            message = 'Data tidak ditemukan';
        } else if (error.response.status === 400) {
            message = 'Data yang dikirim tidak valid';
        }
    } else if (error.request) {
        message = 'Tidak dapat terhubung ke server. Pastikan server sudah berjalan.';
    }

    showAlert(message, 'danger');
};
