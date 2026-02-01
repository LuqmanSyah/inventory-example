// Auth Helper - Membantu mengelola autentikasi dan logout

// Check if user is logged in, jika tidak redirect ke login
function checkAuth() {
  const user = localStorage.getItem("inventori_user");
  const userRole = localStorage.getItem("inventori_user_role");

  if (!user || !userRole) {
    window.location.href = "login.html";
    return null;
  }

  try {
    return JSON.parse(user);
  } catch (e) {
    window.location.href = "login.html";
    return null;
  }
}

// Check if user has admin role
function checkAdminRole() {
  const userRole = localStorage.getItem("inventori_user_role");
  if (userRole !== "ADMIN") {
    // Redirect to staff dashboard if not admin
    window.location.href = "../staff/dashboard.html";
    return false;
  }
  return true;
}

// Check if user has staff role
function checkStaffRole() {
  const userRole = localStorage.getItem("inventori_user_role");
  if (userRole !== "STAFF") {
    // Redirect to admin dashboard if not staff
    window.location.href = "../admin/dashboard.html";
    return false;
  }
  return true;
}

// Check if user is Admin
function isAdmin() {
  const userRole = localStorage.getItem("inventori_user_role");
  return userRole === "ADMIN";
}

// Get current user role
function getUserRole() {
  return localStorage.getItem("inventori_user_role");
}

// Hide Users menu for staff users (only ADMIN can access)
function hideUsersMenuForStaff() {
  const userRole = getUserRole();

  console.log("Checking user role for hiding Users menu:", userRole);

  // If user is STAFF, hide the Users menu
  if (userRole === "STAFF") {
    // Execute immediately and with timeout for reliability
    const hideMenu = () => {
      // Find all Users menu items
      const usersLinks = document.querySelectorAll('a[href="users.html"]');
      console.log("Found users links:", usersLinks.length);

      usersLinks.forEach((link) => {
        const navItem = link.closest("li.nav-item");
        if (navItem) {
          navItem.style.display = "none";
          console.log("Hidden users menu item");
        }
      });
    };

    // Execute immediately
    hideMenu();

    // Also execute with timeout to ensure DOM is ready
    setTimeout(hideMenu, 100);
  }
}

// Set user display name from database
async function setUserDisplay() {
  const user = checkAuth();
  const userId = localStorage.getItem("inventori_user_id");

  if (!userId) {
    // Fallback to localStorage if no userId
    if (user && document.getElementById("userNameDisplay")) {
      document.getElementById("userNameDisplay").textContent = user.fullName || user.username || "User";
    }
    // Hide users menu for staff
    hideUsersMenuForStaff();
    return;
  }

  try {
    // Fetch user data from database
    // Try the new profile endpoint first, fallback to user endpoint
    let response;
    try {
      response = await axios.get(`${API_ENDPOINTS.auth}/profile?userId=${userId}`);
    } catch (err) {
      response = await axios.get(`${API_ENDPOINTS.auth}/user/${userId}`);
    }

    if (response.data && document.getElementById("userNameDisplay")) {
      document.getElementById("userNameDisplay").textContent = response.data.fullName || response.data.username || "User";
    }
  } catch (error) {
    console.error("Error fetching user display:", error);
    // Fallback to localStorage if API fails
    if (user && document.getElementById("userNameDisplay")) {
      document.getElementById("userNameDisplay").textContent = user.fullName || user.username || "User";
    }
  }

  // Hide users menu for staff
  hideUsersMenuForStaff();
}

// Logout function
function logout() {
  localStorage.removeItem("inventori_user");
  localStorage.removeItem("inventori_user_id");
  localStorage.removeItem("inventori_user_role");
  localStorage.removeItem("inventori_remember_username");
  sessionStorage.clear();
  showAlert("Anda berhasil keluar dari sistem", "success");
  setTimeout(() => {
    window.location.href = "../login.html";
  }, 1500);
}

// Setup logout handler - for pages in admin/staff folders
function setupLogoutHandler() {
  const logoutBtn = document.getElementById("logoutBtn");
  const confirmLogoutBtn = document.getElementById("confirmLogoutBtn");
  const logoutModal = document.getElementById("logoutModal");

  if (logoutBtn) {
    logoutBtn.addEventListener("click", function (e) {
      e.preventDefault();
      if (logoutModal) {
        const modal = new bootstrap.Modal(logoutModal);
        modal.show();
      }
    });
  }

  if (confirmLogoutBtn) {
    confirmLogoutBtn.addEventListener("click", function () {
      logout();
    });
  }
}

// Enhanced showAlert function
function showAlert(message, type = "success") {
  // Coba cari alert container di halaman
  let alertContainer = document.querySelector('[role="alert"]');

  if (!alertContainer) {
    // Jika tidak ada, buat satu
    alertContainer = document.createElement("div");
    alertContainer.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    alertContainer.style.zIndex = "9999";
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

  if (type === "success") {
    setTimeout(() => {
      if (alertContainer && alertContainer.parentNode) {
        alertContainer.remove();
      }
    }, 3000);
  }
}

// Get dashboard URL based on user role
function getDashboardUrl() {
  const userRole = localStorage.getItem("inventori_user_role");
  if (userRole === "ADMIN") {
    return "admin/dashboard.html";
  } else {
    return "staff/dashboard.html";
  }
}

// Setup dashboard links based on user role
function setupDashboardLinks() {
  const dashboardUrl = getDashboardUrl();
  const userRole = getUserRole();

  // Update navbar brand link
  const dashboardLink = document.getElementById("dashboardLink");
  if (dashboardLink) {
    dashboardLink.href = dashboardUrl;
  }

  // Update dashboard nav link
  const navDashboardLink = document.getElementById("navDashboardLink");
  if (navDashboardLink) {
    navDashboardLink.href = dashboardUrl;
  }

  // Hide categories link for non-admin users
  const categoriesNavItem = document.getElementById("categoriesNavItem");
  if (categoriesNavItem && userRole !== "ADMIN") {
    categoriesNavItem.style.display = "none";
  }

  // Hide users menu for staff users
  hideUsersMenuForStaff();
}

// Setup logout button
document.addEventListener("DOMContentLoaded", function () {
  setUserDisplay();
  setupDashboardLinks(); // Setup dashboard links based on role

  // Ensure users menu is hidden for staff after DOM is loaded
  hideUsersMenuForStaff();

  const logoutBtn = document.getElementById("logoutBtn");
  const confirmLogoutBtn = document.getElementById("confirmLogoutBtn");
  const logoutModal = document.getElementById("logoutModal");

  if (logoutBtn) {
    logoutBtn.addEventListener("click", function (e) {
      e.preventDefault();
      if (logoutModal) {
        const modal = new bootstrap.Modal(logoutModal);
        modal.show();
      }
    });
  }

  if (confirmLogoutBtn) {
    confirmLogoutBtn.addEventListener("click", function () {
      logout();
    });
  }
});
