// Staff Profile functionality (View Only)
document.addEventListener("DOMContentLoaded", function () {
  checkAuth(); // Verify user is logged in
  checkStaffRole(); // Verify user is staff
  setUserDisplay(); // Set user display name
  setupLogoutHandler(); // Setup logout functionality
  loadProfileData();
});

function checkStaffRole() {
  const userRole = localStorage.getItem("inventori_user_role");
  if (userRole === "ADMIN") {
    // Redirect to admin profile if admin
    window.location.href = "../admin/profile.html";
    return false;
  }
  return true;
}

async function loadProfileData() {
  try {
    const userId = localStorage.getItem("inventori_user_id");
    const localUser = JSON.parse(localStorage.getItem("inventori_user") || "{}");

    if (!userId) {
      showAlert("User ID tidak ditemukan. Silakan login ulang.", "danger");
      return;
    }

    let profileData = null;

    // Try to fetch profile data from API
    try {
      let response;
      try {
        response = await axios.get(`${API_ENDPOINTS.auth}/profile?userId=${userId}`);
      } catch (err) {
        // Fallback to existing user endpoint
        console.log("Profile endpoint not available, using user endpoint");
        response = await axios.get(`${API_ENDPOINTS.auth}/user/${userId}`);
      }
      
      if (response && response.data) {
        profileData = response.data;
      }
    } catch (apiError) {
      console.log("API not available, using localStorage data");
    }

    // If API failed, use localStorage data
    if (!profileData) {
      profileData = {
        fullName: localUser.fullName || "",
        username: localUser.username || "",
        email: localUser.email || "",
        phoneNumber: localUser.phoneNumber || ""
      };
    }

    // Update header
    document.getElementById("profileName").textContent = profileData.fullName || profileData.username || "Staff";

    // Set username (read-only)
    document.getElementById("username").value = profileData.username || "";

    // Load existing profile data into form fields
    document.getElementById("fullName").value = profileData.fullName || "";
    document.getElementById("email").value = profileData.email || "";
    document.getElementById("phoneNumber").value = profileData.phoneNumber || "";

    // Update user display in navbar
    const userNameDisplay = document.getElementById("userNameDisplay");
    if (userNameDisplay) {
      userNameDisplay.textContent = profileData.fullName || profileData.username || "Staff";
    }
  } catch (error) {
    console.error("Error loading profile data:", error);
    
    // Last fallback: try localStorage
    try {
      const localUser = JSON.parse(localStorage.getItem("inventori_user") || "{}");
      document.getElementById("profileName").textContent = localUser.fullName || localUser.username || "Staff";
      document.getElementById("username").value = localUser.username || "";
      document.getElementById("fullName").value = localUser.fullName || "";
      document.getElementById("email").value = localUser.email || "";
      document.getElementById("phoneNumber").value = localUser.phoneNumber || "";
    } catch (localError) {
      showAlert("Gagal memuat data profil: " + (error.response?.data?.message || error.message), "danger");
    }
  }
}
