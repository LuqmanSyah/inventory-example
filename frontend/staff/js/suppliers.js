// Staff Suppliers functionality - View only (no add/edit/delete)
let allSuppliers = [];

document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkStaffRole(); // Verify user is staff
  setUserDisplay(); // Set user display name
  setupLogoutHandler(); // Setup logout functionality
  loadSuppliers();
  setupEventListeners();
});

function setupEventListeners() {
  // Search input
  document.getElementById("searchInput").addEventListener("input", filterSuppliers);
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
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No suppliers found</td></tr>';
    return;
  }

  // Staff view - no action buttons
  tbody.innerHTML = suppliers
    .map(
      (supplier) => `
        <tr>
            <td>${supplier.id}</td>
            <td><strong>${supplier.name}</strong></td>
            <td>${supplier.phoneNumber || "-"}</td>
            <td>${supplier.email || "-"}</td>
            <td>${supplier.address || "-"}</td>
            <td>${supplier.description || "-"}</td>
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
    (supplier) => supplier.name.toLowerCase().includes(searchTerm) || (supplier.email && supplier.email.toLowerCase().includes(searchTerm)) || (supplier.phoneNumber && supplier.phoneNumber.toLowerCase().includes(searchTerm)),
  );

  displaySuppliers(filtered);
}
