let selectedPhotoFile = null;
let profileData = {
    fullName: '',
    email: '',
    phoneNumber: '',
    photoUrl: null
};

document.addEventListener('DOMContentLoaded', function() {
    checkAuth(); // Verify user is logged in
    setupEventListeners();
    loadProfileData();
    displayUserName();
});

function setupEventListeners() {
    // Photo upload area
    const photoUploadArea = document.getElementById('photoUploadArea');
    const photoInput = document.getElementById('photoInput');

    photoUploadArea.addEventListener('click', () => photoInput.click());

    photoInput.addEventListener('change', handlePhotoSelect);

    // Drag and drop
    photoUploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        photoUploadArea.classList.add('dragover');
    });

    photoUploadArea.addEventListener('dragleave', () => {
        photoUploadArea.classList.remove('dragover');
    });

    photoUploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        photoUploadArea.classList.remove('dragover');
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            photoInput.files = files;
            handlePhotoSelect();
        }
    });

    // Save button
    document.getElementById('saveBtn').addEventListener('click', saveProfile);

    // Logout
    setupLogoutHandler();
}

function handlePhotoSelect() {
    const photoInput = document.getElementById('photoInput');
    const file = photoInput.files[0];

    if (!file) return;

    // Validate file
    if (!file.type.startsWith('image/')) {
        showAlert('File harus berupa gambar (JPG, PNG, GIF)', 'danger');
        photoInput.value = '';
        return;
    }

    if (file.size > 5 * 1024 * 1024) {
        showAlert('Ukuran file maksimal 5MB', 'danger');
        photoInput.value = '';
        return;
    }

    // Store file
    selectedPhotoFile = file;

    // Preview
    const reader = new FileReader();
    reader.onload = (e) => {
        const profilePhoto = document.getElementById('profilePhoto');
        const photoPlaceholder = document.getElementById('photoPlaceholder');

        profilePhoto.src = e.target.result;
        profilePhoto.style.display = 'block';
        photoPlaceholder.style.display = 'none';

        profileData.photoUrl = e.target.result;
    };
    reader.readAsDataURL(file);
}

async function loadProfileData() {
    try {
        const user = JSON.parse(localStorage.getItem('user') || '{}');

        // Set username (read-only)
        document.getElementById('username').value = user.username || '';
        
        // Try to fetch full profile data from backend
        try {
            const response = await axios.get(
                `${API_ENDPOINTS.baseURL}/api/auth/profile`,
                {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                }
            );

            if (response.data) {
                profileData = {
                    fullName: response.data.fullName || '',
                    email: response.data.email || '',
                    phoneNumber: response.data.phoneNumber || '',
                    photoUrl: response.data.photoUrl || null
                };

                // Load existing profile data
                document.getElementById('fullName').value = profileData.fullName;
                document.getElementById('email').value = profileData.email;
                document.getElementById('phoneNumber').value = profileData.phoneNumber;

                if (profileData.photoUrl) {
                    const profilePhoto = document.getElementById('profilePhoto');
                    const photoPlaceholder = document.getElementById('photoPlaceholder');

                    profilePhoto.src = profileData.photoUrl;
                    profilePhoto.style.display = 'block';
                    photoPlaceholder.style.display = 'none';
                }
            }
        } catch (error) {
            // If endpoint doesn't exist yet, just use localStorage data
            console.log('Profile endpoint not available yet');
        }

    } catch (error) {
        console.error('Error loading profile data:', error);
        showAlert('Gagal memuat data profil', 'danger');
    }
}

async function saveProfile() {
    // Validate inputs
    const fullName = document.getElementById('fullName').value.trim();
    const email = document.getElementById('email').value.trim();
    const phoneNumber = document.getElementById('phoneNumber').value.trim();

    if (!fullName) {
        showAlert('Nama lengkap harus diisi', 'danger');
        return;
    }

    if (!email) {
        showAlert('Email harus diisi', 'danger');
        return;
    }

    if (!validateEmail(email)) {
        showAlert('Format email tidak valid', 'danger');
        return;
    }

    if (phoneNumber && !validatePhoneNumber(phoneNumber)) {
        showAlert('Format nomor telepon tidak valid (gunakan format 08...)', 'danger');
        return;
    }

    // Update profile data
    profileData.fullName = fullName;
    profileData.email = email;
    profileData.phoneNumber = phoneNumber;

    // Save to localStorage (for now, until backend endpoint is created)
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    user.fullName = fullName;
    user.email = email;
    user.phoneNumber = phoneNumber;
    if (profileData.photoUrl) {
        user.photoUrl = profileData.photoUrl;
    }
    localStorage.setItem('user', JSON.stringify(user));

    // Update display
    document.getElementById('profileName').textContent = fullName;

    // Show success message
    showAlert('Profil berhasil disimpan!', 'success');

    // Reset photo input
    selectedPhotoFile = null;
    document.getElementById('photoInput').value = '';

    // Optional: Send to backend when endpoint is available
    // await sendProfileToBackend();
}

async function sendProfileToBackend() {
    try {
        const saveBtn = document.getElementById('saveBtn');
        const spinner = document.getElementById('spinner');
        const btnText = saveBtn.querySelector('span:last-child');

        saveBtn.disabled = true;
        spinner.style.display = 'inline-block';
        btnText.textContent = 'Menyimpan...';

        const formData = new FormData();
        formData.append('fullName', profileData.fullName);
        formData.append('email', profileData.email);
        formData.append('phoneNumber', profileData.phoneNumber);

        if (selectedPhotoFile) {
            formData.append('photo', selectedPhotoFile);
        }

        const response = await axios.post(
            `${API_ENDPOINTS.baseURL}/api/auth/profile`,
            formData,
            {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'multipart/form-data'
                }
            }
        );

        if (response.data) {
            showAlert('Profil berhasil disimpan ke server!', 'success');
        }

    } catch (error) {
        console.error('Error saving profile:', error);
        showAlert('Gagal menyimpan profil ke server', 'danger');
    } finally {
        const saveBtn = document.getElementById('saveBtn');
        const spinner = document.getElementById('spinner');
        const btnText = saveBtn.querySelector('span:last-child');

        saveBtn.disabled = false;
        spinner.style.display = 'none';
        btnText.textContent = 'Simpan Perubahan';
    }
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function validatePhoneNumber(phoneNumber) {
    // Basic validation: starts with 08 and has 10-12 digits
    const phoneRegex = /^08\d{8,10}$/;
    return phoneRegex.test(phoneNumber.replace(/[^0-9]/g, ''));
}

function displayUserName() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const displayName = user.fullName || user.username || 'Admin';
    
    document.getElementById('userNameDisplay').textContent = displayName;
    document.getElementById('profileName').textContent = displayName;
    document.getElementById('profileRole').textContent = 'Administator Sistem';
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

    // Auto-dismiss success alerts
    if (type === 'success') {
        setTimeout(() => {
            alertElement.remove();
        }, 4000);
    }
}

function setupLogoutHandler() {
    const logoutBtn = document.getElementById('logoutBtn');
    const logoutModal = new bootstrap.Modal(document.getElementById('logoutModal'));
    const confirmLogoutBtn = document.getElementById('confirmLogoutBtn');

    logoutBtn.addEventListener('click', (e) => {
        e.preventDefault();
        logoutModal.show();
    });

    confirmLogoutBtn.addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('userRole');
        sessionStorage.clear();
        window.location.href = 'login.html';
    });
}
