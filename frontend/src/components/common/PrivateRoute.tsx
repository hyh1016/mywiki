import React, {JSX} from 'react';
import {Navigate} from 'react-router-dom';
import {useAuth} from '../../contexts/AuthContext';

const PrivateRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        // 인증 상태를 확인하는 동안 로딩 인디케이터를 보여줍니다.
        return <div>Loading...</div>;
    }

    return isAuthenticated ? children : <Navigate to="/login" />;
};

export default PrivateRoute;
