import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('eduToken');
    if (!token) {
      setLoading(false);
      return;
    }

    authService
      .getCurrentUser()
      .then((response) => {
        setUser(response.data.data);
      })
      .catch(() => {
        localStorage.removeItem('eduToken');
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const login = async (credentials) => {
    const response = await authService.login(credentials);
    const authData = response.data.data;
    localStorage.setItem('eduToken', authData.accessToken);
    setUser(authData.user);
    return authData;
  };

  const logout = () => {
    localStorage.removeItem('eduToken');
    setUser(null);
  };

  const value = useMemo(
    () => ({ user, loading, login, logout, isAuthenticated: !!user }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
