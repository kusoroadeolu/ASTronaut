// Load highlight.js
// const highlightLink = document.createElement('link');
// highlightLink.rel = 'stylesheet';
// highlightLink.href = 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/atom-one-dark.min.css';
// document.head.appendChild(highlightLink);

const highlightScript = document.createElement('script');
highlightScript.src = 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js';
document.head.appendChild(highlightScript);

// Load marked.js
const markedScript = document.createElement('script');
markedScript.src = 'https://cdnjs.cloudflare.com/ajax/libs/marked/11.1.1/marked.min.js';
document.head.appendChild(markedScript);

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
let allSnippets = [];

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
    const readOnlyMode = document.getElementById('readOnlyMode');
    const editMode = document.getElementById('editMode');

    readOnlyMode.style.display = 'block';
    editMode.style.display = 'none';

    const titleElement = document.querySelector('.snippet-title');
    if (titleElement) {
        titleElement.textContent = snippetData.name;
    }

    const languageBadge = document.querySelector('.language-badge');
    if (languageBadge) {
        if (snippetData.language) {
            languageBadge.textContent = snippetData.language;
            languageBadge.style.display = 'inline-block';
        } else {
            languageBadge.style.display = 'none';
        }
    }

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

    const codeBlock = document.getElementById('codeBlock');
    const lineNumbers = document.getElementById('lineNumbers');

    if (codeBlock) {
        const content = snippetData.content || 'No code content available';

        // Apply syntax highlighting if hljs is loaded and language is Java
        if (window.hljs && snippetData.language === 'JAVA') {
            try {
                const highlighted = window.hljs.highlight(content, { language: 'java' });
                codeBlock.innerHTML = highlighted.value;
                codeBlock.classList.add('hljs');
            } catch (e) {
                console.error('Highlighting failed:', e);
                codeBlock.textContent = content;
            }
        } else {
            codeBlock.textContent = content;
        }

        if (lineNumbers) {
            const lineCount = content.split('\n').length;
            lineNumbers.innerHTML = Array.from({ length: lineCount }, (_, i) => `<div>${i + 1}</div>`).join('');
        }
    }

    const notesContent = document.querySelector('.notes-content');
    if (notesContent) {
        const notes = snippetData.extraNotes || 'No notes available';

        // Render markdown if marked is loaded
        if (window.marked) {
            try {
                notesContent.innerHTML = window.marked.parse(notes);
            } catch (e) {
                console.error('Markdown parsing failed:', e);
                notesContent.textContent = notes;
            }
        } else {
            notesContent.textContent = notes;
        }
    }

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
        readOnlyMode.style.display = 'none';
        editMode.style.display = 'block';
        editMode.classList.remove('edit-mode-hidden');
        editMode.classList.add('fade-in');
        populateEditMode();
    } else {
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

    const language = document.getElementById('editLanguage').value;
    if (language) {
        updatedData.language = language;
    }

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

            // Explicitly switch to read-only mode
            const readOnlyMode = document.getElementById('readOnlyMode');
            const editMode = document.getElementById('editMode');

            editMode.style.display = 'none';
            editMode.classList.add('edit-mode-hidden');
            readOnlyMode.style.display = 'block';

            // Then update the display
            displaySnippet();

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

// Fetch all snippets for comparison
async function fetchAllSnippets() {
    try {
        // Fetch a large page to get all snippets (or at least enough for comparison)
        const response = await fetch(`${window.location.origin}/snippets?page=0&size=100&sort=createdAt,DESC`, {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            const data = await response.json();
            // Extract content array from paginated response
            allSnippets = data.content || [];
            // Filter out the current snippet
            allSnippets = allSnippets.filter(s => s.id !== parseInt(snippetId));
            return allSnippets;
        } else {
            showToast('error', 'Error', 'Failed to load snippets for comparison');
            return [];
        }
    } catch (err) {
        console.error('Error fetching snippets:', err);
        showToast('error', 'Error', 'Failed to load snippets');
        return [];
    }
}

// Toggle compare mode - show snippet selector modal
async function toggleCompareMode() {
    const snippets = await fetchAllSnippets();

    if (snippets.length === 0) {
        showToast('info', 'No Snippets', 'You need at least one other snippet to compare');
        return;
    }

    showCompareModal(snippets);
}

// Show modal to select snippet for comparison
function showCompareModal(snippets) {
    // Create modal overlay
    let overlay = document.querySelector('.compare-modal-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.className = 'compare-modal-overlay';
        document.body.appendChild(overlay);
    }

    // Create modal content
    overlay.innerHTML = `
        <div class="compare-modal">
            <h3 class="compare-modal-title">Select Snippet to Compare</h3>
            <div class="snippet-list">
                ${snippets.map(snippet => `
                    <div class="snippet-item" onclick="selectSnippetForCompare(${snippet.id})">
                        <div>
                            <div class="snippet-item-name">${snippet.name}</div>
                            <div class="snippet-item-language">${snippet.language || 'No language'}</div>
                        </div>
                    </div>
                `).join('')}
            </div>
            <div class="modal-buttons">
                <button class="btn btn-secondary" onclick="closeCompareModal()">
                    <i class="ri-close-line"></i>
                    Cancel
                </button>
            </div>
        </div>
    `;

    overlay.classList.add('show');
}

// Close compare modal
function closeCompareModal() {
    const overlay = document.querySelector('.compare-modal-overlay');
    if (overlay) {
        overlay.classList.remove('show');
    }
}

// Select snippet and load diff
async function selectSnippetForCompare(compareToId) {
    closeCompareModal();

    try {
        const response = await fetch(
            `${window.location.origin}/snippets/${snippetId}/compare/${compareToId}`,
            {
                method: 'GET',
                credentials: 'include'
            }
        );

        if (response.ok) {
            const diffData = await response.json();
            displayDiffView(diffData);
        } else {
            showToast('error', 'Error', 'Failed to load comparison');
        }
    } catch (err) {
        console.error('Error fetching diff:', err);
        showToast('error', 'Error', 'Failed to load comparison');
    }
}

// Display diff view
function displayDiffView(diffData) {
    const readOnlyMode = document.getElementById('readOnlyMode');
    const editMode = document.getElementById('editMode');
    const diffView = document.getElementById('diffView');

    // Hide other modes
    readOnlyMode.style.display = 'none';
    editMode.style.display = 'none';

    // Show diff view
    diffView.style.display = 'block';
    diffView.classList.add('show');

    // Update title
    const diffTitle = document.querySelector('.diff-title');
    if (diffTitle) {
        diffTitle.textContent = `${diffData.comparing.snippetName} vs ${diffData.comparingTo.snippetName}`;
    }

    // Update column headers
    const headers = document.querySelectorAll('.diff-column-header');
    if (headers.length >= 2) {
        headers[0].textContent = diffData.comparing.snippetName;
        headers[1].textContent = diffData.comparingTo.snippetName;
    }

    // Render left side (comparing)
    renderDiffColumn(
        'diffLineNumbersLeft',
        'diffCodeLeft',
        diffData.comparing.lines
    );

    // Render right side (comparingTo)
    renderDiffColumn(
        'diffLineNumbersRight',
        'diffCodeRight',
        diffData.comparingTo.lines
    );

    // Setup synchronized scrolling
    setupSynchronizedScrolling();
}

// Render a diff column
function renderDiffColumn(lineNumbersId, codeId, lines) {
    const lineNumbersEl = document.getElementById(lineNumbersId);
    const codeEl = document.getElementById(codeId);

    if (!lineNumbersEl || !codeEl) return;

    // Render line numbers
    lineNumbersEl.innerHTML = lines
        .map(line => `<div>${line.lineNum}</div>`)
        .join('');

    // Render code with highlighting and diff colors
    codeEl.innerHTML = lines
        .map(line => {
            let className = '';
            if (line.lineType === 'ADDED') {
                className = 'diff-line-added';
            } else if (line.lineType === 'REMOVED') {
                className = 'diff-line-removed';
            }

            // Apply syntax highlighting if available
            let content = escapeHtml(line.lineContent);
            if (window.hljs && snippetData && snippetData.language === 'JAVA') {
                try {
                    content = window.hljs.highlight(line.lineContent, { language: 'java' }).value;
                } catch (e) {
                    content = escapeHtml(line.lineContent);
                }
            }

            return `<div class="${className}">${content}</div>`;
        })
        .join('');
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Setup synchronized scrolling between diff columns
function setupSynchronizedScrolling() {
    const leftContent = document.querySelector('#diffView .diff-column:first-child .diff-content');
    const rightContent = document.querySelector('#diffView .diff-column:last-child .diff-content');

    if (!leftContent || !rightContent) return;

    let isLeftScrolling = false;
    let isRightScrolling = false;

    leftContent.addEventListener('scroll', function() {
        if (isRightScrolling) return;
        isLeftScrolling = true;
        rightContent.scrollTop = this.scrollTop;
        rightContent.scrollLeft = this.scrollLeft;
        setTimeout(() => { isLeftScrolling = false; }, 10);
    });

    rightContent.addEventListener('scroll', function() {
        if (isLeftScrolling) return;
        isRightScrolling = true;
        leftContent.scrollTop = this.scrollTop;
        leftContent.scrollLeft = this.scrollLeft;
        setTimeout(() => { isRightScrolling = false; }, 10);
    });
}

// Exit compare mode
function exitCompareMode() {
    const diffView = document.getElementById('diffView');
    const readOnlyMode = document.getElementById('readOnlyMode');

    if (diffView) {
        diffView.style.display = 'none';
        diffView.classList.remove('show');
    }
    if (readOnlyMode) readOnlyMode.style.display = 'block';
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {

    console.log('Page loaded, fetching snippet data...');
    // Render header with back button
    renderHeader({
        showUsername: true,
        buttons: [
            HeaderButtons.back()
        ]
    });

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