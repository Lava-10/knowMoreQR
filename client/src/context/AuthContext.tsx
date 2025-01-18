import React, { createContext, useState, useContext, useEffect, ReactNode } from 'react';
import axios from 'axios';

interface AuthState {
    token: string | null;
    userId: string | null;
    userEmail: string | null;
    userType: 'consumer' | 'company' | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}

interface AuthContextType extends AuthState {
    login: (data: { token: string; id: string; email: string; userType: 'consumer' | 'company' }) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [authState, setAuthState] = useState<AuthState>({
        token: localStorage.getItem('token'),
        userId: localStorage.getItem('userId'),
        userEmail: localStorage.getItem('userEmail'),
        userType: localStorage.getItem('userType') as 'consumer' | 'company' | null,
        isAuthenticated: !!localStorage.getItem('token'), // Check if token exists
        isLoading: true, // Start as loading to check token validity or initial state
    });

    // Configure axios default Authorization header
    useEffect(() => {
        if (authState.token) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${authState.token}`;
            // Optionally add a check here to validate token with backend if needed on load
            // For now, assume token in localStorage is valid until an API call fails
             setAuthState((prev: AuthState) => ({ ...prev, isLoading: false }));
        } else {
            delete axios.defaults.headers.common['Authorization'];
             setAuthState((prev: AuthState) => ({ ...prev, isLoading: false }));
        }
    }, [authState.token]);

    const login = (data: { token: string; id: string; email: string; userType: 'consumer' | 'company' }) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('userId', data.id);
        localStorage.setItem('userEmail', data.email);
        localStorage.setItem('userType', data.userType);
        axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
        setAuthState({
            token: data.token,
            userId: data.id,
            userEmail: data.email,
            userType: data.userType,
            isAuthenticated: true,
            isLoading: false,
        });
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userType');
        localStorage.removeItem('isLoggedIn'); // Remove old flag if present
        delete axios.defaults.headers.common['Authorization'];
        setAuthState({
            token: null,
            userId: null,
            userEmail: null,
            userType: null,
            isAuthenticated: false,
            isLoading: false,
        });
        // Optionally redirect to login page here or let components handle it
        // window.location.href = '/login'; 
    };

    return (
        <AuthContext.Provider value={{ ...authState, login, logout }}>
            {!authState.isLoading ? children : <div>Loading...</div>} {/* Optionally show loading indicator */}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}; 