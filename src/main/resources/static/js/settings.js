const API_BASE = `${window.origin}`;
let currentUsername = '';
let currentEmail = '';

function togglePasswordVisibility(inputId) {
    const input = document.getElementById(inputId);
    const button = input.parentElement.querySelector('.password-toggle');
    const icon = button.querySelector('i');

    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('ri-eye-line');
        icon.classList.add('ri-eye-off-line');
    } else {
        input.type = 'password';
        icon.classList.remove('ri-eye-off-line');
        icon.classList.add('ri-eye-line');
    }
}

// Close modal when clicking outside
document.addEventListener('DOMContentLoaded', function() {
    // Render header
    renderHeader({
        showUsername: false,
        buttons: [
            HeaderButtons.back(),
            HeaderButtons.logout()
        ]
    });


    const deleteModal = document.getElementById('delete-modal');
    if (deleteModal) {
        deleteModal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeDeleteModal();
            }
        });
    }

    // Initialize page data
    initPage();

    // Enter key on delete confirmation fields
    const deletePasswordField = document.getElementById('delete-confirm-password');
    if (deletePasswordField) {
        deletePasswordField.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                confirmDelete();
            }
        });
    }
});

// Initialize page
async function initPage() {
    const storedUser = sessionStorage.getItem('astronaut_user');

    if (storedUser) {
        const userData = JSON.parse(storedUser);
        currentUsername = userData.username;
        currentEmail = userData.email;

        document.getElementById('username-display').textContent = currentUsername;
        document.getElementById('email-display').textContent = currentEmail;
    } else {
        showToast('error', 'Session Expired', 'Please log in again');
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 2000);
    }
}

// Fetch current user data
async function fetchUserData() {
    try {
        const response = await fetch(`${API_BASE}/users/me`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/index.html';
                return;
            }
            throw new Error('Failed to fetch user data');
        }

        const userData = await response.json();
        currentUsername = userData.username;
        currentEmail = userData.email;

        document.getElementById('username-display').textContent = currentUsername;
        document.getElementById('email-display').textContent = currentEmail;
    } catch (error) {
        console.error('Error fetching user data:', error);
        showToast('error', 'Load Failed', 'Failed to load user data');
    }
}

// Toggle Username Edit
function toggleUsernameEdit() {
    const editRow = document.getElementById('username-edit-row');
    const input = document.getElementById('username-input');

    if (editRow.style.display === 'none') {
        editRow.style.display = 'flex';
        input.value = currentUsername;
        input.focus();
    } else {
        editRow.style.display = 'none';
    }
}

// Save Username
async function saveUsername() {
    const input = document.getElementById('username-input');
    const newUsername = input.value.trim();

    if (!newUsername) {
        showToast('error', 'Invalid Input', 'Username cannot be empty');
        return;
    }

    if (newUsername.length < 1) {
        showToast('error', 'Invalid Input', 'Username must be at least 1 character');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/users/me`, {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: newUsername,
                email: currentEmail
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update username');
        }

        // Update local variables and DOM
        currentUsername = newUsername;
        document.getElementById('username-display').textContent = newUsername;
        document.getElementById('username-edit-row').style.display = 'none';

        // Update sessionStorage
        sessionStorage.setItem('astronaut_user', JSON.stringify({
            username: newUsername,
            email: currentEmail
        }));

        showToast('success', 'Username Updated', 'Your username has been changed successfully');
    } catch (error) {
        console.error('Error updating username:', error);
        showToast('error', 'Update Failed', error.message || 'Failed to update username');
    }
}

// Toggle Email Edit
function toggleEmailEdit() {
    const editRow = document.getElementById('email-edit-row');
    const input = document.getElementById('email-input');

    if (editRow.style.display === 'none') {
        editRow.style.display = 'flex';
        input.value = currentEmail;
        input.focus();
    } else {
        editRow.style.display = 'none';
    }
}

// Save Email
async function saveEmail() {
    const input = document.getElementById('email-input');
    const newEmail = input.value.trim();

    if (!newEmail) {
        showToast('error', 'Invalid Input', 'Email cannot be empty');
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(newEmail)) {
        showToast('error', 'Invalid Email', 'Please enter a valid email address');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/users/me`, {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: currentUsername,
                email: newEmail
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update email');
        }

        // Update local variables and DOM
        currentEmail = newEmail;
        document.getElementById('email-display').textContent = newEmail;
        document.getElementById('email-edit-row').style.display = 'none';

        // Update sessionStorage
        sessionStorage.setItem('astronaut_user', JSON.stringify({
            username: currentUsername,
            email: newEmail
        }));

        showToast('success', 'Email Updated', 'Your email has been changed successfully');
    } catch (error) {
        console.error('Error updating email:', error);
        showToast('error', 'Update Failed', error.message || 'Failed to update email');
    }
}

