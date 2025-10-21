// Toast Notification System
//Snippet-detail.js shows snippet info and allows snippet editing
function showToast(type, title, message) {
    // Create container if it doesn't exist
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    // Icon mapping
    const icons = {
        success: 'ri-check-circle-line',
        error: 'ri-error-warning-line',
        info: 'ri-information-line',
        warning: 'ri-alert-line'
    };

    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <i class="toast-icon ${icons[type]}"></i>
        <div class="toast-content">
            <div class="toast-title">${title}</div>
            ${message ? `<div class="toast-message">${message}</div>` : ''}
        </div>
    `;

    // Add to container
    container.appendChild(toast);

    // Trigger animation
    setTimeout(() => {
        toast.classList.add('show');
    }, 10);

    // Remove after 3.5 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            toast.remove();
            // Remove container if empty
            if (container.children.length === 0) {
                container.remove();
            }
        }, 300);
    }, 3500);
}

// Get snippet ID from URL
const urlParams = new URLSearchParams(window.location.search);
const snippetId = urlParams.get('id');

if (!snippetId) {
    showToast('error', 'Error', 'No snippet ID provided');
    setTimeout(() => {
        window.location.href = '/dashboard.html';
    }, 1500);
}

let snippetData = null;

// Fetch snippet data
async function fetchSnippetData() {
    try {
        console.log('Fetching snippet:', snippetId);
        const response = await fetch(`${window.location.origin}/snippets/${snippetId}`, {
            method: 'GET',
            credentials: 'include'
        });

        console.log('Response status:', response.status);

        if (response.ok) {
            snippetData = await response.json();
            console.log('Snippet data loaded:', snippetData);
            displaySnippet();
        } else if (response.status === 401) {
            window.location.href = '/login.html';
        } else if (response.status === 404) {
            showToast('error', 'Not Found', 'Snippet not found');
            setTimeout(() => {
                window.location.href = '/dashboard.html';
            }, 1500);
        } else {
            showToast('error', 'Error', 'Failed to load snippet');
            setTimeout(() => {
                window.location.href = '/dashboard.html';
            }, 1500);
        }
    } catch (err) {
        console.error('Error fetching snippet:', err);
        showToast('error', 'Error', 'Failed to load snippet');
        setTimeout(() => {
            window.location.href = '/dashboard.html';
        }, 1500);
    }
}

// Display snippet in read-only mode
function displaySnippet() {
    // Make sure read-only mode is visible and edit mode is hidden
    const readOnlyMode = document.getElementById('readOnlyMode');
    const editMode = document.getElementById('editMode');

    readOnlyMode.style.display = 'block';
    editMode.style.display = 'none';

    // Update title
    const titleElement = document.querySelector('.snippet-title');
    if (titleElement) {
        titleElement.textContent = snippetData.name;
    }

    // Update language badge
    const languageBadge = document.querySelector('.language-badge');
    if (languageBadge) {
        if (snippetData.language) {
            languageBadge.textContent = snippetData.language;
            languageBadge.style.display = 'inline-block';
        } else {
            languageBadge.style.display = 'none';
        }
    }

    // Update tags
    const tagsContainer = document.querySelector('.tags-container');
    if (tagsContainer) {
        if (snippetData.tags && snippetData.tags.length > 0) {
            tagsContainer.innerHTML = snippetData.tags
                .map(tag => `<span class="tag">${tag}</span>`)
                .join('');
        } else {
            tagsContainer.innerHTML = '<span class="tag" style="opacity: 0.5;">No tags</span>';
        }
    }

    // Update dates
    const createdDate = new Date(snippetData.createdAt).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
    const updatedDate = new Date(snippetData.lastUpdated).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    const dateItems = document.querySelectorAll('.date-item span:not(.date-label)');
    if (dateItems.length >= 2) {
        dateItems[0].textContent = createdDate;
        dateItems[1].textContent = updatedDate;
    }

    // Update code content
    const codeBlock = document.getElementById('codeBlock');
    const lineNumbers = document.getElementById('lineNumbers');

    if (codeBlock) {
        const content = snippetData.content || 'No code content available';
        codeBlock.textContent = content;

        // Generate line numbers
        if (lineNumbers) {
            const lineCount = content.split('\n').length;
            lineNumbers.innerHTML = Array.from({ length: lineCount }, (_, i) => i + 1).join('\n');
        }
    }

    // Update notes
    const notesContent = document.querySelector('.notes-content');
    if (notesContent) {
        notesContent.textContent = snippetData.extraNotes || 'No notes available';
    }

    // Populate edit mode fields
    populateEditMode();
}

// Populate edit mode with current data
function populateEditMode() {
    const editTitle = document.getElementById('editTitle');
    const editLanguage = document.getElementById('editLanguage');
    const editTags = document.getElementById('editTags');
    const editCode = document.getElementById('editCode');
    const editNotes = document.getElementById('editNotes');

    if (editTitle) editTitle.value = snippetData.name || '';
    if (editLanguage) editLanguage.value = snippetData.language || '';
    if (editTags) editTags.value = snippetData.tags?.join(', ') || '';
    if (editCode) editCode.value = snippetData.content || '';
    if (editNotes) editNotes.value = snippetData.extraNotes || '';
}

// Toggle edit mode
function toggleEditMode() {
    const readOnlyMode = document.getElementById('readOnlyMode');
    const editMode = document.getElementById('editMode');

    if (editMode.style.display === 'none' || !editMode.style.display) {
        // Switch to edit mode
        readOnlyMode.style.display = 'none';
        editMode.style.display = 'block';
        editMode.classList.remove('edit-mode-hidden');
        editMode.classList.add('fade-in');
        // Repopulate in case data changed
        populateEditMode();
    } else {
        // Switch back to read-only
        readOnlyMode.style.display = 'block';
        editMode.style.display = 'none';
        editMode.classList.add('edit-mode-hidden');
        editMode.classList.remove('fade-in');
    }
}

// Copy code to clipboard
function copyCode() {
    const codeBlock = document.getElementById('codeBlock');
    const code = codeBlock.textContent;

    navigator.clipboard.writeText(code).then(() => {
        const btn = document.querySelector('.btn-copy');
        const originalHTML = btn.innerHTML;
        btn.innerHTML = '<i class="ri-check-line"></i> Copied!';
        setTimeout(() => {
            btn.innerHTML = originalHTML;
        }, 2000);
    }).catch(err => {
        console.error('Failed to copy:', err);
        showToast('error', 'Error', 'Failed to copy code');
    });
}

// Navigate back to dashboard
function goToDashboard(event) {
    if (event) event.preventDefault();
    window.location.href = '/dashboard.html';
}

// Save snippet (edit mode)
async function saveSnippet() {
    const updatedData = {
        snippetName: document.getElementById('editTitle').value.trim(),
        content: document.getElementById('editCode').value,
        extraNotes: document.getElementById('editNotes').value
    };

    // Only add language if it's selected
    const language = document.getElementById('editLanguage').value;
    if (language) {
        updatedData.language = language;
    }

    // Parse tags
    const tagsInput = document.getElementById('editTags').value.trim();
    if (tagsInput) {
        updatedData.tags = tagsInput
            .split(',')
            .map(t => t.trim())
            .filter(t => t);
    } else {
        updatedData.tags = [];
    }

    if (!updatedData.snippetName) {
        showToast('warning', 'Missing Name', 'Snippet name is required');
        return;
    }

    try {
        console.log('Updating snippet with:', updatedData);
        const response = await fetch(`${window.location.origin}/snippets/${snippetId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            snippetData = await response.json();
            console.log('Snippet updated:', snippetData);
            displaySnippet();
            toggleEditMode();
            showToast('success', 'Saved!', 'Snippet updated successfully');
        } else {
            const error = await response.json();
            showToast('error', 'Failed to Save', error.message || 'Unknown error');
        }
    } catch (err) {
        console.error('Error updating snippet:', err);
        showToast('error', 'Error', 'Failed to update snippet');
    }
}

