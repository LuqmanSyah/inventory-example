// Admin Users Management - Full CRUD access (Admin only)
let allUsers = [];
let deleteUserId = null;
let currentUserId = null;

document.addEventListener("DOMContentLoaded", function () {
  checkAuth();
  checkAdminRole();
  setUserDisplay();
  setupLogoutHandler();

  // Get current user ID
  currentUserId = localStorage.getItem("inventori_user_id");

  loadUsers();
  loadUserStats();
  setupEventListeners();
});

function setupEventListeners() {
  // Search input
  document.getElementById("searchInput").addEventListener("input", filterUsers);

  // Role filter
  document.getElementById("roleFilter").addEventListener("change", filterUsers);

  // Status filter
  document.getElementById("statusFilter").addEventListener("change", filterUsers);
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
    document.getElementById("adminCount").textContent = stats.adminCount || 0;
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
      const avatarClass = user.role === "ADMIN" ? "avatar-admin" : "avatar-staff";
      const roleBadgeClass = user.role === "ADMIN" ? "bg-primary" : "bg-secondary";
      const statusBadgeClass = user.isActive ? "bg-success" : "bg-danger";
      const statusText = user.isActive ? "Aktif" : "Tidak Aktif";
      const isCurrentUser = user.id.toString() === currentUserId;

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
          <span class="badge ${roleBadgeClass}">${user.role}</span>
        </td>
        <td>
          <span class="badge status-badge ${statusBadgeClass}">${statusText}</span>
        </td>
        <td class="text-center">
          <div class="btn-group btn-group-sm">
            <button class="btn btn-outline-info" onclick="viewUser(${user.id})" title="Detail">
              <i class="bi bi-eye"></i>
            </button>
            <button class="btn btn-outline-warning" onclick="openEditModal(${user.id})" title="Edit">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-outline-danger" onclick="openDeleteModal(${user.id})" title="Hapus" ${isCurrentUser ? "disabled" : ""}>
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
    if (id) {
      // Update existing user
      await axios.put(`${API_ENDPOINTS.users}/${id}`, userData);
      showAlert("User berhasil diperbarui!", "success");
    } else {
      // Create new user
      await axios.post(API_ENDPOINTS.users, userData);
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
    const avatarClass = user.role === "ADMIN" ? "avatar-admin" : "avatar-staff";
    const roleBadgeClass = user.role === "ADMIN" ? "bg-primary" : "bg-secondary";

    document.getElementById("viewUserAvatar").className = `user-avatar mx-auto mb-3 ${avatarClass}`;
    document.getElementById("viewUserAvatar").style.cssText = "width: 80px; height: 80px; font-size: 2rem;";
    document.getElementById("viewUserAvatar").textContent = initials;
    document.getElementById("viewFullName").textContent = user.fullName || "-";
    document.getElementById("viewRoleBadge").className = `badge ${roleBadgeClass}`;
    document.getElementById("viewRoleBadge").textContent = user.role;
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
    await axios.delete(`${API_ENDPOINTS.users}/${deleteUserId}`);
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
