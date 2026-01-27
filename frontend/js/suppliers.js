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
            <td>${supplier.contactPerson || "-"}</td>
            <td>${supplier.email || "-"}</td>
            <td>${supplier.phone || "-"}</td>
            <td>${supplier.address || "-"}</td>
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
      (supplier.contactPerson &&
        supplier.contactPerson.toLowerCase().includes(searchTerm)) ||
      (supplier.email && supplier.email.toLowerCase().includes(searchTerm)) ||
      (supplier.phone && supplier.phone.toLowerCase().includes(searchTerm)),
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
    document.getElementById("supplierContactPerson").value =
      supplier.contactPerson || "";
    document.getElementById("supplierEmail").value = supplier.email || "";
    document.getElementById("supplierPhone").value = supplier.phone || "";
    document.getElementById("supplierAddress").value = supplier.address || "";

    const modal = new bootstrap.Modal(document.getElementById("supplierModal"));
    modal.show();
  } catch (error) {
    handleError(error);
  }
}

async function saveSupplier() {
  const form = document.getElementById("supplierForm");

  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const supplierId = document.getElementById("supplierId").value;
  const supplierData = {
    name: document.getElementById("supplierName").value,
    contactPerson:
      document.getElementById("supplierContactPerson").value || null,
    email: document.getElementById("supplierEmail").value || null,
    phone: document.getElementById("supplierPhone").value || null,
    address: document.getElementById("supplierAddress").value || null,
  };

  try {
    if (supplierId) {
      // Update existing supplier
      await axios.put(`${API_ENDPOINTS.suppliers}/${supplierId}`, supplierData);
      showAlert("Supplier updated successfully!", "success");
    } else {
      // Create new supplier
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
