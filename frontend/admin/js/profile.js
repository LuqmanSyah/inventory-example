// Admin Profile functionality
let selectedPhotoFile = null;
let profileData = {
  fullName: "",
  email: "",
  phoneNumber: "",
  photoUrl: null,
};
let originalProfileData = {}; // Store original data for comparison

document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkAdminRole(); // Verify user is admin
  setUserDisplay(); // Set user display name
  setupLogoutHandler(); // Setup logout functionality
  setupEventListeners();
  loadProfileData();
  displayUserName();
});

function setupEventListeners() {
  // Photo upload area
  const photoUploadArea = document.getElementById("photoUploadArea");
  const photoInput = document.getElementById("photoInput");

  photoUploadArea.addEventListener("click", () => photoInput.click());

  photoInput.addEventListener("change", handlePhotoSelect);

  // Drag and drop
  photoUploadArea.addEventListener("dragover", (e) => {
    e.preventDefault();
    photoUploadArea.classList.add("dragover");
  });

  photoUploadArea.addEventListener("dragleave", () => {
    photoUploadArea.classList.remove("dragover");
  });

  photoUploadArea.addEventListener("drop", (e) => {
    e.preventDefault();
    photoUploadArea.classList.remove("dragover");

    const files = e.dataTransfer.files;
    if (files.length > 0) {
      photoInput.files = files;
      handlePhotoSelect();
    }
  });

  // Save button
  document.getElementById("saveBtn").addEventListener("click", saveProfile);

  // Real-time validation
  document.getElementById("email").addEventListener("blur", validateEmail);
  document.getElementById("phoneNumber").addEventListener("blur", validatePhoneNumber);

  // Track changes
  document.getElementById("fullName").addEventListener("input", checkForChanges);
  document.getElementById("email").addEventListener("input", checkForChanges);
  document.getElementById("phoneNumber").addEventListener("input", checkForChanges);
}

function validateEmail() {
  const emailInput = document.getElementById("email");
  const email = emailInput.value.trim();

  if (email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      emailInput.classList.add("is-invalid");
      emailInput.classList.remove("is-valid");
    } else {
      emailInput.classList.add("is-valid");
      emailInput.classList.remove("is-invalid");
    }
  } else {
    emailInput.classList.remove("is-invalid", "is-valid");
  }
}

function validatePhoneNumber() {
  const phoneInput = document.getElementById("phoneNumber");
  const phone = phoneInput.value.trim();

  if (phone) {
    const phoneRegex = /^(\+62|62|0)[0-9]{9,12}$/;
    if (!phoneRegex.test(phone.replace(/[\s-]/g, ""))) {
      phoneInput.classList.add("is-invalid");
      phoneInput.classList.remove("is-valid");
    } else {
      phoneInput.classList.add("is-valid");
      phoneInput.classList.remove("is-invalid");
    }
  } else {
    phoneInput.classList.remove("is-invalid", "is-valid");
  }
}

function checkForChanges() {
  const fullName = document.getElementById("fullName").value.trim();
  const email = document.getElementById("email").value.trim();
  const phoneNumber = document.getElementById("phoneNumber").value.trim();

  const hasChanges = fullName !== originalProfileData.fullName || email !== originalProfileData.email || phoneNumber !== (originalProfileData.phoneNumber || "");

  const saveBtn = document.getElementById("saveBtn");
  if (hasChanges) {
    saveBtn.classList.add("btn-warning");
    saveBtn.classList.remove("btn-primary");
  } else {
    saveBtn.classList.add("btn-primary");
    saveBtn.classList.remove("btn-warning");
  }
}

function handlePhotoSelect() {
  const photoInput = document.getElementById("photoInput");
  const file = photoInput.files[0];

  if (!file) return;

  // Validate file
  if (!file.type.startsWith("image/")) {
    showAlert("File harus berupa gambar (JPG, PNG, GIF)", "danger");
    photoInput.value = "";
    return;
  }

  if (file.size > 5 * 1024 * 1024) {
    showAlert("Ukuran file maksimal 5MB", "danger");
    photoInput.value = "";
    return;
  }

  // Store file
  selectedPhotoFile = file;

  // Preview
  const reader = new FileReader();
  reader.onload = (e) => {
    const profilePhoto = document.getElementById("profilePhoto");
    const photoPlaceholder = document.getElementById("photoPlaceholder");

    profilePhoto.src = e.target.result;
    profilePhoto.style.display = "block";
    photoPlaceholder.style.display = "none";

    profileData.photoUrl = e.target.result;
  };
  reader.readAsDataURL(file);
}

