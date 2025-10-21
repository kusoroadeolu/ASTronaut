# Astronaut Frontend UI Generation Prompt

You are building a vanilla HTML/CSS/JavaScript frontend for **Astronaut**, a code snippet management and analysis platform.

## Project Overview

**Purpose:** Allow users to create, organize, search, and compare Java code snippets with automatic metadata extraction.

**Tech Stack:** HTML5, CSS3, Vanilla JavaScript (no frameworks)

**Design Philosophy:** Clean, minimal, modern, developer-focused. Dark mode with bright accent colors.

**Color Palette:**
- **Background:** Deep Navy (#0f172a or #1e293b)
- **Accent:** Electric Blue (#3b82f6) or Cyan (#06b6d4)
- **Secondary Text/Borders:** Gray (#94a3b8 or #64748b)
- **Success:** Green (#10b981)
- **Error/Delete:** Red (#ef4444)
- **Warning:** Amber (#f59e0b)
- **Diff Added:** Light Green (#d4edda)
- **Diff Removed:** Light Red (#f8d7da)
- **Light Surface:** #f1f5f9

**Typography:**
- Font Family: System fonts (-apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif)
- Code: Monospace ("Courier New", monospace)
- Sizes: 12px, 14px, 16px, 18px, 20px, 24px

---

## Pages to Generate

### 1. Landing Page (`index.html`)

**Purpose:** Entry point explaining what Astronaut does. No authentication required.

**Layout:**
- Hero section with app name, tagline, and brief description
- Features overview section (what users can do)
- Call-to-action buttons: "Login" and "Get Started" (register)
- Footer with basic info

**Hero Section:**
- Large title: "Astronaut"
- Subtitle/tagline: Something explaining code snippet management (e.g., "Store, organize, and compare Java code snippets")
- Brief description of key benefits
- Two buttons: "Login" and "Get Started"

**Features Section:**
- List or grid showing 3-4 key features:
    - Create and organize snippets
    - Automatic metadata extraction
    - Advanced search and filtering
    - Compare and diff snippets

**Navigation:**
- Links to login/register in top right
- App name/logo in top left

**Design Notes:**
- Center content on page
- Use the navy/blue color scheme
- Make buttons prominent
- Keep it clean and simple — no clutter

---

### 2. Authentication Page (`auth.html`)

**Purpose:** Single page with both login and register forms. Toggle between them without page reload.

**Layout:**
- Centered form card
- App logo/name at top
- Form fields in middle
- Toggle link at bottom

**Register Form (initially hidden):**
- Username input field
- Email input field
- Password input field
- Confirm password input field
- Register button
- Link: "Already have an account?" → switches to login form
- Error message display area

**Login Form (initially visible):**
- Email input field
- Password input field
- Login button
- Link: "Don't have an account?" → switches to register form
- Error message display area

**Functionality:**
- Clicking register link shows register form, hides login form (smooth transition)
- Clicking login link shows login form, hides register form
- Both forms have error message areas for validation/API errors
- Submit buttons have loading state

**Design Notes:**
- Center the form card on page
- Use subtle shadow/border for card
- Make the toggle link obvious but not distracting
- Input fields should have clear focus states
- Show/hide password toggle on password fields (eye icon)

---

### 3. Dashboard Page (`dashboard.html`)

**Purpose:** Main hub after login. Users see their snippets, search, and navigate to other features.

**Header:**
- App name/logo on left
- User's username displayed
- Settings icon (gear) in top right that links to settings page
- Logout button (or in a user menu)

**Search Section (Top):**
- Simple search bar with placeholder "Search snippets by name or tags..."
- This searches across snippet names and tags

**Advanced Search Section (Collapsible):**
- Collapsed by default, click to expand
- Single input field for each metadata type, arranged vertically:
    - Method Return Types: [input field]
    - Class Names: [input field]
    - Class Annotations: [input field]
    - Class Fields: [input field]
    - Field Annotations: [input field]
    - Method Annotations: [input field]
    - Languages: [input field]
- Users type values separated by spaces or commas
- Buttons: [Search] [Clear Filters]
- Toggle: "Enable Fuzzy Search" (checkbox or toggle switch showing current state)

**Snippet Cards Grid:**
- Display snippets below search sections
- Each card shows:
    - Snippet name (prominent)
    - Language (badge, e.g., "Java")
    - Tags (pills/badges)
    - Username of creator (user's own username since these are their snippets)
    - Created date
    - Last updated date
- Cards are clickable → navigate to snippet detail page
- Cards have hover effect (slight shadow/scale)

**"Create New Snippet" Button:**
- Prominent button somewhere visible (top right, or floating button)
- Clicking it navigates to snippet creation/editor

**Empty State:**
- If user has no snippets, show a helpful message with CTA to create first snippet

**Pagination:**
- Show page navigation if more than 20 snippets (default page size)
- [Previous] [Page 1 of X] [Next] style

**Loading State:**
- Show skeleton/spinner while loading snippets

---

### 4. Snippet Detail Page (`snippet-detail.html`)

**Purpose:** View and edit individual snippets. Can also compare or delete from here.

**Read-Only Mode (default):**

**Header Section:**
- Snippet name (title)
- Language badge
- Tags displayed as pills
- Created date
- Last updated date

**Main Content (80/20 split):**
- **Left 80%:**
    - Code content displayed in monospace font
    - Syntax highlighting (use highlight.js or Prism)
    - Line numbers on left
    - Copy button (copies all code)
    - Read-only (not editable in this mode)

- **Right 20%:**
    - "Extra Notes" or "Explanation" section
    - Display the extra notes content
    - Read-only in this mode

**Buttons at Bottom:**
- "Edit" button → switches to edit mode
- "Delete" button → shows confirmation, then deletes
- "Compare" button → opens modal to select snippet to compare with
- "Back" button → return to dashboard

**Edit Mode (click Edit):**

**Form becomes editable:**
- Snippet name (text input)
- Language (dropdown: Java, Other)
- Tags (input field, can add/remove)
- Code content (textarea with monospace font)
- Extra notes (textarea)

**Buttons at Bottom:**
- "Save" button → saves changes and returns to read-only mode
- "Delete" button (available even in edit mode)
- "Cancel" button → discards changes, returns to read-only mode

**Compare Modal (click Compare):**
- Modal/popup appears
- Title: "Select snippet to compare with"
- List of all user's snippets (excluding current one)
- Each item clickable
- "Compare" button to confirm selection
- "Cancel" button to close modal
- After selecting and clicking Compare → page switches to side-by-side diff view

**Side-by-Side Diff View:**
- Header shows both snippet names being compared
- Left column: Original snippet code with line numbers
- Right column: Comparing-to snippet code with line numbers
- Line highlighting:
    - **Green background:** Lines added in right snippet
    - **Red background:** Lines removed from left snippet
    - **Default background:** Unchanged lines
- Synchronized scrolling between columns
- "Back to Normal View" button to exit diff mode

**Design Notes:**
- Code section should have good contrast for readability
- Monospace font for all code
- Line numbers should be subtle (lighter color)
- Buttons should be spaced clearly
- Edit mode should visually distinguish from read-only (maybe subtle background color change on form)
- Diff highlighting should be clear but not overwhelming

---

### 5. Settings Page (`settings.html`)

**Purpose:** User account management and preferences.

**Header:**
- "Settings" title
- "Back" button to return to dashboard

**Account Settings Section:**
- **Username:**
    - Current username displayed
    - "Edit" button or clickable field to change
    - After clicking: text input field with "Save" button

- **Email:**
    - Current email displayed
    - "Edit" button or clickable field to change
    - After clicking: text input field with "Save" button

**Password Section:**
- Current password input field (with show/hide toggle)
- New password input field (with show/hide toggle)
- Confirm new password input field (with show/hide toggle)
- "Update Password" button

**Preferences Section:**
- Toggle/checkbox: "Enable Fuzzy Search"
- Display current state (enabled/disabled)

**Danger Zone Section (highlighted in red/amber):**
- Warning text: "Deleting your account is permanent. You will lose all your snippets."
- "Delete My Account" button (red)
- Clicking it opens confirmation modal

**Delete Account Confirmation Modal:**
- Title: "Delete Account?"
- Warning message: "This action cannot be undone. All your snippets will be deleted."
- Require user to type their username to confirm
- "Cancel" button
- "Delete Account" button (red, disabled until username is typed correctly)

**Logout Button:**
- Somewhere prominent (top right, or in settings)
- Clicking it logs user out and redirects to login page

**Success/Error Messages:**
- Show feedback when settings are updated (e.g., "Username updated successfully")
- Show errors from API (e.g., "Email already in use")

**Design Notes:**
- Group settings into logical sections with clear headers
- Use red/warning color for destructive actions
- Make confirmation modals clear and scary (so users don't accidentally delete)
- Show loading state on buttons while saving

---

## Code Organization

**Per page, provide:**

1. **HTML Structure**
    - Semantic markup (buttons are `<button>`, links are `<a>`)
    - Clear IDs and classes
    - Accessibility attributes (labels for inputs, aria-labels where needed)

2. **CSS (in `<style>` tag)**
    - Mobile-first responsive design
    - CSS variables for colors, spacing, typography
    - Consistent naming convention
    - Transitions for smooth interactions (200ms default)
    - Focus states for all interactive elements

3. **JavaScript (in `<script>` tag)**
    - Event listeners for buttons/forms
    - Form validation before submission
    - Error handling with user-friendly messages
    - Loading states (spinners, button disabled state)
    - Smooth transitions between view modes (read-only ↔ edit)
    - Modal open/close functionality
    - Comments explaining logic

**File Structure:**
```
index.html (landing page)
auth.html (login/register)
dashboard.html (snippet list)
snippet-detail.html (view/edit/compare)
settings.html (account management)
```

---

## Design System

### Buttons
- Primary: Blue background (#3b82f6), white text
- Danger: Red background (#ef4444), white text
- Secondary: Gray background (#64748b), white text
- Hover: Slightly darker shade
- Active/Clicked: Even darker shade
- Disabled: Gray, no hover effect
- Loading state: Show spinner inside button, disable interaction

### Forms
- Input fields: Dark background (#1e293b), light text (#f1f5f9)
- Placeholder text: Gray (#94a3b8)
- Focus state: Blue border (#3b82f6), slight glow
- Error state: Red border (#ef4444)
- Labels: Light gray (#cbd5e1)

### Cards
- Background: Slightly lighter than main background (#334155)
- Border: Subtle (#475569)
- Shadow: Subtle (box-shadow: 0 1px 3px rgba(0,0,0,0.1))
- Hover: Slight lift and shadow increase

### Modals
- Semi-transparent overlay (dark background with opacity)
- Card centered on screen
- Clear close button (X in top right)
- Buttons at bottom

### Spacing & Sizing
- Use 8px/16px/24px/32px increments for consistency
- Max content width: 1200px on large screens
- Padding inside cards/containers: 16px or 24px
- Margin between sections: 24px or 32px

---

## Interactions & Animations

- **Transitions:** Use 200ms cubic-bezier(0.4, 0, 0.2, 1) for smooth motion
- **Form Toggle:** Fade in/fade out when switching between login and register
- **Edit Mode Toggle:** Fade or slide transition
- **Hover Effects:** Cards scale slightly (1.02), shadow increases
- **Loading:** Show spinner/skeleton while data loads
- **Success Messages:** Toast or temporary message (auto-dismiss after 3-4 seconds)
- **Errors:** Show error message and keep it visible until dismissed
- **CRUD Toasts:** When snippets are created, updated, or deleted, show a toast notification that appears and fades out after 3-4 seconds (don't require user to dismiss)

---


**Important:** Generate HTML and CSS only. Do NOT write JavaScript for API calls or interactions yet. Use placeholder JavaScript comments where behavior will be added later (e.g., `// TODO: fetch snippets on page load`).

Examples of what NOT to include yet:
- Actual API fetch calls
- Form submission handlers
- Event listeners for buttons
- State management

What TO include:
- HTML structure with semantic markup
- CSS styling and layout
- Placeholder comments for where JS will go
- Basic form validation (HTML5 validation attributes like `required`, `type="email"`)

---

## Deliverables

Generate 5 HTML files:
1. `index.html` + `index.css` — Landing page
2. `auth.html` + `auth.css` — Login/Register (single page, toggle forms)
3. `dashboard.html` + `dashboard.css` — Snippet list with search
4. `snippet-detail.html` + `snippet-detail.css` — View/edit/compare snippets
5. `settings.html` + `settings.css` — Account management

**Each HTML file:**
- Link to its corresponding CSS file in `<head>`
- Semantic markup with clear IDs and classes
- Placeholder JavaScript comments for where interactions will be added later
- No actual JavaScript functionality yet

**Each CSS file:**
- Complete styling for the page
- CSS variables for colors, spacing, typography
- Mobile-first responsive design
- Smooth transitions and hover states
- Focus states for accessibility

All files should be production-ready (no scaffolding or TODOs in the HTML/CSS structure itself — only comments in reserved areas for JS logic).

---

## Design Tone

Build for developers. Keep it minimal, professional, dark-themed, modern. Bright accent colors for interactive elements. Good contrast. Smooth transitions. Clear hierarchy. Functional over flashy.




# Astronaut Frontend UI Generation Prompt

Build a vanilla HTML/CSS frontend for a code snippet management platform. 5 pages, separate HTML and CSS files for each.

## Colors & Typography

**Palette:**
- Background: Deep Navy (#0f172a)
- Accent: Electric Blue (#3b82f6)
- Text/Borders: Gray (#94a3b8, #64748b)
- Success: Green (#10b981)
- Error: Red (#ef4444)
- Diff Added: Light Green (#d4edda)
- Diff Removed: Light Red (#f8d7da)

**Fonts:**
- Body: System fonts (-apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif)
- Code: Monospace ("Courier New", monospace)

---

## Pages

### 1. Landing Page (`index page')

Entry point before login. Hero section explaining Astronaut, features overview, buttons to Login and Get Started.

---

### 2. Auth Page (`auth page')

Single page with login and register forms. Toggle between them without reload.

**Login Form (visible by default):**
- Email, password inputs
- Login button
- Link: "Don't have an account?" → shows register form

**Register Form (hidden):**
- Username, email, password, confirm password inputs
- Register button
- Link: "Already have an account?" → shows login form

Both forms centered, error message area below form fields.

---

### 3. Dashboard (`dashboard page')

Main hub after login. User's snippets displayed as cards.

**Header:**
- App name on left
- Username displayed
- Settings icon (gear) top right linking to settings page

**Search Section:**
- Simple search bar: "Search snippets by name or tags..."

**Advanced Search (Collapsible):**
- Single input field for each metadata type (method return types, class names, annotations, etc.)
- Values separated by spaces/commas
- Buttons: [Search] [Clear Filters]
- Checkbox: "Enable Fuzzy Search"

**Snippet Cards Grid:**
- Each card shows: name, language badge, tags, username, created/updated dates
- Cards are clickable → go to snippet detail page
- Hover effect (slight shadow/scale)

**Create New Snippet Button:**
- Prominent button to create new snippet

**Pagination:**
- [Previous] [Page 1 of X] [Next] style

---

### 4. Snippet Detail (`snippet-detail pages')

View and edit individual snippets.

**Read-Only Mode (default):**
- Header: snippet name, language badge, tags, created/updated dates
- 80/20 split:
    - Left 80%: code with syntax highlighting, line numbers, copy button
    - Right 20%: extra notes/explanation
- Buttons: [Edit] [Delete] [Compare] [Back]

**Edit Mode (click Edit):**
- All fields editable: name, language dropdown, tags input, code textarea, extra notes textarea
- Buttons: [Save] [Delete] [Cancel]

**Compare (click Compare):**
- Modal appears with list of user's snippets
- User selects one → click Compare
- Page shows side-by-side diff:
    - Left: original code with line numbers
    - Right: comparing-to code with line numbers
    - Green highlighting: added lines
    - Red highlighting: removed lines
    - Synchronized scrolling between columns
- [Back to Normal View] button

---

### 5. Settings (`settings page')

Account management and preferences.

**Sections:**
- **Account:** Edit username, edit email (each with Edit/Save buttons)
- **Password:** Current password, new password, confirm new password inputs + [Update Password] button
- **Preferences:** Toggle checkbox "Enable Fuzzy Search"
- **Danger Zone:** [Delete My Account] button (red)
    - Opens confirmation modal requiring user to type username
- **Logout:** Button at top right

Success/error messages for each action. Loading states on buttons.

---

## Design System

**Buttons:**
- Primary: Blue bg, white text, darker on hover
- Danger: Red bg, white text
- Disabled: Gray, no interaction
- Loading: Show spinner, disable interaction

**Forms:**
- Dark input bg (#1e293b), light text
- Focus: Blue border (#3b82f6) with glow
- Error: Red border
- Labels: Light gray

**Cards:**
- Bg: #334155, subtle border
- Shadow: 0 1px 3px rgba(0,0,0,0.1)
- Hover: lifted with increased shadow

**Spacing:**
- Use 8px/16px/24px/32px increments
- Max width: 1200px
- Padding: 16px-24px

**Interactions:**
- Smooth transitions: 200ms cubic-bezier(0.4, 0, 0.2, 1)
- Hover: cards scale 1.02
- Toast notifications: Auto-fade after 3-4 seconds (for snippet create/update/delete)
- Modals: Semi-transparent overlay, centered card

---

## Important Notes

 Generate HTML and CSS and JS if needed.

**File Structure:**
- Link CSS files in HTML head: `<link rel="stylesheet" href="filename.css">`
- Each page is self-contained
- Semantic HTML (proper button/link tags, input labels)
- Accessibility: aria-labels where needed, focus states


## Deliverables

5 HTML + CSS file pairs:
1. `index.html` + `index.css`
2. `auth.html` + `auth.css`
3. `dashboard.html` + `dashboard.css`
4. `snippet-detail.html` + `snippet-detail.css`
5. `settings.html` + `settings.css`

Each file complete and production-ready. HTML has semantic structure and placeholder comments. CSS has variables, responsive design, smooth animations.