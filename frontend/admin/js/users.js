// Admin Users Management - Full CRUD access (Admin only)
let allUsers = [];
let deleteUserId = null;
let currentUserId = null;
let currentUserRole = null;

document.addEventListener("DOMContentLoaded", function () {
  checkAuth();
  checkAdminRole();

  // Only SUPER_ADMIN can access user management - redirect others
  if (!checkSuperAdminRole()) {
    return; // Stop execution if not super admin
  }

  setUserDisplay();
  setupLogoutHandler();

  // Get current user ID and Role
  currentUserId = localStorage.getItem("inventori_user_id");
  currentUserRole = localStorage.getItem("inventori_user_role");

  loadUsers();
  loadUserStats();
  setupEventListeners();
  setupRoleOptions();
});

function setupEventListeners() {
  // Search input
  document.getElementById("searchInput").addEventListener("input", filterUsers);

  // Role filter
  document.getElementById("roleFilter").addEventListener("change", filterUsers);

  // Status filter
  document.getElementById("statusFilter").addEventListener("change", filterUsers);
}

// Setup role options based on current user's role
function setupRoleOptions() {
  const roleSelect = document.getElementById("role");
  const roleHint = document.getElementById("roleHint");
  const adminRoleOption = document.getElementById("adminRoleOption");

  // Only SUPER_ADMIN can create/edit ADMIN users
  if (currentUserRole === "SUPER_ADMIN") {
    // Super Admin can create Admin users
    if (adminRoleOption) {
      adminRoleOption.style.display = "block";
    }
    roleHint.textContent = "Sebagai Super Admin, Anda dapat membuat user Admin";
    roleHint.className = "form-text text-success";
  } else {
    // Regular Admin can only create Staff users
    if (adminRoleOption) {
      adminRoleOption.style.display = "none";
    }
    roleHint.textContent = "Admin hanya dapat membuat user Staff";
    roleHint.className = "form-text text-warning";
  }
}

async function loadUsers() {
  try {
    const response = await axios.get(API_ENDPOINTS.users);
    allUsers = response.data;
    displayUsers(allUsers);
  } catch (error) {
    handleError(error);
  }
}

async function loadUserStats() {
  try {
    const response = await axios.get(`${API_ENDPOINTS.users}/stats`);
    const stats = response.data;

    document.getElementById("totalUsers").textContent = stats.totalUsers || 0;
    // Combine Super Admin and Admin count for display, or show separately
    const totalAdmins = (stats.superAdminCount || 0) + (stats.adminCount || 0);
    document.getElementById("adminCount").textContent = totalAdmins;
    document.getElementById("staffCount").textContent = stats.staffCount || 0;
    document.getElementById("activeUsers").textContent = stats.activeUsers || 0;
  } catch (error) {
    console.error("Error loading user stats:", error);
  }
}

function displayUsers(users) {
  const tbody = document.getElementById("usersTableBody");

  if (users.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">Tidak ada user ditemukan</td></tr>';
    return;
  }

  tbody.innerHTML = users
    .map((user) => {
      const initials = getInitials(user.fullName || user.username);
      const isSuperAdmin = user.role === "SUPER_ADMIN";
      const isAdmin = user.role === "ADMIN";
      const avatarClass = isSuperAdmin ? "avatar-super-admin" : isAdmin ? "avatar-admin" : "avatar-staff";
      const roleBadgeClass = isSuperAdmin ? "bg-danger" : isAdmin ? "bg-primary" : "bg-secondary";
      const roleDisplay = isSuperAdmin ? "Super Admin" : user.role;
      const statusBadgeClass = user.isActive ? "bg-success" : "bg-danger";
      const statusText = user.isActive ? "Aktif" : "Tidak Aktif";
      const isCurrentUser = user.id.toString() === currentUserId;

      // Determine if edit/delete is allowed based on current user's role
      const canEdit = currentUserRole === "SUPER_ADMIN" || (currentUserRole === "ADMIN" && user.role === "STAFF");
      const canDelete = (currentUserRole === "SUPER_ADMIN" && !isSuperAdmin && !isCurrentUser) || (currentUserRole === "ADMIN" && user.role === "STAFF" && !isCurrentUser);

      return `
      <tr>
        <td>
          <div class="d-flex align-items-center">
            <div class="user-avatar ${avatarClass} me-2">${initials}</div>
            <div>
              <div class="fw-bold">${escapeHtml(user.fullName || "-")}</div>
              ${isCurrentUser ? '<small class="text-primary">(Anda)</small>' : ""}
            </div>
          </div>
        </td>
        <td>${escapeHtml(user.username)}</td>
        <td>${escapeHtml(user.email || "-")}</td>
        <td>${escapeHtml(user.phoneNumber || "-")}</td>
        <td>
          <span class="badge ${roleBadgeClass}">${roleDisplay}</span>
        </td>
        <td>
          <span class="badge status-badge ${statusBadgeClass}">${statusText}</span>
        </td>
        <td class="text-center">
          <div class="btn-group btn-group-sm">
            <button class="btn btn-outline-info" onclick="viewUser(${user.id})" title="Detail">
              <i class="bi bi-eye"></i>
            </button>
            <button class="btn btn-outline-warning" onclick="openEditModal(${user.id})" title="Edit" ${!canEdit ? "disabled" : ""}>
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-outline-danger" onclick="openDeleteModal(${user.id})" title="Hapus" ${!canDelete ? "disabled" : ""}>
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </td>
      </tr>
    `;
    })
    .join("");
}

