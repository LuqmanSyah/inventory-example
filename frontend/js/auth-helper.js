// Auth Helper - Membantu mengelola autentikasi dan logout

// Check if user is logged in, jika tidak redirect ke login
function checkAuth() {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    
    if (!token || !user) {
        window.location.href = 'login.html';
        return null;
    }
    
    try {
        return JSON.parse(user);
    } catch (e) {
        window.location.href = 'login.html';
        return null;
    }
}

// Set user display name
function setUserDisplay() {
    const user = checkAuth();
    if (user && document.getElementById('userNameDisplay')) {
        document.getElementById('userNameDisplay').textContent = user.fullName || user.username || 'User';
    }
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userRole');
    sessionStorage.clear();
    showAlert('Anda berhasil keluar dari sistem', 'success');
    setTimeout(() => {
        window.location.href = 'login.html';
    }, 1500);
}

// Enhanced showAlert function
function showAlert(message, type = 'success') {
    // Coba cari alert container di halaman
    let alertContainer = document.querySelector('[role="alert"]');
    
    if (!alertContainer) {
        // Jika tidak ada, buat satu
        alertContainer = document.createElement('div');
        alertContainer.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
        alertContainer.style.zIndex = '9999';
        alertContainer.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.appendChild(alertContainer);
    } else {
        alertContainer.className = `alert alert-${type} alert-dismissible fade show`;
        alertContainer.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
    }

    if (type === 'success') {
        setTimeout(() => {
            if (alertContainer && alertContainer.parentNode) {
                alertContainer.remove();
            }
        }, 3000);
    }
}

// Setup logout button
document.addEventListener('DOMContentLoaded', function() {
    setUserDisplay();
    
    const logoutBtn = document.getElementById('logoutBtn');
    const confirmLogoutBtn = document.getElementById('confirmLogoutBtn');
    const logoutModal = document.getElementById('logoutModal');

    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (logoutModal) {
                const modal = new bootstrap.Modal(logoutModal);
                modal.show();
            }
        });
    }

    if (confirmLogoutBtn) {
        confirmLogoutBtn.addEventListener('click', function() {
            logout();
        });
    }
});
