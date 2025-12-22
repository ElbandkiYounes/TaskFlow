# TaskFlow Frontend

A modern, responsive React application for project and task management built with React 18, TypeScript, and Tailwind CSS.

## 🚀 Features

### Phase 1 - MVP (Implemented)
- ✅ **Authentication**
  - Login with JWT token management
  - Protected routes with automatic redirect
  - Test user credentials display on login page
  - Automatic token refresh handling

- ✅ **Dashboard**
  - Overview statistics (projects, tasks, completion rate)
  - Overall progress visualization
  - Recent projects display
  - Quick navigation to projects

- ✅ **Project Management**
  - View all projects with progress indicators
  - Create new projects with title and description
  - Project details with comprehensive information
  - Task count and completion tracking

- ✅ **Task Management**
  - View tasks by project (pending/completed sections)
  - Create tasks with title, description, and due date
  - Toggle task completion status
  - Delete tasks with confirmation dialog
  - Due date indicators (overdue, today, upcoming)
  - Visual feedback for completed tasks

- ✅ **UI/UX**
  - Responsive design (mobile, tablet, desktop)
  - Loading skeletons for better UX
  - Toast notifications for user feedback
  - Error handling with user-friendly messages
  - Modern UI with shadcn/ui components
  - Smooth transitions and hover effects

## 🛠️ Tech Stack

- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **React Router** - Client-side routing
- **Tailwind CSS** - Utility-first styling
- **shadcn/ui** - Accessible component library (Radix UI + Tailwind)
- **Axios** - HTTP client
- **React Hook Form** - Form validation
- **date-fns** - Date manipulation
- **Sonner** - Toast notifications
- **Lucide React** - Icon library

## 📋 Prerequisites

- Node.js 20.18.0 or higher
- npm 10.8.2 or higher
- TaskFlow backend API running on `http://localhost:8080`

## 🚀 Getting Started

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Environment Configuration

The `.env` file is already configured with the default API endpoint:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

If your backend runs on a different port, update this file accordingly.

### 3. Start Development Server

```bash
npm run dev
```

The application will be available at `http://localhost:5173`

### 4. Build for Production

```bash
npm run build
```

The production build will be in the `dist` folder.

### 5. Preview Production Build

```bash
npm run preview
```

## 👥 Test User Credentials

The application comes with test user credentials displayed on the login page:

- **John Doe**: `john@example.com` / `password123`
- **Jane Smith**: `jane@example.com` / `password123`
- **Admin User**: `admin@example.com` / `password123`

These users are pre-seeded in the backend database.

## 📁 Project Structure

```
src/
├── components/
│   ├── auth/              # Authentication components
│   │   └── PrivateRoute.tsx
│   ├── layout/            # Layout components
│   │   └── Layout.tsx
│   ├── projects/          # Project-related components
│   │   └── CreateProjectDialog.tsx
│   ├── tasks/             # Task-related components
│   │   ├── TaskList.tsx
│   │   ├── CreateTaskDialog.tsx
│   │   └── DeleteTaskDialog.tsx
│   └── ui/                # shadcn/ui components
│       ├── button.tsx
│       ├── card.tsx
│       ├── input.tsx
│       ├── label.tsx
│       ├── progress.tsx
│       ├── checkbox.tsx
│       ├── dialog.tsx
│       ├── badge.tsx
│       ├── separator.tsx
│       ├── skeleton.tsx
│       └── textarea.tsx
├── contexts/              # React Context providers
│   └── AuthContext.tsx
├── pages/                 # Page components
│   ├── Login.tsx
│   ├── Dashboard.tsx
│   ├── Projects.tsx
│   └── ProjectDetails.tsx
├── services/              # API services
│   └── api.ts
├── types/                 # TypeScript type definitions
│   └── index.ts
├── lib/                   # Utility functions
│   └── utils.ts
├── App.tsx                # Main app component with routing
├── main.tsx               # Application entry point
└── index.css              # Global styles with Tailwind
```

## 🔌 API Integration

The application connects to the TaskFlow REST API with the following endpoints:

### Authentication
- `POST /api/auth/login` - User login

### Projects
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project by ID
- `GET /api/projects/{id}/progress` - Get project progress
- `PUT /api/projects/{id}` - Update project title/description (requires Bearer token)
- `DELETE /api/projects/{id}` - Delete project and cascade tasks (idempotent, requires Bearer token)

