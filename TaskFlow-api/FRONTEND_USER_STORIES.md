# Frontend User Stories - TaskFlow React Application

## Project Overview
Build a modern, responsive React application for the TaskFlow project management system that consumes the REST API backend.

**Tech Stack:**
- React 18+ with TypeScript
- React Router for navigation
- Axios for API calls
- Context API or Redux for state management
- shadcn/ui (Radix + Tailwind) for modern components
- Tailwind CSS for styling and layout
- React Hook Form for form validation
- JWT token management

---

## Epic 1: Authentication & User Management

### Story 1.1: User Login
**As a** user  
**I want to** log in with my email and password  
**So that** I can access my projects and tasks

**Story Points:** 5

**Acceptance Criteria:**
- Login page with email and password fields
- Form validation (valid email format, required fields)
- Display error messages for invalid credentials (401)
- Store JWT token in localStorage/sessionStorage on success
- Redirect to dashboard after successful login
- Show loading spinner during authentication
- Remember me checkbox (optional)
- Password visibility toggle

**API Integration:**
- `POST /api/auth/login`
- Request: `{ email, password }`
- Response: `{ token, email, name }`

**Technical Notes:**
- Create AuthContext for global auth state
- Implement private route wrapper
- Handle token expiration
- Clear token on logout
- Use shadcn/ui components: Input, Button, Card, Skeleton/Loader
- Use `Button` with `size="icon"` and lucide-react `Eye`/`EyeOff` icons for password toggle (with `animate-spin` for loading)

---

### Story 1.2: Logout Functionality
**As a** logged-in user  
**I want to** log out of the application  
**So that** I can secure my account

**Story Points:** 2

**Acceptance Criteria:**
- Logout button visible in header/navbar
- Clear JWT token from storage
- Redirect to login page
- Clear all user-related state

**Technical Notes:**
- Update AuthContext
- Clear axios default headers
- Redirect using React Router

---

### Story 1.3: Protected Routes
**As a** developer  
**I want to** protect routes that require authentication  
**So that** unauthorized users cannot access secured pages

**Story Points:** 3

**Acceptance Criteria:**
- Redirect to login if user is not authenticated
- Persist authentication state across page refreshes
- Check token validity before allowing access
- Show loading state while checking authentication

**Technical Notes:**
- Create PrivateRoute component
- Implement token validation
- Handle expired tokens gracefully

---

### Story 1.4: Test User Credentials Display
**As a** developer/user  
**I want to** see available test user credentials on the login page  
**So that** I can log in without needing to reference documentation

**Story Points:** 1

**Acceptance Criteria:**
- Display collapsible "Test Users" section on login page
- Show test user emails and password
- Styled to not interfere with main login form
- Easy to copy credentials
- Can be hidden/removed in production

**Test Users:**
- john@example.com / password123
- jane@example.com / password123
- admin@example.com / password123

**Technical Notes:**
- Create TestCredentials component
- Use environment variable to show/hide in production
- Consider adding "Use this account" quick-fill buttons

---

## Epic 2: Dashboard & Project List

### Story 2.1: Dashboard Overview
**As a** user  
**I want to** see an overview dashboard  
**So that** I can quickly understand my project status

**Story Points:** 5

**Acceptance Criteria:**
- Display welcome message with user's name
- Show total projects count
- Show total tasks count
- Show completed tasks count
- Display overall progress percentage
- Show recent projects (last 5)
- Responsive grid layout

**API Integration:**
- `GET /api/projects` - fetch all projects
- Response: Array of `{ id, title, description, userId, createdAt, updatedAt }`
- Calculate statistics from projects and tasks locally

**Technical Notes:**
- Create Dashboard component
- Implement statistics calculations
- Use shadcn `Skeleton` while fetching data
- Use shadcn `Card` for layout and Tailwind typography classes
- Use Tailwind Grid/Flex utilities for responsive layout

---

### Story 2.2: Projects List View
**As a** user  
**I want to** view all my projects in a list  
**So that** I can manage and navigate to specific projects

**Story Points:** 5

**Acceptance Criteria:**
- Display all projects in a card/list layout
- Show project title, description (truncated), and created date
- Show task count and progress bar for each project
- Click on project card to view details
- Empty state when no projects exist
- Search/filter functionality (bonus)
- Sort by date, name, or progress (bonus)

