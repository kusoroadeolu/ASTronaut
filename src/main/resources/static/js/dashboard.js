// Dashboard.js(Shows all snippets + search logic + settings) Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {

    // At the top of dashboard.js, after DOM loads
    renderHeader({
        showUsername: true,
        buttons: [
            HeaderButtons.settings()
        ]
    });

// Advanced Search Toggle
    const advancedToggle = document.getElementById('advancedToggle');
    const advancedPanel = document.getElementById('advancedPanel');

    if (advancedToggle && advancedPanel) {
        advancedToggle.addEventListener('click', function() {
            advancedPanel.classList.toggle('open');
            advancedToggle.classList.toggle('active');
        });
    }

// Pagination State
    let currentPage = 0; // API uses zero-based indexing
    let totalPages = 1;
    let pageSize = 20;

    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const pageIndicator = document.getElementById('pageIndicator');

    function updatePagination() {
        if (pageIndicator) {
            pageIndicator.textContent = `Page ${currentPage + 1} of ${totalPages}`;
        }
        if (prevBtn) prevBtn.disabled = currentPage === 0;
        if (nextBtn) nextBtn.disabled = currentPage === totalPages - 1;
    }

    if (prevBtn) {
        prevBtn.addEventListener('click', function() {
            if (currentPage > 0) {
                currentPage--;
                fetchSnippets();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', function() {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fetchSnippets();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    }

// Fetch and display snippets
    async function fetchSnippets() {
        try {
            const response = await fetch(`${window.location.origin}/snippets?page=${currentPage}&size=${pageSize}&sort=createdAt,DESC`, {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                displaySnippets(data.content);

                // Update pagination info
                totalPages = data.page.totalPages;
                updatePagination();
            } else if (response.status === 401) {
                // User not authenticated, redirect to login

                window.location.href = '/index.html';

            } else {
                console.error('Failed to fetch snippets');
                showEmptyState('Failed to load snippets. Please try again.');
            }
        } catch (err) {
            console.error('Error fetching snippets:', err);
            showEmptyState('Failed to load snippets. Please try again.');
        }
    }

    function displaySnippets(snippets) {
        const grid = document.querySelector('.snippets-grid');
        if (!grid) return;

        if (!snippets || snippets.length === 0) {
            showEmptyState('No snippets yet. Click the + button to create your first snippet!');
            return;
        }

        grid.innerHTML = snippets.map(snippet => {
            const createdDate = new Date(snippet.createdAt).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });

            const tagsHTML = snippet.tags && snippet.tags.length > 0
                ? snippet.tags.map(tag => `<span class="tag">${tag}</span>`).join('')
                : '<span class="tag" style="opacity: 0.5;">No tags</span>';

            const languageHTML = snippet.language
                ? `<span class="snippet-language">${snippet.language}</span>`
                : '';

            return `
            <div class="snippet-card" data-snippet-id="${snippet.id}">
                <div class="snippet-header">
                    <div class="snippet-title">${snippet.name}</div>
                    ${languageHTML}
                </div>
                <div class="snippet-tags">
                    ${tagsHTML}
                </div>
                <div class="snippet-footer">
                    <div class="snippet-dates">
                        <span class="snippet-date-label">Created: ${createdDate}</span>
                    </div>
                </div>
            </div>
        `;
        }).join('');

        // Add click handlers to new cards
        document.querySelectorAll('.snippet-card').forEach(card => {
            card.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                const snippetId = this.getAttribute('data-snippet-id');
                window.location.href = `/snippet-detail.html?id=${snippetId}`;
            });
        });
    }

    function showEmptyState(message) {
        const grid = document.querySelector('.snippets-grid');
        if (!grid) return;

        grid.innerHTML = `
        <div class="empty-state" style="grid-column: 1 / -1;">
            <div class="empty-state-icon">
                <i class="ri-file-code-line"></i>
            </div>
            <div class="empty-state-text">${message}</div>
        </div>
    `;
    }

// Settings Button Handler
    const settingsBtn = document.querySelector('.settings-btn');
    if (settingsBtn) {
        settingsBtn.addEventListener('click', function() {
            console.log('Opening settings');
            window.location.href = '/settings.html';
        });
    }

// Get all search field elements
    const searchBar = document.querySelector('.search-bar');
    const classNamesInput = document.getElementById('classNames');
    const classAnnotationsInput = document.getElementById('classAnnotations');
    const classFieldsInput = document.getElementById('classFields');
    const classFieldAnnotationsInput = document.getElementById('classFieldAnnotations');
    const methodReturnTypesInput = document.getElementById('methodReturnTypes');
    const methodAnnotationsInput = document.getElementById('methodAnnotations');
    const languageFilter = document.getElementById('languageFilter');

// Track if we're in filter mode
    let isFilterMode = false;

// Helper function to parse comma-separated values
    function parseCommaSeparated(value) {
        if (!value || !value.trim()) return [];
        return [...new Set(value.split(',').map(t => t.trim()).filter(t => t))];
    }

// Fetch snippets with filters
    async function fetchFilteredSnippets() {
        try {
            // Build filter object
            const filters = {};

            // Main search bar - tagsOrNames
            const tagsOrNames = parseCommaSeparated(searchBar?.value);
            if (tagsOrNames.length > 0) {
                filters.tagsOrNames = tagsOrNames;
            }

            // Language filter
            if (languageFilter?.value) {
                filters.languages = [languageFilter.value];
            }

            // Advanced search filters
            const classNames = parseCommaSeparated(classNamesInput?.value);
            if (classNames.length > 0) filters.classNames = classNames;

            const classAnnotations = parseCommaSeparated(classAnnotationsInput?.value);
            if (classAnnotations.length > 0) filters.classAnnotations = classAnnotations;

            const classFields = parseCommaSeparated(classFieldsInput?.value);
            if (classFields.length > 0) filters.classFields = classFields;

            const classFieldAnnotations = parseCommaSeparated(classFieldAnnotationsInput?.value);
            if (classFieldAnnotations.length > 0) filters.classFieldAnnotations = classFieldAnnotations;

            const methodReturnTypes = parseCommaSeparated(methodReturnTypesInput?.value);
            if (methodReturnTypes.length > 0) filters.methodReturnTypes = methodReturnTypes;

            const methodAnnotations = parseCommaSeparated(methodAnnotationsInput?.value);
            if (methodAnnotations.length > 0) filters.methodAnnotations = methodAnnotations;

            // Check if any filters are active
            const hasFilters = Object.keys(filters).length > 0;

            if (!hasFilters) {
                // No filters, use regular fetch
                isFilterMode = false;
                fetchSnippets();
                return;
            }

            isFilterMode = true;

            const response = await fetch(`${window.location.origin}/snippets/filter?page=${currentPage}&size=${pageSize}&sort=createdAt,DESC`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(filters)
            });

            if (response.ok) {
                const data = await response.json();
                displaySnippets(data.content);

                // Update pagination info
                totalPages = data.page.totalPages;
                updatePagination();
            } else if (response.status === 401) {
                console.log("Pagination auth error: ", err)
                window.location.href = '/index.html';
            } else {
                console.error('Failed to filter snippets');
                showEmptyState('Failed to filter snippets. Please try again.');
            }
        } catch (err) {
            console.error('Error filtering snippets:', err);
            showEmptyState('Failed to filter snippets. Please try again.');
        }
    }