// Update Password
async function updatePassword() {
    const currentPassword = document.getElementById('current-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (!currentPassword || !newPassword || !confirmPassword) {
        showToast('error', 'Missing Fields', 'All password fields are required');
        return;
    }

    if (newPassword.length < 6) {
        showToast('error', 'Weak Password', 'New password must be at least 6 characters');
        return;
    }

    if (newPassword !== confirmPassword) {
        showToast('error', 'Password Mismatch', 'New passwords do not match');
        return;
    }

    if (currentPassword === newPassword) {
        showToast('error', 'Invalid Change', 'New password must be different from current password');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/users/me/password`, {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword,
                confirmNewPassword: confirmPassword
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update password');
        }

        // Clear form
        document.getElementById('current-password').value = '';
        document.getElementById('new-password').value = '';
        document.getElementById('confirm-password').value = '';

        showToast('success', 'Password Updated', 'Your password has been changed successfully');
    } catch (error) {
        console.error('Error updating password:', error);
        showToast('error', 'Update Failed', error.message || 'Failed to update password');
    }
}

// Toggle Fuzzy Search
async function toggleFuzzySearch() {
    const toggle = document.getElementById('fuzzy-search-toggle');
    toggle.classList.toggle('active');
    const isActive = toggle.classList.contains('active');

    try {
        const response = await fetch(`${API_BASE}/users/preferences`, {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                enableFuzzySearch: isActive
            })
        });

        if (!response.ok) {
            throw new Error('Failed to update preferences');
        }

        showToast('success', 'Preference Updated', `Fuzzy search ${isActive ? 'enabled' : 'disabled'}`);
    } catch (error) {
        console.error('Error updating preferences:', error);
        toggle.classList.toggle('active');
        showToast('error', 'Update Failed', 'Failed to update preferences');
    }
}

// Open Delete Modal
function openDeleteModal() {
    document.getElementById('delete-modal').classList.add('active');
    document.getElementById('delete-password').value = '';
    document.getElementById('delete-confirm-password').value = '';
    document.getElementById('delete-password').focus();
}

// Close Delete Modal
function closeDeleteModal() {
    document.getElementById('delete-modal').classList.remove('active');
}

// Confirm Delete
async function confirmDelete() {
    const password = document.getElementById('delete-password').value;
    const confirmPassword = document.getElementById('delete-confirm-password').value;

    if (!password || !confirmPassword) {
        showToast('error', 'Missing Fields', 'Both password fields are required');
        return;
    }

    if (password !== confirmPassword) {
        showToast('error', 'Password Mismatch', 'Passwords do not match');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/users`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                password: password,
                confirmPassword: confirmPassword
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to delete account');
        }

        closeDeleteModal();
        showToast('success', 'Account Deleted', 'Redirecting to login...');
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 2000);
    } catch (error) {
        console.error('Error deleting account:', error);
        showToast('error', 'Deletion Failed', error.message || 'Failed to delete account');
    }
}