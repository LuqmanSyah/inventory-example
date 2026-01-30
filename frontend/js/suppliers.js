// Suppliers functionality
let allSuppliers = [];
let deleteSupplierId = null;

document.addEventListener("DOMContentLoaded", function () {
  loadSuppliers();
  setupEventListeners();
});

function setupEventListeners() {
  // Search input
  document
    .getElementById("searchInput")
    .addEventListener("input", filterSuppliers);
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
    tbody.innerHTML =
      '<tr><td colspan="7" class="text-center text-muted">No suppliers found</td></tr>';
    return;
  }

  tbody.innerHTML = suppliers
    .map(
      (supplier) => `
        <tr>
            <td>${supplier.id}</td>
            <td><strong>${supplier.name}</strong></td>
            <td>${supplier.email || "-"}</td>
            <td>${supplier.phoneNumber || "-"}</td>
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
}

async function openEditModal(id) {
  try {
    const response = await axios.get(`${API_ENDPOINTS.suppliers}/${id}`);
    const supplier = response.data;

    document.getElementById("modalTitle").textContent = "Edit Supplier";
    document.getElementById("supplierId").value = supplier.id;
    document.getElementById("supplierName").value = supplier.name;
    document.getElementById("supplierEmail").value = supplier.email || "";
    document.getElementById("supplierPhoneNumber").value = supplier.phoneNumber || "";
    document.getElementById("supplierAddress").value = supplier.address || "";
    document.getElementById("supplierDescription").value = supplier.description || "";

    const modal = new bootstrap.Modal(document.getElementById("supplierModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

async function saveSupplier() {
  console.log('saveSupplier called');
  
  const form = document.getElementById("supplierForm");
  console.log('Form found:', form);

  if (!form.checkValidity()) {
    console.log('Form validation failed');
    form.reportValidity();
    return;
  }

  const supplierId = document.getElementById("supplierId").value;
  const supplierData = {
    name: document.getElementById("supplierName").value,
    address: document.getElementById("supplierAddress").value,
    email: document.getElementById("supplierEmail").value || null,
    phoneNumber: document.getElementById("supplierPhoneNumber").value || null,
    description: document.getElementById("supplierDescription").value || null,
  };

  console.log('Supplier data:', supplierData);
  console.log('API endpoint:', API_ENDPOINTS.suppliers);

  try {
    if (supplierId) {
      // Update existing supplier
      console.log('Updating supplier:', supplierId);
      await axios.put(`${API_ENDPOINTS.suppliers}/${supplierId}`, supplierData);
      showAlert("Supplier updated successfully!", "success");
    } else {
      // Create new supplier
      console.log('Creating new supplier');
      await axios.post(API_ENDPOINTS.suppliers, supplierData);
      showAlert("Supplier created successfully!", "success");
    }

    // Close modal and reload suppliers
    const modal = bootstrap.Modal.getInstance(
      document.getElementById("supplierModal"),
    );
    modal.hide();
    loadSuppliers();
  } catch (error) {
    console.error('Save error:', error);
    handleError(error);
  }
}

function openDeleteModal(id) {
  const supplier = allSuppliers.find((s) => s.id === id);
  if (supplier) {
    deleteSupplierId = id;
    document.getElementById("deleteSupplierName").textContent = supplier.name;
    const modal = new bootstrap.Modal(document.getElementById("deleteModal"));
    modal.show();
  }
}

async function confirmDelete() {
  if (!deleteSupplierId) return;

  try {
    await axios.delete(`${API_ENDPOINTS.suppliers}/${deleteSupplierId}`);
    showAlert("Supplier deleted successfully!", "success");

    // Close modal and reload suppliers
    const modal = bootstrap.Modal.getInstance(
      document.getElementById("deleteModal"),
    );
    modal.hide();
    deleteSupplierId = null;
    loadSuppliers();
  } catch (error) {
    handleError(error);
  }
}