// Update the fetchSnippets function to use the correct endpoint based on mode
    const originalFetchSnippets = fetchSnippets;
    fetchSnippets = function() {
        if (isFilterMode) {
            fetchFilteredSnippets();
        } else {
            originalFetchSnippets();
        }
    };

// Main Search Button
    const searchBtn = document.getElementById('searchBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', function() {
            currentPage = 0; // Reset to first page
            fetchFilteredSnippets();
        });
    }

// Advanced Search Clear Button
    const clearFiltersBtn = document.querySelector('.search-buttons .btn-secondary');
    if (clearFiltersBtn) {
        clearFiltersBtn.addEventListener('click', function() {
            console.log('Filters cleared');

            // Clear all inputs
            if (searchBar) searchBar.value = '';
            if (classNamesInput) classNamesInput.value = '';
            if (classAnnotationsInput) classAnnotationsInput.value = '';
            if (classFieldsInput) classFieldsInput.value = '';
            if (classFieldAnnotationsInput) classFieldAnnotationsInput.value = '';
            if (methodReturnTypesInput) methodReturnTypesInput.value = '';
            if (methodAnnotationsInput) methodAnnotationsInput.value = '';
            if (languageFilter) languageFilter.value = '';

            // Reset to regular mode and fetch all snippets
            isFilterMode = false;
            currentPage = 0;
            originalFetchSnippets();
        });
    }