**API Integration:**
- `GET /api/projects`

**Technical Notes:**
- Create `ProjectCard` component using shadcn `Card`
- Use shadcn `Progress` for progress bars
- Use Tailwind Grid/Masonry-like layout with CSS columns
- Add debounced search with shadcn `Input`

---

### Story 2.3: Create New Project
**As a** user  
**I want to** create a new project  
**So that** I can organize my tasks

**Story Points:** 5

**Acceptance Criteria:**
- "Create Project" button on dashboard/projects page
- Modal or separate page with form (title, description)
- Form validation (title required, max lengths)
- Show success message after creation
- Redirect to project details or refresh list
- Handle API errors gracefully

**API Integration:**
- `POST /api/projects`
- Request: `{ title, description }`
- Response: `{ id, title, description, userId, createdAt, updatedAt }`

**Technical Notes:**
- Create `ProjectForm` component
- Use React Hook Form with shadcn `Form` + `Input` components
- Use shadcn `Dialog` or dedicated page for form
- Use `Button` with loading state (lucide `Loader2` icon + `animate-spin`)
- Optimistic UI updates (optional)

---

## Epic 3: Project Details & Management

### Story 3.1: View Project Details
**As a** user  
**I want to** view detailed information about a project  
**So that** I can see all associated tasks and progress

**Story Points:** 5

**Acceptance Criteria:**
- Display project title and description
- Show created and updated timestamps
- Display progress bar with percentage
- List all tasks for the project
- Show task counts (total, completed, pending)
- Back button to return to projects list

**API Integration:**
- `GET /api/projects/{id}`
- `GET /api/projects/{projectId}/tasks`
- `GET /api/projects/{id}/progress`

**Technical Notes:**
- Create `ProjectDetails` component
- Fetch project and tasks separately or in parallel
- Handle 404 if project not found
- Use shadcn `Card`, `Separator`, `Badge` components
- Use `Button` with `size="icon"` and lucide `ArrowLeft` for navigation

---

### Story 3.2: Project Progress Visualization
**As a** user  
**I want to** see visual progress of my project  
**So that** I can track completion status

**Story Points:** 3

**Acceptance Criteria:**
- Circular or linear progress bar
- Display percentage text
- Show completed/total task count
- Color coding (red < 30%, yellow 30-70%, green > 70%)
- Real-time updates when tasks are completed

**API Integration:**
- `GET /api/projects/{id}/progress`
- Response: `{ projectId, projectTitle, totalTasks, completedTasks, progressPercentage }`

**Technical Notes:**
- Use shadcn `Progress` (linear) or custom circular indicator
- Use Tailwind and semantic headings for percentage display
- Color coding via Tailwind classes (`text-red-600`, `text-yellow-600`, `text-green-600`)
- Auto-refresh on task updates

---

## Epic 4: Task Management

### Story 4.1: View Tasks List
**As a** user  
**I want to** view all tasks in a project  
**So that** I can see what needs to be done

**Story Points:** 5

**Acceptance Criteria:**
- Display tasks in a list/card layout
- Show task title, description, due date
- Show completion status (checkbox or badge)
- Visual distinction between completed/pending tasks
- Filter by status (all, completed, pending)
- Sort by due date, created date
- Empty state when no tasks exist

**API Integration:**
- `GET /api/projects/{projectId}/tasks`
- Response: Array of `{ id, title, description, dueDate, isCompleted, projectId, createdAt, updatedAt }`

**Technical Notes:**
- Create `TaskList` component
- Create `TaskItem` using semantic list and Tailwind utility classes
- Use shadcn `Checkbox` for completion status
- Use shadcn `Badge` for status chips
- Implement filtering and sorting locally

---

### Story 4.2: Create New Task
**As a** user  
**I want to** add a new task to a project  
**So that** I can track work items

**Story Points:** 5

**Acceptance Criteria:**
- "Add Task" button on project details page
- Form with title, description, due date
- Form validation (title required)
- Date picker for due date
- Show success message
- Task appears in list immediately
- Handle validation errors from API

**API Integration:**
- `POST /api/projects/{projectId}/tasks`
- Request: `{ title, description, dueDate }` (dueDate is optional)
- Response: `{ id, title, description, dueDate, isCompleted, projectId, createdAt, updatedAt }`

