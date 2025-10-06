import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import apiClient from '../api/apiClient';

interface AuthContextType {
    isAuthenticated: boolean;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                // 백엔드에 로그인 상태를 확인할 수 있는 API 요청
                // 성공 시(2xx), 사용자는 로그인 상태임
                await apiClient.get('/api/user/me');
                setIsAuthenticated(true);
            } catch (error) {
                // 실패 시(401 등), 사용자는 로그인 상태가 아님
                setIsAuthenticated(false);
            } finally {
                setIsLoading(false);
            }
        };

        checkAuthStatus();
    }, []);

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
