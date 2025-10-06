import React from 'react';
import Layout from '../components/layout/Layout';
import Button from '../components/common/Button';
import GoogleIcon from '../assets/google-icon.svg';
import logo from '../assets/logo.png';
import './LoginPage.css';

const LoginPage: React.FC = () => {
    const handleLogin = () => {
        if (process.env.REACT_APP_API_BASE_URL) {
            window.location.href = `${process.env.REACT_APP_API_BASE_URL}/oauth2/authorization/google`;
        }
    };

    return (
        <Layout>
            <div className="login-page-content">
                <img src={logo} alt="mywiki logo" className="login-logo" />
                <h2>mywiki에 오신 것을 환영합니다!</h2>
                <p>오늘은 어떤 지식을 쌓아볼까요?</p>
                <Button onClick={handleLogin} className="google-login-btn">
                    <img src={GoogleIcon} alt="Google icon" className="google-icon" />
                    <span>구글 계정으로 시작</span>
                </Button>
            </div>
        </Layout>
    );
};

export default LoginPage;