**Technical Notes:**
- Create `TaskForm` component
- Use shadcn Date Picker pattern (`Calendar` + `Popover`) with `date-fns`
- Use shadcn `Input`, `Button`, `Dialog` components
- Validate due date (optional warning for past dates)

---

### Story 4.3: Toggle Task Completion
**As a** user  
**I want to** mark tasks as complete or incomplete  
**So that** I can track my progress

**Story Points:** 3

**Acceptance Criteria:**
- Checkbox or toggle button on each task
- Visual feedback on click (strikethrough, opacity)
- Update progress bar immediately
- No confirmation needed (quick action)
- Handle toggle errors gracefully
- Optimistic UI update

**API Integration:**
- `PATCH /api/tasks/{id}/complete`
- Response: `{ id, title, description, dueDate, isCompleted, projectId, createdAt, updatedAt }`

**Technical Notes:**
- Handle optimistic update
- Revert on error
- Update project progress
- Use shadcn `Checkbox` with a clickable row (Tailwind `hover:bg-muted`)
- Use Tailwind `line-through` class for completed tasks

---

### Story 4.4: Delete Task
**As a** user  
**I want to** delete a task  
**So that** I can remove tasks that are no longer needed

**Story Points:** 2

**Acceptance Criteria:**
- Delete button/icon on each task
- Confirmation dialog (optional for quick UX)
- Remove task from list immediately
- Update project progress
- Show success message