// ===== CREATE SNIPPET MODAL =====

// Create and inject modal HTML
    function createModal() {
        const modalHTML = `
        <div class="modal-overlay" id="createSnippetModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 class="modal-title">Create New Snippet</h2>
                    <button class="modal-close" type="button" id="closeModal">
                        <i class="ri-close-line"></i>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="createSnippetForm">
                        <div class="form-group">
                            <label for="snippetName">Snippet Name <span style="color: #ef4444;">*</span></label>
                            <input 
                                type="text" 
                                id="snippetName" 
                                name="snippetName" 
                                placeholder="e.g., User Authentication Service"
                                required
                                minlength="1"
                            >
                        </div>
                        
                        <div class="form-group">
                            <label for="language">Language</label>
                            <select id="language" name="language">
                                <option value="">Select a language (optional)</option>
                                <option value="JAVA">Java</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="tags">Tags</label>
                            <input 
                                type="text" 
                                id="tags" 
                                name="tags" 
                                placeholder="e.g., spring, authentication, security"
                            >
                            <small>Separate multiple tags with commas</small>
                        </div>
                        
                        <div class="form-actions">
                            <button type="button" class="btn btn-secondary" id="cancelBtn">Cancel</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="ri-add-line"></i>
                                Create Snippet
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;

        document.body.insertAdjacentHTML('beforeend', modalHTML);
    }

// Initialize modal functionality
    function initCreateSnippetModal() {
        createModal();

        const modal = document.getElementById('createSnippetModal');
        const fabBtn = document.querySelector('.fab');
        const closeModalBtn = document.getElementById('closeModal');
        const cancelBtn = document.getElementById('cancelBtn');
        const form = document.getElementById('createSnippetForm');

        if (!modal || !fabBtn || !closeModalBtn || !cancelBtn || !form) {
            console.error('Modal elements not found');
            return;
        }

        function openModal() {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        }

        function closeModal() {
            modal.classList.remove('show');
            document.body.style.overflow = '';
            form.reset();
        }

        fabBtn.addEventListener('click', openModal);
        closeModalBtn.addEventListener('click', closeModal);
        cancelBtn.addEventListener('click', closeModal);

        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeModal();
            }
        });

        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && modal.classList.contains('show')) {
                closeModal();
            }
        });

        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const snippetName = document.getElementById('snippetName').value.trim();
            const language = document.getElementById('language').value;
            const tagsInput = document.getElementById('tags').value.trim();

            const tags = tagsInput
                ? [...new Set(tagsInput.split(',').map(t => t.trim()).filter(t => t))]
                : [];

            const requestBody = {
                snippetName: snippetName,
                tags: tags
            };

            if (language) {
                requestBody.language = language;
            }

            try {
                const response = await fetch(`${window.location.origin}/snippets`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: JSON.stringify(requestBody)
                });

                if (response.ok) {
                    const newSnippet = await response.json();
                    console.log('Snippet created:', newSnippet);
                    // Redirect to the newly created snippet's detail page
                    window.location.href = `/snippet-detail.html?id=${newSnippet.id}`;
                } else {
                    const error = await response.json();
                    alert(`Error: ${error.message || 'Failed to create snippet'}`);
                }
            } catch (err) {
                console.error('Error creating snippet:', err);
                alert('Failed to create snippet. Please try again.');
            }
        });
    }

// Initialize the modal when the page loads
    initCreateSnippetModal();

// Fetch snippets on page load
    fetchSnippets();

}); // End of DOMContentLoaded