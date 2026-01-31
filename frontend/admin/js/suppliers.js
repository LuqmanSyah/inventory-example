// Admin Suppliers functionality - Full CRUD access
let allSuppliers = [];
let deleteSupplierId = null;

document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkAdminRole(); // Only admin can access full supplier management
  setUserDisplay(); // Set user display name
  setupLogoutHandler(); // Setup logout functionality
  loadSuppliers();
  setupEventListeners();
});

function setupEventListeners() {
  // Search input
  document.getElementById("searchInput").addEventListener("input", filterSuppliers);

  // Real-time validation for phone and email
  document.getElementById("supplierPhoneNumber").addEventListener("input", function () {
    const validation = validatePhoneNumber(this.value);
    if (this.value && !validation.valid) {
      showFieldError("supplierPhoneNumber", validation.message);
    } else {
      clearFieldError("supplierPhoneNumber");
    }
  });

  document.getElementById("supplierEmail").addEventListener("input", function () {
    const validation = validateEmail(this.value);
    if (!validation.valid) {
      showFieldError("supplierEmail", validation.message);
    } else {
      clearFieldError("supplierEmail");
    }
  });
}

async function loadSuppliers() {
  try {
    const response = await axios.get(API_ENDPOINTS.suppliers);
    allSuppliers = response.data;
    displaySuppliers(allSuppliers);
  } catch (error) {
    handleError(error);
  }
}

function displaySuppliers(suppliers) {
  const tbody = document.getElementById("suppliersTableBody");

  if (suppliers.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No suppliers found</td></tr>';
    return;
  }

  tbody.innerHTML = suppliers
    .map(
      (supplier, index) => `
        <tr>
            <td>${index + 1}</td>
            <td><strong>${supplier.name}</strong></td>
            <td>${supplier.phoneNumber || "-"}</td>
            <td>${supplier.email || "-"}</td>
            <td>${supplier.address || "-"}</td>
            <td>${supplier.description || "-"}</td>
            <td>
                <button class="btn btn-sm btn-warning" onclick="openEditModal(${supplier.id})" title="Edit">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="openDeleteModal(${supplier.id})" title="Delete">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `,
    )
    .join("");
}

function filterSuppliers() {
  const searchTerm = document.getElementById("searchInput").value.toLowerCase();

  if (!searchTerm) {
    displaySuppliers(allSuppliers);
    return;
  }

  const filtered = allSuppliers.filter(
    (supplier) =>
      supplier.name.toLowerCase().includes(searchTerm) ||
      (supplier.email && supplier.email.toLowerCase().includes(searchTerm)) ||
      (supplier.phoneNumber && supplier.phoneNumber.toLowerCase().includes(searchTerm)) ||
      (supplier.description && supplier.description.toLowerCase().includes(searchTerm)),
  );

  displaySuppliers(filtered);
}

function openAddModal() {
  document.getElementById("modalTitle").textContent = "Add Supplier";
  document.getElementById("supplierForm").reset();
  document.getElementById("supplierId").value = "";
  clearAllFieldErrors();
}

