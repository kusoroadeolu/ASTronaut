// Header Component
function renderHeader(config = {}) {
    const {
        showUsername = false,
        buttons = []
    } = config;

    // Create header element
    const header = document.createElement('header');

    // Left side
    const headerLeft = document.createElement('div');
    headerLeft.className = 'header-left';
    headerLeft.innerHTML = `
        <div class="app-name">
            <i class="ri-rocket-2-line"></i> Astronaut
        </div>
    `;

    // Right side
    const headerRight = document.createElement('div');
    headerRight.className = 'header-right';

    // Add username if needed
    if (showUsername) {
        const usernameEl = document.createElement('div');
        usernameEl.className = 'username';

        // Try to get username from sessionStorage
        const storedUser = sessionStorage.getItem('astronaut_user');
        if (storedUser) {
            const userData = JSON.parse(storedUser);
            usernameEl.textContent = userData.username || '';
        }

        headerRight.appendChild(usernameEl);
    }

    // Add buttons
    buttons.forEach(btn => {
        const button = document.createElement('button');
        button.className = `header-btn ${btn.className || ''}`;

        if (btn.icon && !btn.text) {
            button.classList.add('icon-only');
        }

        if (btn.title) {
            button.title = btn.title;
        }

        let innerHTML = '';
        if (btn.icon) {
            innerHTML += `<i class="${btn.icon}"></i>`;
        }
        if (btn.text) {
            innerHTML += btn.text;
        }
        button.innerHTML = innerHTML;

        if (btn.onClick) {
            button.addEventListener('click', btn.onClick);
        }

        headerRight.appendChild(button);
    });

    header.appendChild(headerLeft);
    header.appendChild(headerRight);

    // Insert at the beginning of body
    document.body.insertBefore(header, document.body.firstChild);
}

// Common button configurations
const HeaderButtons = {
    settings: function(onClick) {
        return {
            icon: 'ri-settings-3-line',
            title: 'Settings',
            onClick: onClick || function() { window.location.href = '/settings.html'; }
        };
    },

    back: function(onClick) {
        return {
            icon: 'ri-arrow-left-line',
            text: 'Back',
            onClick: onClick || function() { window.location.href = '/dashboard.html'; }
        };
    },

    logout: function(onClick) {
        return {
            icon: 'ri-logout-box-line',
            text: 'Logout',
            className: 'logout',
            onClick: onClick || async function() {
                try {
                    await fetch(`${window.origin}/users/logout`, {
                        method: 'DELETE',
                        credentials: 'include',
                        headers: { 'Content-Type': 'application/json' }
                    });
                } catch (error) {
                    console.error('Logout error:', error);
                }
                window.location.href = '/index.html';
            }
        };
    }
};