async function loadProfileData() {
  try {
    const userId = localStorage.getItem("inventori_user_id");

    if (!userId) {
      showAlert("User ID tidak ditemukan. Silakan login ulang.", "danger");
      return;
    }

    // Fetch full profile data from database
    // Try the new profile endpoint first, fallback to user endpoint
    let response;
    try {
      response = await axios.get(`${API_ENDPOINTS.auth}/profile?userId=${userId}`);
    } catch (err) {
      // Fallback to existing user endpoint
      console.log("Profile endpoint not available, using user endpoint");
      response = await axios.get(`${API_ENDPOINTS.auth}/user/${userId}`);
    }

    if (response.data) {
      profileData = {
        fullName: response.data.fullName || "",
        email: response.data.email || "",
        phoneNumber: response.data.phoneNumber || "",
        photoUrl: response.data.photoUrl || null,
      };

      // Store original data for comparison
      originalProfileData = { ...profileData };

      // Set username (read-only)
      document.getElementById("username").value = response.data.username || "";
      document.getElementById("profileName").textContent = response.data.fullName || response.data.username || "Admin";

      // Load existing profile data
      document.getElementById("fullName").value = profileData.fullName;
      document.getElementById("email").value = profileData.email;
      document.getElementById("phoneNumber").value = profileData.phoneNumber || "";

      if (profileData.photoUrl) {
        const profilePhoto = document.getElementById("profilePhoto");
        const photoPlaceholder = document.getElementById("photoPlaceholder");

        profilePhoto.src = profileData.photoUrl;
        profilePhoto.style.display = "block";
        photoPlaceholder.style.display = "none";
      }
    }
  } catch (error) {
    console.error("Error loading profile data:", error);
    showAlert("Gagal memuat data profil dari database: " + (error.response?.data?.message || error.message), "danger");
  }
}

async function displayUserName() {
  const userId = localStorage.getItem("inventori_user_id");

  if (!userId) return;

  try {
    // Try the new profile endpoint first, fallback to user endpoint
    let response;
    try {
      response = await axios.get(`${API_ENDPOINTS.auth}/profile?userId=${userId}`);
    } catch (err) {
      response = await axios.get(`${API_ENDPOINTS.auth}/user/${userId}`);
    }

    const userNameDisplay = document.getElementById("userNameDisplay");

    if (userNameDisplay && response.data) {
      userNameDisplay.textContent = response.data.fullName || response.data.username || "Admin";
    }
  } catch (error) {
    console.error("Error fetching user name:", error);
  }
}

async function saveProfile() {
  // Validate inputs
  const fullName = document.getElementById("fullName").value.trim();
  const email = document.getElementById("email").value.trim();
  const phoneNumber = document.getElementById("phoneNumber").value.trim();

  if (!fullName) {
    showAlert("Nama lengkap harus diisi", "danger");
    document.getElementById("fullName").focus();
    return;
  }

  if (!email) {
    showAlert("Email harus diisi", "danger");
    document.getElementById("email").focus();
    return;
  }

  // Validate email format
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    showAlert("Format email tidak valid", "danger");
    document.getElementById("email").focus();
    return;
  }

  // Validate phone number if provided (optional)
  if (phoneNumber) {
    const phoneRegex = /^(\+62|62|0)[0-9]{9,12}$/;
    if (!phoneRegex.test(phoneNumber.replace(/[\s-]/g, ""))) {
      showAlert("Format nomor telepon tidak valid. Gunakan format: 08xx atau +628xx", "danger");
      document.getElementById("phoneNumber").focus();
      return;
    }
  }

  // Check if there are any changes
  const hasChanges = fullName !== originalProfileData.fullName || email !== originalProfileData.email || phoneNumber !== (originalProfileData.phoneNumber || "");

  if (!hasChanges) {
    showAlert("Tidak ada perubahan data", "info");
    return;
  }

  // Show loading
  const saveBtn = document.getElementById("saveBtn");
  const spinner = document.getElementById("spinner");
  const saveBtnText = saveBtn.querySelector("span");
  const originalBtnText = saveBtnText.textContent;

  saveBtn.disabled = true;
  spinner.style.display = "inline-block";
  saveBtnText.textContent = " Menyimpan...";

  try {
    const userId = localStorage.getItem("inventori_user_id");

    if (!userId) {
      showAlert("User ID tidak ditemukan. Silakan login ulang.", "danger");
      saveBtn.disabled = false;
      spinner.style.display = "none";
      saveBtnText.textContent = originalBtnText;
      return;
    }

    // Update profile data
    const updateData = {
      fullName: fullName,
      email: email,
      phoneNumber: phoneNumber || null,
    };

    // Save to backend database
    // Try the new profile endpoint first, fallback to user endpoint
    try {
      await axios.put(`${API_ENDPOINTS.auth}/profile?userId=${userId}`, updateData);
    } catch (err) {
      // Fallback to existing user endpoint
      await axios.put(`${API_ENDPOINTS.auth}/user/${userId}`, updateData);
    }

    // Update original data after successful save
    originalProfileData = {
      fullName: fullName,
      email: email,
      phoneNumber: phoneNumber,
    };

    // Update display from fresh data
    document.getElementById("profileName").textContent = fullName;
    await displayUserName();

    // Reset button style
    saveBtn.classList.add("btn-primary");
    saveBtn.classList.remove("btn-warning");

    // Remove validation classes
    document.getElementById("email").classList.remove("is-invalid", "is-valid");
    document.getElementById("phoneNumber").classList.remove("is-invalid", "is-valid");

    showAlert("✓ Profil berhasil diperbarui!", "success");
  } catch (error) {
    console.error("Error saving profile:", error);
    const errorMessage = error.response?.data?.message || error.message || "Terjadi kesalahan";
    showAlert("Gagal menyimpan profil: " + errorMessage, "danger");
  } finally {
    // Hide loading
    saveBtn.disabled = false;
    spinner.style.display = "none";
    saveBtnText.textContent = originalBtnText;
  }
}