function filterUsers() {
  const searchTerm = document.getElementById("searchInput").value.toLowerCase();
  const roleFilter = document.getElementById("roleFilter").value;
  const statusFilter = document.getElementById("statusFilter").value;

  let filtered = allUsers;

  // Search filter
  if (searchTerm) {
    filtered = filtered.filter(
      (user) =>
        (user.username && user.username.toLowerCase().includes(searchTerm)) ||
        (user.fullName && user.fullName.toLowerCase().includes(searchTerm)) ||
        (user.email && user.email.toLowerCase().includes(searchTerm)) ||
        (user.phoneNumber && user.phoneNumber.includes(searchTerm)),
    );
  }

  // Role filter
  if (roleFilter) {
    filtered = filtered.filter((user) => user.role === roleFilter);
  }

  // Status filter
  if (statusFilter !== "") {
    const isActive = statusFilter === "true";
    filtered = filtered.filter((user) => user.isActive === isActive);
  }

  displayUsers(filtered);
}

function openAddModal() {
  document.getElementById("modalTitle").textContent = "Tambah User";
  document.getElementById("userForm").reset();
  document.getElementById("userId").value = "";
  document.getElementById("username").disabled = false;
  document.getElementById("password").required = true;
  document.getElementById("passwordRequired").style.display = "inline";
  document.getElementById("passwordHint").textContent = "Minimal 6 karakter";
  document.getElementById("isActive").value = "true";
  document.getElementById("role").value = "STAFF";

  // Setup role options based on current user's role
  setupRoleOptions();
}

