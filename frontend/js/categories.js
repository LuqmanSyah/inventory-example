// Categories functionality
let allCategories = [];
let deleteCategoryId = null;

document.addEventListener('DOMContentLoaded', function() {
    checkAuth(); // Verify user is logged in
    setUserDisplay(); // Set user display name
    loadCategories();
    setupEventListeners();
});

function setupEventListeners() {
    // Search input
    document.getElementById('searchInput').addEventListener('input', filterCategories);
}

async function loadCategories() {
    try {
        const response = await axios.get(API_ENDPOINTS.categories);
        allCategories = response.data;
        displayCategories(allCategories);
    } catch (error) {
        handleError(error);
    }
}

function displayCategories(categories) {
    const container = document.getElementById('categoriesContainer');

    if (categories.length === 0) {
        container.innerHTML = '<div class="col-12 text-center text-muted"><p>No categories found</p></div>';
        return;
    }

    container.innerHTML = categories.map(category => `
        <div class="col-md-4 col-lg-3 mb-4">
            <div class="card h-100 shadow-sm">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <h5 class="card-title">
                            <i class="bi bi-tag text-primary"></i> ${category.name}
                        </h5>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-warning" onclick="openEditModal(${category.id})" title="Edit">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-outline-danger" onclick="openDeleteModal(${category.id})" title="Delete">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                    <p class="card-text text-muted">${category.description || 'No description'}</p>
                </div>
                <div class="card-footer bg-transparent">
                    <small class="text-muted">
                        <i class="bi bi-box"></i> ${category.productCount || 0} products
                    </small>
                </div>
            </div>
        </div>
    `).join('');
}

function filterCategories() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();

    if (!searchTerm) {
        displayCategories(allCategories);
        return;
    }

    const filtered = allCategories.filter(category =>
        category.name.toLowerCase().includes(searchTerm) ||
        (category.description && category.description.toLowerCase().includes(searchTerm))
    );

    displayCategories(filtered);
}

function openAddModal() {
    document.getElementById('modalTitle').textContent = 'Add Category';
    document.getElementById('categoryForm').reset();
    document.getElementById('categoryId').value = '';
}

async function openEditModal(id) {
    try {
        const response = await axios.get(`${API_ENDPOINTS.categories}/${id}`);
        const category = response.data;

        document.getElementById('modalTitle').textContent = 'Edit Category';
        document.getElementById('categoryId').value = category.id;
        document.getElementById('categoryName').value = category.name;
        document.getElementById('categoryDescription').value = category.description || '';

        const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
        modal.show();
    } catch (error) {
        handleError(error);
    }
}

async function saveCategory() {
    const form = document.getElementById('categoryForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const categoryId = document.getElementById('categoryId').value;
    const categoryData = {
        name: document.getElementById('categoryName').value,
        description: document.getElementById('categoryDescription').value || null
    };

    try {
        if (categoryId) {
            // Update existing category
            await axios.put(`${API_ENDPOINTS.categories}/${categoryId}`, categoryData);
            showAlert('Category updated successfully!', 'success');
        } else {
            // Create new category
            await axios.post(API_ENDPOINTS.categories, categoryData);
            showAlert('Category created successfully!', 'success');
        }

        // Close modal and reload categories
        const modal = bootstrap.Modal.getInstance(document.getElementById('categoryModal'));
        modal.hide();
        loadCategories();
    } catch (error) {
        handleError(error);
    }
}

function openDeleteModal(id) {
    const category = allCategories.find(c => c.id === id);
    if (category) {
        deleteCategoryId = id;
        document.getElementById('deleteCategoryName').textContent = category.name;
        const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
        modal.show();
    }
}

async function confirmDelete() {
    if (!deleteCategoryId) return;

    try {
        await axios.delete(`${API_ENDPOINTS.categories}/${deleteCategoryId}`);
        showAlert('Category deleted successfully!', 'success');

        // Close modal and reload categories
        const modal = bootstrap.Modal.getInstance(document.getElementById('deleteModal'));
        modal.hide();
        deleteCategoryId = null;
        loadCategories();
    } catch (error) {
        handleError(error);
    }
}
