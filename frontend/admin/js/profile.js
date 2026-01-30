// Admin Profile functionality
let selectedPhotoFile = null;
let profileData = {
  fullName: "",
  email: "",
  phoneNumber: "",
  photoUrl: null,
};

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
    const user = JSON.parse(localStorage.getItem("inventori_user") || "{}");

    // Set username (read-only)
    document.getElementById("username").value = user.username || "";
    document.getElementById("profileName").textContent = user.fullName || user.username || "Admin";

    // Try to fetch full profile data from backend
    try {
      const response = await axios.get(`${API_ENDPOINTS.auth}/profile`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });

      if (response.data) {
        profileData = {
          fullName: response.data.fullName || "",
          email: response.data.email || "",
          phoneNumber: response.data.phoneNumber || "",
          photoUrl: response.data.photoUrl || null,
        };

        // Load existing profile data
        document.getElementById("fullName").value = profileData.fullName;
        document.getElementById("email").value = profileData.email;
        document.getElementById("phoneNumber").value = profileData.phoneNumber;

        if (profileData.photoUrl) {
          const profilePhoto = document.getElementById("profilePhoto");
          const photoPlaceholder = document.getElementById("photoPlaceholder");

          profilePhoto.src = profileData.photoUrl;
          profilePhoto.style.display = "block";
          photoPlaceholder.style.display = "none";
        }
      }
    } catch (error) {
      // If endpoint doesn't exist yet, just use localStorage data
      console.log("Profile endpoint not available yet");
      document.getElementById("fullName").value = user.fullName || "";
      document.getElementById("email").value = user.email || "";
    }
  } catch (error) {
    console.error("Error loading profile data:", error);
    showAlert("Gagal memuat data profil", "danger");
  }
}

function displayUserName() {
  const user = JSON.parse(localStorage.getItem("inventori_user") || "{}");
  const userNameDisplay = document.getElementById("userNameDisplay");
  if (userNameDisplay) {
    userNameDisplay.textContent = user.fullName || user.username || "Admin";
  }
}

async function saveProfile() {
  // Validate inputs
  const fullName = document.getElementById("fullName").value.trim();
  const email = document.getElementById("email").value.trim();
  const phoneNumber = document.getElementById("phoneNumber").value.trim();

  if (!fullName) {
    showAlert("Nama lengkap harus diisi", "danger");
    return;
  }

  // Show loading
  const saveBtn = document.getElementById("saveBtn");
  const spinner = document.getElementById("spinner");
  saveBtn.disabled = true;
  spinner.style.display = "inline-block";

  try {
    // Update profile data
    const updateData = {
      fullName: fullName,
      email: email,
      phoneNumber: phoneNumber,
    };

    // Try to save to backend
    try {
      await axios.put(`${API_ENDPOINTS.auth}/profile`, updateData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
    } catch (error) {
      console.log("Profile update endpoint not available yet");
    }

    // Update local storage
    const user = JSON.parse(localStorage.getItem("inventori_user") || "{}");
    user.fullName = fullName;
    user.email = email;
    localStorage.setItem("inventori_user", JSON.stringify(user));

    // Update display
    document.getElementById("profileName").textContent = fullName;
    displayUserName();

    showAlert("Profil berhasil disimpan!", "success");
  } catch (error) {
    console.error("Error saving profile:", error);
    showAlert("Gagal menyimpan profil", "danger");
  } finally {
    // Hide loading
    saveBtn.disabled = false;
    spinner.style.display = "none";
  }
}
