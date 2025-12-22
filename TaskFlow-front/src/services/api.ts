import axios from 'axios';
import type {
  LoginResponse,
  Project,
  Task,
  ProjectProgress,
  LoginFormData,
  ProjectFormData,
  TaskFormData,
} from '@/types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('taskflow_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('taskflow_token');
      localStorage.removeItem('taskflow_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  login: async (data: LoginFormData): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>('/auth/login', data);
    return response.data;
  },
};

// Projects API
export const projectsApi = {
  getAll: async (): Promise<Project[]> => {
    const response = await api.get<Project[]>('/projects');
    return response.data;
  },

  getById: async (id: number): Promise<Project> => {
    const response = await api.get<Project>(`/projects/${id}`);
    return response.data;
  },

  create: async (data: ProjectFormData): Promise<Project> => {
    const response = await api.post<Project>('/projects', data);
    return response.data;
  },

  update: async (id: number, data: ProjectFormData): Promise<Project> => {
    const response = await api.put<Project>(`/projects/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/projects/${id}`);
  },

  getProgress: async (id: number): Promise<ProjectProgress> => {
    const response = await api.get<ProjectProgress>(`/projects/${id}/progress`);
    return response.data;
  },
};

// Tasks API
export const tasksApi = {
  getByProjectId: async (projectId: number): Promise<Task[]> => {
    const response = await api.get<Task[]>(`/projects/${projectId}/tasks`);
    return response.data;
  },

  create: async (projectId: number, data: TaskFormData): Promise<Task> => {
    const response = await api.post<Task>(`/projects/${projectId}/tasks`, data);
    return response.data;
  },

  toggleComplete: async (id: number): Promise<Task> => {
    const response = await api.patch<Task>(`/tasks/${id}/complete`);
    return response.data;
  },

  update: async (id: number, data: TaskFormData): Promise<Task> => {
    const response = await api.patch<Task>(`/tasks/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/tasks/${id}`);
  },
};

export default api;
