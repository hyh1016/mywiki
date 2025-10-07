import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import {apiClient} from '../api/apiClient';
import {useLocation} from "react-router-dom";

interface AuthContextType {
    isAuthenticated: boolean;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const location = useLocation();

    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                await apiClient.get('/api/user/me');
                setIsAuthenticated(true);
            } catch (error) {
                setIsAuthenticated(false);
            } finally {
                setIsLoading(false);
            }
        };

        if (location.pathname === '/login') {
            setIsAuthenticated(false);
            setIsLoading(false);
        } else {
            checkAuthStatus();
        }
    }, [location.pathname]);

    return (
        <AuthContext.Provider value={{ isAuthenticated, isLoading }}>
            {children}
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