**API Integration:**
- `DELETE /api/tasks/{id}`
- Response: 204 No Content (success even if task doesn't exist - idempotent)

**Technical Notes:**
- Idempotent delete (API supports it)
- Update local state
- Update progress automatically

---

### Story 4.5: Task Due Date Indicators
**As a** user  
**I want to** see visual indicators for task due dates  
**So that** I can prioritize urgent tasks

**Story Points:** 2

**Acceptance Criteria:**
- Color-coded due dates (red = overdue, yellow = due soon, green = future)
- "Overdue" badge on past due dates
- "Due today" badge
- Sort by urgency option
- Filter by overdue tasks

**Technical Notes:**
- Create utility functions for date comparison
- Use shadcn `Badge` with Tailwind color classes (error, warning, success)
- Implement relative date display (e.g., "2 days ago")
- Use shadcn `Tooltip` (Radix Tooltip) for additional date info

---

## Epic 5: UI/UX Enhancements

### Story 5.1: Responsive Design
**As a** user  
**I want to** use the application on any device  
**So that** I can access my projects on mobile, tablet, or desktop

**Story Points:** 5

**Acceptance Criteria:**
- Mobile-first responsive design
- Hamburger menu on mobile
- Touch-friendly buttons and interactions
- Optimized layouts for different screen sizes
- Test on multiple devices

**Technical Notes:**
- Use Tailwind Grid and Flex utilities
- Implement mobile navigation with shadcn `Sheet`
- Use Tailwind breakpoints (`sm`, `md`, `lg`, `xl`)
- Test with Chrome DevTools

---

### Story 5.2: Loading States
**As a** user  
**I want to** see loading indicators  
**So that** I know the application is processing my request

**Story Points:** 2

**Acceptance Criteria:**
- Skeleton loaders for data fetching
- Spinner for button actions
- Progress bar for page loads
- Disable buttons during submission
- Consistent loading patterns across app

**Technical Notes:**
- Use shadcn `Skeleton` for loaders
- Use lucide `Loader2` + `animate-spin` for button/page spinners
- Use shadcn `Progress` for determinate progress; use `nprogress` for top page progress bar (optional)
- Use loading state in all async operations

---

### Story 5.3: Error Handling & Messages
**As a** user  
**I want to** see clear error messages  
**So that** I understand what went wrong and how to fix it

**Story Points:** 3

**Acceptance Criteria:**
- Toast notifications for success/error messages
- Inline validation errors on forms
- Friendly error messages (not technical jargon)
- Network error handling (offline, timeout)
- 404 pages for invalid routes
- Retry mechanism for failed requests

**Technical Notes:**
- Use shadcn `use-toast` (or `sonner`) for toast notifications
- Use shadcn `Alert` component for inline messages
- Create ErrorBoundary component
- Map API errors to user-friendly messages

---

### Story 5.4: Confirmation Dialogs
**As a** user  
**I want to** confirm destructive actions  
**So that** I don't accidentally delete important data

**Story Points:** 2

**Acceptance Criteria:**
- Confirmation for delete project
- Confirmation for delete task (optional)
- Modal with clear action buttons
- Escape key to cancel
- Click outside to cancel

**Technical Notes:**
- Use shadcn `Dialog` component
- Use `DialogHeader`, `DialogContent`, `DialogFooter`
- Accessible (keyboard navigation, ARIA via Radix)

---

### Story 5.5: Search & Filter
**As a** user  
**I want to** search and filter projects and tasks  
**So that** I can quickly find what I'm looking for

**Story Points:** 3

**Acceptance Criteria:**
- Search bar for projects (filter by title)
- Search bar for tasks (filter by title)
- Filter tasks by status (all, completed, pending)
- Filter tasks by due date (overdue, today, upcoming)
- Clear filters button
- Real-time filtering (no submit button)

**Technical Notes:**
- Client-side filtering
- Debounce search input
- URL query params for filters (optional)

---

### Story 5.6: Dark Mode
**As a** user  
**I want to** toggle between light and dark themes  
**So that** I can use the app comfortably in different lighting

**Story Points:** 3

**Acceptance Criteria:**
- Theme toggle button in header
- Persist theme preference
- Smooth transition between themes
- All components support both themes
- System preference detection (optional)

**Technical Notes:**
- Use `ThemeProvider` with `next-themes` (or equivalent) to manage light/dark
- Store preference in localStorage via `next-themes`
- Use Tailwind's dark mode class strategy
- Create custom theme via CSS variables and Tailwind config

---

## Epic 6: Advanced Features (Bonus)

### Story 6.1: Drag & Drop Task Ordering
**As a** user  
**I want to** reorder tasks by dragging  
**So that** I can prioritize my work

**Story Points:** 5

**Acceptance Criteria:**
- Drag tasks to reorder
- Visual feedback during drag
- Persist order to backend (if API supports)
- Works on touch devices

**Technical Notes:**
- Use react-beautiful-dnd or dnd-kit
- Store order in local state or backend

---

### Story 6.2: Task Categories/Tags
**As a** user  
**I want to** categorize tasks with tags  
**So that** I can organize related tasks

**Story Points:** 5

**Acceptance Criteria:**
- Add/remove tags on tasks
- Filter tasks by tags
- Color-coded tags
- Auto-suggest existing tags

**Technical Notes:**
- Extend API to support tags
- Multi-select tag component

---

### Story 6.3: Notifications
**As a** user  
**I want to** receive notifications for upcoming deadlines  
**So that** I don't miss important tasks

**Story Points:** 5

**Acceptance Criteria:**
- Browser notifications (if permitted)
- In-app notification center
- Notification preferences
- Snooze functionality

**Technical Notes:**
- Use Web Notifications API
- Request permission
- Check due dates on app load

---

### Story 6.4: Collaborative Features
**As a** user  
**I want to** share projects with other users  
**So that** we can collaborate

**Story Points:** 8

**Acceptance Criteria:**
- Invite users by email
- Assign tasks to team members
- View team member's tasks
- Permission levels (owner, editor, viewer)

**Technical Notes:**
- Requires backend API changes
- Real-time updates with WebSocket (optional)

---

## Summary

### Total Story Points: 97

**Note:** User registration is not available in the current backend. The application uses pre-seeded test users created by the DataSeeder.

**Note:** Project update and delete functionality is now available.

**Note:** Task edit functionality (title/description) is now available, in addition to create, toggle completion, and delete.

### Epics Breakdown:
1. **Authentication & User Management**: 11 points
2. **Dashboard & Project List**: 15 points
3. **Project Details & Management**: 8 points
4. **Task Management**: 17 points
5. **UI/UX Enhancements**: 18 points
6. **Advanced Features (Bonus)**: 28 points

### Development Phases:

**Phase 1 - MVP (44 points, 2-3 weeks)**
- Authentication (login, logout, protected routes)
- Projects list, create, view
- Tasks list, create, toggle completion
- Basic styling and error handling

**Phase 2 - Core Features (23 points, 1-2 weeks)**
- Delete tasks
- Progress visualization
- Dashboard overview
- Loading states

**Phase 3 - UX Polish (18 points, 1 week)**
- Responsive design
- Search and filter
- Confirmation dialogs
- Better error handling
- Dark mode

**Phase 4 - Advanced (Optional)**
- Drag & drop
- Tags/categories
- Notifications
- Collaboration

---

## Technical Setup Recommendations

### Project Structure:
```
src/
├── components/
│   ├── common/          # Reusable components
│   ├── auth/            # Login, Register
│   ├── projects/        # Project list, card, form
│   ├── tasks/           # Task list, item, form
│   └── layout/          # Header, Footer, Sidebar
├── pages/               # Page components
├── contexts/            # React Context (Auth, Theme)
├── hooks/               # Custom hooks
├── services/            # API calls (axios)
├── utils/               # Helper functions
├── types/               # TypeScript types
└── styles/              # Global styles

```

### Key Libraries:
- **React Router** - Navigation
- **Axios** - HTTP client
- **React Hook Form** - Form handling
- **Yup** - Schema validation
- **React Query** - Server state management (optional)
- **shadcn/ui** - UI components built on Radix + Tailwind
- **lucide-react** - Icon set (Eye, EyeOff, Loader2, ArrowLeft, etc.)
- **Radix UI** - Accessible primitives (Dialog, Tooltip, Popover)
- **date-fns** - Date manipulation
- **sonner** or **shadcn/use-toast** - Toast notifications
### shadcn/ui Setup Notes:

For a modern, consistent UI:
- Initialize Tailwind CSS in your React app and configure dark mode (`class`).
- Install shadcn/ui and generate required components (Button, Card, Dialog, Progress, Skeleton, Badge, Checkbox, Tooltip, Calendar).
- Use lucide-react icons; prefer `Loader2` for spinners with `animate-spin`.
- Replace MUI-specific components with shadcn counterparts referenced throughout these stories.

Recommended baseline commands (Vite + Tailwind):

```
# Create app (if starting fresh)
npm create vite@latest taskflow-frontend -- --template react-ts
cd taskflow-frontend

# Install Tailwind
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p

# Install shadcn/ui and icons
npm install lucide-react radix-ui react-hook-form date-fns
npx shadcn@latest init
npx shadcn@latest add button card dialog progress skeleton badge checkbox tooltip calendar input form toast sheet separator
```

Then wire up `ThemeProvider` (using `next-themes` or a simple context) and Tailwind dark mode.

### Environment Variables:
```
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_TOKEN_KEY=taskflow_token
```

---

## API Endpoints Reference

**Authentication:** All endpoints except `/api/auth/login` require the `Authorization` header with format: `Bearer <token>`

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/auth/login` | User login |
| GET | `/api/projects` | Get all projects |
| POST | `/api/projects` | Create project |
| GET | `/api/projects/{id}` | Get project by ID |
| PUT | `/api/projects/{id}` | Update project (title, description) |
| DELETE | `/api/projects/{id}` | Delete project and its tasks |
| GET | `/api/projects/{id}/progress` | Get project progress |
| GET | `/api/projects/{projectId}/tasks` | Get project tasks |
| POST | `/api/projects/{projectId}/tasks` | Create task |
| PATCH | `/api/tasks/{id}/complete` | Toggle task completion |
| PATCH | `/api/tasks/{id}` | Update task (title and/or description) |
| DELETE | `/api/tasks/{id}` | Delete task |

**Note:** Project update and delete endpoints are available.

**Note:** Task update endpoint (title/description) is available.

**Note:** DELETE operations are idempotent - they return 204 No Content even if the resource doesn't exist.

### Response Formats:

**LoginResponse:**
```json
{ "token": "jwt.token.here", "email": "user@example.com", "name": "User Name" }
```

**ProjectResponse:**
```json
{ "id": 1, "title": "Project Title", "description": "Description", "userId": 1, "createdAt": "2025-12-21T...", "updatedAt": "2025-12-21T..." }
```

**TaskResponse:**
```json
{ "id": 1, "title": "Task Title", "description": "Description", "dueDate": "2025-12-25", "isCompleted": false, "projectId": 1, "createdAt": "2025-12-21T...", "updatedAt": "2025-12-21T..." }
```

**ProjectProgressResponse:**
```json
{ "projectId": 1, "projectTitle": "Project Title", "totalTasks": 10, "completedTasks": 7, "progressPercentage": 70.0 }
```

---

**Ready to start building! 🚀**