// Delete snippet
async function deleteSnippet() {
    if (!confirm('Are you sure you want to delete this snippet? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch(`${window.location.origin}/snippets/${snippetId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (response.status === 204 || response.ok) {
            showToast('success', 'Deleted', 'Snippet deleted successfully');
            setTimeout(() => {
                window.location.href = '/dashboard.html';
            }, 1500);
        } else {
            showToast('error', 'Error', 'Failed to delete snippet');
        }
    } catch (err) {
        console.error('Error deleting snippet:', err);
        showToast('error', 'Error', 'Failed to delete snippet');
    }
}

// Toggle compare mode
function toggleCompareMode() {
    showToast('info', 'Coming Soon', 'Compare mode is not yet available');
    // TODO: Implement comparison functionality
}

// Exit compare mode
function exitCompareMode() {
    const diffView = document.getElementById('diffView');
    const readOnlyMode = document.getElementById('readOnlyMode');

    if (diffView) diffView.style.display = 'none';
    if (readOnlyMode) readOnlyMode.style.display = 'block';
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Page loaded, fetching snippet data...');
    fetchSnippetData();
});

// Also try to fetch immediately in case DOMContentLoaded already fired
if (document.readyState === 'loading') {
    // Still loading, wait for event
} else {
    // Already loaded
    console.log('DOM already loaded, fetching snippet data...');
    fetchSnippetData();
}