async function openEditModal(id) {
  try {
    const response = await axios.get(`${API_ENDPOINTS.users}/${id}`);
    const user = response.data;

    document.getElementById("modalTitle").textContent = "Edit User";
    document.getElementById("userId").value = user.id;
    document.getElementById("username").value = user.username;
    document.getElementById("username").disabled = true; // Username tidak bisa diubah
    document.getElementById("password").value = "";
    document.getElementById("password").required = false;
    document.getElementById("passwordRequired").style.display = "none";
    document.getElementById("passwordHint").textContent = "Kosongkan jika tidak ingin mengubah password";
    document.getElementById("fullName").value = user.fullName || "";
    document.getElementById("email").value = user.email || "";
    document.getElementById("phoneNumber").value = user.phoneNumber || "";
    document.getElementById("role").value = user.role;
    document.getElementById("isActive").value = user.isActive ? "true" : "false";

    // Setup role options and handle restrictions
    setupRoleOptions();

    // Disable role change for SUPER_ADMIN and ADMIN (if current user is not SUPER_ADMIN)
    const roleSelect = document.getElementById("role");
    if (user.role === "SUPER_ADMIN") {
      roleSelect.disabled = true;
      document.getElementById("roleHint").textContent = "Role Super Admin tidak dapat diubah";
      document.getElementById("roleHint").className = "form-text text-danger";
    } else if (user.role === "ADMIN" && currentUserRole !== "SUPER_ADMIN") {
      roleSelect.disabled = true;
      document.getElementById("roleHint").textContent = "Hanya Super Admin yang dapat mengubah role Admin";
      document.getElementById("roleHint").className = "form-text text-warning";
    } else {
      roleSelect.disabled = false;
    }

    const modal = new bootstrap.Modal(document.getElementById("userModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

async function saveUser() {
  const id = document.getElementById("userId").value;
  const password = document.getElementById("password").value;

  // Validasi password untuk user baru
  if (!id && (!password || password.length < 6)) {
    showAlert("Password minimal 6 karakter", "danger");
    return;
  }

  const userData = {
    username: document.getElementById("username").value,
    fullName: document.getElementById("fullName").value,
    email: document.getElementById("email").value,
    phoneNumber: document.getElementById("phoneNumber").value,
    role: document.getElementById("role").value,
    isActive: document.getElementById("isActive").value === "true",
  };

  // Tambahkan password jika diisi
  if (password) {
    userData.password = password;
  }

  try {
    const config = {
      headers: {
        "X-Requester-Id": currentUserId,
      },
    };

    if (id) {
      // Update existing user
      await axios.put(`${API_ENDPOINTS.users}/${id}`, userData, config);
      showAlert("User berhasil diperbarui!", "success");
    } else {
      // Create new user
      await axios.post(API_ENDPOINTS.users, userData, config);
      showAlert("User berhasil ditambahkan!", "success");
    }

    // Close modal and reload
    bootstrap.Modal.getInstance(document.getElementById("userModal")).hide();
    loadUsers();
    loadUserStats();
  } catch (error) {
    handleError(error);
  }
}

async function viewUser(id) {
  try {
    const response = await axios.get(`${API_ENDPOINTS.users}/${id}`);
    const user = response.data;

    const initials = getInitials(user.fullName || user.username);
    const isSuperAdmin = user.role === "SUPER_ADMIN";
    const isAdmin = user.role === "ADMIN";
    const avatarClass = isSuperAdmin ? "avatar-super-admin" : isAdmin ? "avatar-admin" : "avatar-staff";
    const roleBadgeClass = isSuperAdmin ? "bg-danger" : isAdmin ? "bg-primary" : "bg-secondary";
    const roleDisplay = isSuperAdmin ? "Super Admin" : user.role;

    document.getElementById("viewUserAvatar").className = `user-avatar mx-auto mb-3 ${avatarClass}`;
    document.getElementById("viewUserAvatar").style.cssText = "width: 80px; height: 80px; font-size: 2rem;";
    document.getElementById("viewUserAvatar").textContent = initials;
    document.getElementById("viewFullName").textContent = user.fullName || "-";
    document.getElementById("viewRoleBadge").className = `badge ${roleBadgeClass}`;
    document.getElementById("viewRoleBadge").textContent = roleDisplay;
    document.getElementById("viewUsername").textContent = user.username;
    document.getElementById("viewEmail").textContent = user.email || "-";
    document.getElementById("viewPhone").textContent = user.phoneNumber || "-";

    const statusBadgeClass = user.isActive ? "text-success" : "text-danger";
    const statusIcon = user.isActive ? "check-circle-fill" : "x-circle-fill";
    const statusText = user.isActive ? "Aktif" : "Tidak Aktif";
    document.getElementById("viewStatus").innerHTML = `<i class="bi bi-${statusIcon} ${statusBadgeClass}"></i> ${statusText}`;

    const modal = new bootstrap.Modal(document.getElementById("viewUserModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

async function toggleStatus(id) {
  try {
    await axios.patch(`${API_ENDPOINTS.users}/${id}/status`);
    showAlert("Status user berhasil diubah!", "success");
    loadUsers();
    loadUserStats();
  } catch (error) {
    handleError(error);
  }
}

function openDeleteModal(id) {
  deleteUserId = id;
  const user = allUsers.find((u) => u.id === id);
  document.getElementById("deleteUserName").textContent = user ? `${user.fullName} (${user.username})` : "";

  const modal = new bootstrap.Modal(document.getElementById("deleteModal"));
  modal.show();
}

async function confirmDelete() {
  if (!deleteUserId) return;

  try {
    const config = {
      headers: {
        "X-Requester-Id": currentUserId,
      },
    };
    await axios.delete(`${API_ENDPOINTS.users}/${deleteUserId}`, config);
    showAlert("User berhasil dihapus!", "success");

    bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
    loadUsers();
    loadUserStats();
  } catch (error) {
    handleError(error);
  }

  deleteUserId = null;
}

function togglePassword() {
  const passwordInput = document.getElementById("password");
  const passwordIcon = document.getElementById("passwordIcon");

  if (passwordInput.type === "password") {
    passwordInput.type = "text";
    passwordIcon.className = "bi bi-eye-slash";
  } else {
    passwordInput.type = "password";
    passwordIcon.className = "bi bi-eye";
  }
}

// Helper functions
function getInitials(name) {
  if (!name) return "?";
  return name
    .split(" ")
    .map((word) => word.charAt(0))
    .join("")
    .substring(0, 2)
    .toUpperCase();
}

function escapeHtml(text) {
  if (!text) return "";
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}