### Tasks
- `GET /api/projects/{projectId}/tasks` - Get tasks by project
- `POST /api/projects/{projectId}/tasks` - Create new task
- `PATCH /api/tasks/{id}/complete` - Toggle task completion
- `PATCH /api/tasks/{id}` - Partial update for task title/description (requires Bearer token)
- `DELETE /api/tasks/{id}` - Delete task

All authenticated requests include the JWT token in the `Authorization` header as `Bearer <token>`.

### Quick Axios Examples
- Update project: `axios.put(`${API}/projects/${projectId}`, { title, description }, authHeaders)`
- Delete project: `axios.delete(`${API}/projects/${projectId}`, authHeaders)`
- Update task: `axios.patch(`${API}/tasks/${taskId}`, { title, description }, authHeaders)`

> Note: For task updates, if `title` is provided it must not be blank (backend returns “Title, if provided, must not be blank”).

## 🎨 UI Components

The application uses **shadcn/ui**, a collection of accessible, customizable components built on Radix UI and styled with Tailwind CSS.

### Key Components Used:
- **Button** - Various actions and navigation
- **Card** - Content containers
- **Dialog** - Modals for forms and confirmations
- **Input/Textarea** - Form inputs
- **Checkbox** - Task completion toggle
- **Progress** - Visual progress indicators
- **Badge** - Status and date indicators
- **Skeleton** - Loading states
- **Separator** - Visual dividers

## 🎯 Features Breakdown

### Authentication Flow
1. User enters credentials on login page
2. JWT token received from backend
3. Token stored in localStorage
4. Token automatically attached to API requests
5. Auto-redirect to login on 401 errors

### Project Management Flow
1. View all projects on Projects page
2. Click "Create Project" to add new project
3. Fill form with title and description
4. Project created and displayed in list
5. Click project card to view details

### Task Management Flow
1. Navigate to project details
2. Click "Add Task" to create task
3. Fill form with title, description, and optional due date
4. Task appears in pending section
5. Check checkbox to mark complete
6. Task moves to completed section with visual changes
7. Click trash icon to delete (with confirmation)

## 🌈 Color-Coded Progress

- **Red (< 30%)** - Just started
- **Yellow (30-70%)** - In progress
- **Green (> 70%)** - Almost done

### Due Date Indicators

- **Red Badge** - Overdue tasks
- **Yellow Badge** - Due today
- **Gray Badge** - Upcoming (relative time)

## 🔐 Security Features

- JWT token-based authentication
- Automatic token expiration handling
- Protected routes with redirect
- Secure token storage in localStorage
- Authorization header on all API requests

## 📱 Responsive Design

The application is fully responsive with breakpoints for:
- **Mobile** - < 640px
- **Tablet** - 640px - 1024px
- **Desktop** - > 1024px

## 🚧 Known Limitations

Based on the backend API limitations:

- No user registration (uses pre-seeded users)
- No project editing or deletion (create and view only)
- No task editing (create, toggle, and delete only)
- No real-time updates (requires manual refresh)

## 🎭 Future Enhancements (Phase 2+)

Potential features for future development:

- **Dark Mode** - Theme toggle with system preference detection
- **Search & Filter** - Find projects and tasks quickly
- **Drag & Drop** - Reorder tasks by priority
- **Tags/Categories** - Organize tasks with labels
- **Notifications** - Browser notifications for due dates
- **Collaboration** - Share projects with team members
- **Export** - Download project data as CSV/PDF
- **Charts** - Advanced analytics and visualizations

## 🐛 Troubleshooting

### API Connection Issues
- Ensure the backend is running on `http://localhost:8080`
- Check the `.env` file for correct API URL
- Verify CORS is enabled on the backend

### Build Errors
- Delete `node_modules` and run `npm install` again
- Clear Vite cache: `rm -rf node_modules/.vite`
- Ensure Node.js version is compatible

### TypeScript Errors
- Run `npm run type-check` to see all type errors
- Restart VS Code/IDE TypeScript server
- Check tsconfig paths are correctly configured

## 📄 License

This project is part of the TaskFlow application suite.

## 🤝 Contributing

This is a demonstration project. For production use, additional features and security measures should be implemented.

---

**Happy Task Managing! 🎯**
