const API_BASE = `${window.origin}`;
let currentUsername = '';
let currentEmail = '';

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
        showNotification('User data not found. Please log in again.', 'error');
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
        showNotification('Failed to load user data', 'error');
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
        showNotification('Username cannot be empty', 'error');
        return;
    }

    if (newUsername.length < 1) {
        showNotification('Username must be at least 1 character', 'error');
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

        showNotification('Username updated successfully', 'success');
    } catch (error) {
        console.error('Error updating username:', error);
        showNotification(error.message || 'Failed to update username', 'error');
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
        showNotification('Email cannot be empty', 'error');
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(newEmail)) {
        showNotification('Please enter a valid email address', 'error');
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

        showNotification('Email updated successfully', 'success');
    } catch (error) {
        console.error('Error updating email:', error);
        showNotification(error.message || 'Failed to update email', 'error');
    }
}

// Update Password
async function updatePassword() {
    const currentPassword = document.getElementById('current-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (!currentPassword || !newPassword || !confirmPassword) {
        showNotification('All password fields are required', 'error');
        return;
    }

    if (newPassword.length < 6) {
        showNotification('New password must be at least 6 characters', 'error');
        return;
    }

    if (newPassword !== confirmPassword) {
        showNotification('New passwords do not match', 'error');
        return;
    }

    if (currentPassword === newPassword) {
        showNotification('New password must be different from current password', 'error');
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

        showNotification('Password updated successfully', 'success');
    } catch (error) {
        console.error('Error updating password:', error);
        showNotification(error.message || 'Failed to update password', 'error');
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

        showNotification(`Fuzzy search ${isActive ? 'enabled' : 'disabled'}`, 'success');
    } catch (error) {
        console.error('Error updating preferences:', error);
        toggle.classList.toggle('active');
        showNotification('Failed to update preferences', 'error');
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
        showNotification('Both password fields are required', 'error');
        return;
    }

    if (password !== confirmPassword) {
        showNotification('Passwords do not match', 'error');
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
        showNotification('Account deleted successfully. Redirecting...', 'success');
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 2000);
    } catch (error) {
        console.error('Error deleting account:', error);
        showNotification(error.message || 'Failed to delete account', 'error');
    }
}

// Show Notification
function showNotification(message, type = 'success') {
    const container = document.getElementById('notification-container');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;

    const icon = type === 'success' ? 'ri-check-circle-line' : 'ri-error-warning-line';
    notification.innerHTML = `<i class="${icon}"></i><span>${message}</span>`;

    container.appendChild(notification);

    setTimeout(() => {
        notification.classList.add('out');
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

// Navigation
function goBack() {
    window.location.href = '/dashboard.html';
}

async function logout() {
    try {
        await fetch(`${API_BASE}/users/logout`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    } catch (error) {
        console.error('Error logging out:', error);
    }

    showNotification('Logging out...', 'success');
    setTimeout(() => {
        window.location.href = '/index.html';
    }, 1000);
}

// Close modal when clicking outside
document.addEventListener('DOMContentLoaded', function() {
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
});

// Enter key on delete confirmation fields
document.addEventListener('DOMContentLoaded', function() {
    const deletePasswordField = document.getElementById('delete-confirm-password');
    if (deletePasswordField) {
        deletePasswordField.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                confirmDelete();
            }
        });
    }
});