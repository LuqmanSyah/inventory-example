// Login functionality
document.addEventListener("DOMContentLoaded", function () {
  console.log("Login page loaded");
  console.log("API_BASE_URL:", typeof API_BASE_URL !== "undefined" ? API_BASE_URL : "UNDEFINED");

  // Check if user is already logged in
  const existingUser = localStorage.getItem("inventori_user");
  const existingRole = localStorage.getItem("inventori_user_role");

  if (existingUser && existingRole) {
    // User is already logged in, redirect to appropriate dashboard
    if (existingRole === "ADMIN") {
      window.location.href = "admin/dashboard.html";
    } else {
      window.location.href = "staff/dashboard.html";
    }
    return;
  }

  const loginForm = document.getElementById("loginForm");
  const togglePasswordBtn = document.getElementById("togglePassword");
  const passwordInput = document.getElementById("password");
  const rememberMeCheckbox = document.getElementById("rememberMe");
  const usernameInput = document.getElementById("username");

  if (!loginForm) {
    console.error("Login form not found!");
    return;
  }

  console.log("Login form found, attaching event listener");

  // Toggle password visibility
  togglePasswordBtn.addEventListener("click", function () {
    const type = passwordInput.getAttribute("type");
    if (type === "password") {
      passwordInput.setAttribute("type", "text");
      togglePasswordBtn.innerHTML = '<i class="bi bi-eye-slash"></i>';
      togglePasswordBtn.title = "Sembunyikan password";
    } else {
      passwordInput.setAttribute("type", "password");
      togglePasswordBtn.innerHTML = '<i class="bi bi-eye"></i>';
      togglePasswordBtn.title = "Tampilkan password";
    }
  });

  // Load saved username if remember me was checked
  const savedUsername = localStorage.getItem("inventori_remember_username");
  if (savedUsername) {
    usernameInput.value = savedUsername;
    rememberMeCheckbox.checked = true;
  }

  // Handle login form submission
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();
    console.log("Form submitted, preventDefault called");

    const username = usernameInput.value.trim();
    const password = passwordInput.value;
    const loginBtn = document.getElementById("loginBtn");
    const spinner = document.getElementById("spinner");
    const btnText = document.getElementById("btnText");

    // Validation
    if (!username || !password) {
      showAlert("Username dan password harus diisi", "danger");
      return;
    }

    // Save username if remember me is checked
    if (rememberMeCheckbox.checked) {
      localStorage.setItem("inventori_remember_username", username);
    } else {
      localStorage.removeItem("inventori_remember_username");
    }

    // Disable button and show spinner
    loginBtn.disabled = true;
    spinner.style.display = "inline-block";
    btnText.textContent = "Sedang masuk...";

    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        username: username,
        password: password,
      });

      const user = response.data;

      // Save user data to localStorage
      localStorage.setItem("inventori_user", JSON.stringify(user));
      localStorage.setItem("inventori_user_id", user.id);
      localStorage.setItem("inventori_user_role", user.role);

      // Mark first login in this session
      sessionStorage.setItem("isFirstLogin", "true");

      // Show success message
      showAlert("Login berhasil! Mengalihkan...", "success");

      // Redirect based on role to new folder structure
      setTimeout(() => {
        if (user.role === "ADMIN") {
          window.location.href = "admin/dashboard.html";
        } else {
          window.location.href = "staff/dashboard.html";
        }
      }, 1500);
    } catch (error) {
      console.error("Login error:", error);

      // Re-enable button
      loginBtn.disabled = false;
      spinner.style.display = "none";
      btnText.textContent = "Masuk";

      let errorMessage = "Terjadi kesalahan saat login";

      if (error.response) {
        if (error.response.status === 401) {
          errorMessage = "Username atau password salah";
        } else if (error.response.data && error.response.data.message) {
          errorMessage = error.response.data.message;
        }
      } else if (error.request) {
        errorMessage = "Tidak dapat terhubung ke server. Pastikan server sudah berjalan.";
      }

      showAlert(errorMessage, "danger");
    }
  });

  function showAlert(message, type) {
    const alertContainer = document.getElementById("alertContainer");
    const alertDiv = document.createElement("div");
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
    alertContainer.innerHTML = "";
    alertContainer.appendChild(alertDiv);

    if (type === "success") {
      setTimeout(() => {
        alertDiv.remove();
      }, 3000);
    }
  }
});