async function openEditModal(id) {
  try {
    clearAllFieldErrors();
    const response = await axios.get(`${API_ENDPOINTS.suppliers}/${id}`);
    const supplier = response.data;

    document.getElementById("modalTitle").textContent = "Edit Supplier";
    document.getElementById("supplierId").value = supplier.id;
    document.getElementById("supplierName").value = supplier.name || "";
    document.getElementById("supplierPhoneNumber").value = supplier.phoneNumber || "";
    document.getElementById("supplierEmail").value = supplier.email || "";
    document.getElementById("supplierAddress").value = supplier.address || "";
    document.getElementById("supplierDescription").value = supplier.description || "";

    const modal = new bootstrap.Modal(document.getElementById("supplierModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

// Validation functions for phone and email
function validatePhoneNumber(phone) {
  if (!phone || phone.trim() === "") {
    return { valid: true, message: "" }; // Phone is optional
  }
  const phoneRegex = /^(\+62|62|0)[0-9]{8,13}$/;
  if (!phoneRegex.test(phone)) {
    return { valid: false, message: "Format nomor telepon tidak valid. Gunakan format: 08xx, 62xx, atau +62xx (9-14 digit)" };
  }
  return { valid: true, message: "" };
}

function validateEmail(email) {
  if (!email || email.trim() === "") {
    return { valid: false, message: "Email tidak boleh kosong" };
  }
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!emailRegex.test(email)) {
    return { valid: false, message: "Format email tidak valid" };
  }
  return { valid: true, message: "" };
}

function showFieldError(fieldId, message) {
  const field = document.getElementById(fieldId);
  field.classList.add("is-invalid");

  // Remove existing error message if any
  const existingError = field.parentElement.querySelector(".invalid-feedback");
  if (existingError) {
    existingError.remove();
  }

  // Add new error message
  const errorDiv = document.createElement("div");
  errorDiv.className = "invalid-feedback";
  errorDiv.textContent = message;
  field.parentElement.appendChild(errorDiv);
}

function clearFieldError(fieldId) {
  const field = document.getElementById(fieldId);
  field.classList.remove("is-invalid");
  const existingError = field.parentElement.querySelector(".invalid-feedback");
  if (existingError) {
    existingError.remove();
  }
}

function clearAllFieldErrors() {
  clearFieldError("supplierPhoneNumber");
  clearFieldError("supplierEmail");
}

async function saveSupplier() {
  const form = document.getElementById("supplierForm");
  clearAllFieldErrors();

  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  // Custom validation for phone and email
  const phoneNumber = document.getElementById("supplierPhoneNumber").value;
  const email = document.getElementById("supplierEmail").value;

  const phoneValidation = validatePhoneNumber(phoneNumber);
  const emailValidation = validateEmail(email);

  let hasErrors = false;

  if (!phoneValidation.valid) {
    showFieldError("supplierPhoneNumber", phoneValidation.message);
    hasErrors = true;
  }

  if (!emailValidation.valid) {
    showFieldError("supplierEmail", emailValidation.message);
    hasErrors = true;
  }

  if (hasErrors) {
    return;
  }

  const id = document.getElementById("supplierId").value;
  const supplierData = {
    name: document.getElementById("supplierName").value,
    address: document.getElementById("supplierAddress").value,
    phoneNumber: phoneNumber || null,
    email: email || null,
    description: document.getElementById("supplierDescription").value || null,
  };

  console.log("Supplier data:", supplierData);
  console.log("API endpoint:", API_ENDPOINTS.suppliers);

  try {
    if (id) {
      // Update existing supplier
      await axios.put(`${API_ENDPOINTS.suppliers}/${id}`, supplierData);
      showAlert("Supplier updated successfully!", "success");
    } else {
      // Create new supplier
      console.log("Creating new supplier");
      await axios.post(API_ENDPOINTS.suppliers, supplierData);
      showAlert("Supplier created successfully!", "success");
    }

    // Close modal and reload suppliers
    bootstrap.Modal.getInstance(document.getElementById("supplierModal")).hide();
    loadSuppliers();
  } catch (error) {
    console.error("Save error:", error);
    handleError(error);
  }
}

function openDeleteModal(id) {
  deleteSupplierId = id;
  const supplier = allSuppliers.find((s) => s.id === id);
  document.getElementById("deleteSupplierName").textContent = supplier ? supplier.name : "";

  const modal = new bootstrap.Modal(document.getElementById("deleteModal"));
  modal.show();
}

async function confirmDelete() {
  if (!deleteSupplierId) return;

  try {
    await axios.delete(`${API_ENDPOINTS.suppliers}/${deleteSupplierId}`);
    showAlert("Supplier deleted successfully!", "success");

    bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
    loadSuppliers();
  } catch (error) {
    handleError(error);
  }

  deleteSupplierId = null;
}
