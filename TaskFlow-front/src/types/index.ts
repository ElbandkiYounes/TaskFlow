// API Response Types
export interface LoginResponse {
  token: string;
  email: string;
  name: string;
}

export interface Project {
  id: number;
  title: string;
  description: string;
  userId: number;
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: number;
  title: string;
  description: string;
  dueDate: string | null;
  isCompleted: boolean;
  projectId: number;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectProgress {
  projectId: number;
  projectTitle: string;
  totalTasks: number;
  completedTasks: number;
  progressPercentage: number;
}

// Form Types
export interface LoginFormData {
  email: string;
  password: string;
}

export interface ProjectFormData {
  title: string;
  description: string;
}

export interface TaskFormData {
  title: string;
  description: string;
  dueDate?: string;
}

// Auth Context Types
export interface AuthContextType {
  user: { email: string; name: string } | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
}
