let selectedRole = null;

// Initialize the login page
document.addEventListener('DOMContentLoaded', function () {
    console.log('Login page loaded');
    
    // Setup role pill click handlers
    const adminPill = document.getElementById('adminPill');
    const staffPill = document.getElementById('staffPill');
    const proceedBtn = document.getElementById('proceedBtn');
    const loginForm = document.getElementById('loginForm');
    const togglePasswordBtn = document.getElementById('togglePassword');

    if (adminPill) {
        adminPill.addEventListener('click', function() {
            console.log('Admin pill clicked');
            selectRole('ADMIN');
        });
    }

    if (staffPill) {
        staffPill.addEventListener('click', function() {
            console.log('Staff pill clicked');
            selectRole('STAFF');
        });
    }

    if (proceedBtn) {
        proceedBtn.addEventListener('click', function() {
            console.log('Proceed button clicked');
            proceedToLogin();
        });
    }

    // Toggle password visibility
    if (togglePasswordBtn) {
        togglePasswordBtn.addEventListener('click', function () {
            const passwordInput = document.getElementById('password');
            const icon = this.querySelector('i');

            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });
    }

    // Form submission
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // Check for existing session on page load
    checkExistingSession();
});

function checkExistingSession() {
    // Jangan auto-redirect dari login page agar role selection bisa dipilih
    const rememberMe = localStorage.getItem('rememberMe');
    if (rememberMe === 'true') {
        const rememberedUsername = localStorage.getItem('rememberedUsername');
        if (rememberedUsername) {
            document.getElementById('username').value = rememberedUsername;
            document.getElementById('rememberMe').checked = true;
        }
    }
}

function backToRoleSelection() {
    // Update step indicator
    document.getElementById('step1').classList.add('active');
    document.getElementById('step1').classList.remove('completed');
    document.getElementById('step2').classList.remove('active');

    // Show role selection
    document.getElementById('loginStep').style.display = 'none';
    document.getElementById('roleSelectionStep').style.display = 'block';

    // Clear form
    document.getElementById('loginForm').reset();
    document.getElementById('username').focus();
}

async function handleLogin(e) {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('rememberMe').checked;

    if (!username || !password) {
        showAlert('Username dan password harus diisi', 'danger');
        return;
    }

    // Disable button and show loading
    const loginBtn = document.getElementById('loginBtn');
    const spinner = document.getElementById('spinner');
    const btnText = document.getElementById('btnText');

    loginBtn.disabled = true;
    spinner.style.display = 'inline-block';
    btnText.textContent = 'Memproses...';

    try {
        const response = await axios.post(`${API_ENDPOINTS.baseURL}/api/auth/login`, {
            username: username,
            password: password,
        });

        if (response.data && response.data.token) {
            // Store authentication data
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data.user));
            localStorage.setItem('userRole', response.data.user.role);

            // Mark first login in this session
            sessionStorage.setItem('isFirstLogin', 'true');
            sessionStorage.setItem('selectedRole', selectedRole);

            // Remember me functionality
            if (rememberMe) {
                localStorage.setItem('rememberMe', 'true');
                localStorage.setItem('rememberedUsername', username);
            }

            showAlert('Login berhasil! Mengalihkan...', 'success');

            // Redirect based on role
            setTimeout(() => {
                const role = response.data.user.role;
                if (role === 'ADMIN') {
                    window.location.href = '/admin-dashboard.html';
                } else {
                    window.location.href = '/staff-dashboard.html';
                }
            }, 1000);
        } else {
            showAlert('Login gagal. Silakan coba lagi.', 'danger');
        }
    } catch (error) {
        console.error('Login error:', error);

        let errorMessage = 'Terjadi kesalahan. Silakan coba lagi.';
        if (error.response && error.response.status === 401) {
            errorMessage = 'Username atau password salah.';
        } else if (error.response && error.response.data && error.response.data.message) {
            errorMessage = error.response.data.message;
        }

        showAlert(errorMessage, 'danger');
    } finally {
        // Re-enable button
        loginBtn.disabled = false;
        spinner.style.display = 'none';
        btnText.textContent = 'Masuk';
    }
}

function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    const alertElement = document.createElement('div');
    alertElement.className = `alert alert-${type} alert-dismissible fade show`;
    alertElement.setAttribute('role', 'alert');

    alertElement.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    alertContainer.innerHTML = '';
    alertContainer.appendChild(alertElement);

    // Auto-dismiss after 5 seconds if it's a success message
    if (type === 'success') {
        setTimeout(() => {
            alertElement.remove();
        }, 5000);
    }
}

function checkExistingSession() {
    // Jangan auto-redirect dari login page agar role selection bisa dipilih
    const rememberMe = localStorage.getItem('rememberMe');
    if (rememberMe === 'true') {
        const rememberedUsername = localStorage.getItem('rememberedUsername');
        if (rememberedUsername) {
            document.getElementById('username').value = rememberedUsername;
            document.getElementById('rememberMe').checked = true;
        }
    }
}
