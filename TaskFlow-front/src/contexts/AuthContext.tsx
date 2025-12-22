import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { authApi } from '@/services/api';
import type { AuthContextType } from '@/types';
import { toast } from 'sonner';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<{ email: string; name: string } | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check for existing auth on mount
    const storedToken = localStorage.getItem('taskflow_token');
    const storedUser = localStorage.getItem('taskflow_user');

    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
    }
    setIsLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const response = await authApi.login({ email, password });
      
      const userData = { email: response.email, name: response.name };
      
      localStorage.setItem('taskflow_token', response.token);
      localStorage.setItem('taskflow_user', JSON.stringify(userData));
      
      setToken(response.token);
      setUser(userData);
      
      toast.success('Login successful!');
    } catch (error: any) {
      const message = error.response?.data?.message || 'Invalid email or password';
      toast.error(message);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('taskflow_token');
    localStorage.removeItem('taskflow_user');
    setToken(null);
    setUser(null);
    toast.success('Logged out successfully');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
