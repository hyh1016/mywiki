import React from 'react';
import {useNavigate} from 'react-router-dom';
import Layout from '../components/layout/Layout';
import Button from '../components/common/Button';
import logo from '../assets/logo.png';
import './MainPage.css';

const MainPage: React.FC = () => {
    const navigate = useNavigate();

    const handleNavigate = (path: string) => {
        navigate(path);
    };

    return (
        <Layout>
            <div className="main-page-content">
                <img src={logo} alt="mywiki logo" className="main-logo" />
                <h2>mywiki에 오신 것을 환영합니다!</h2>
                <p>오늘은 어떤 지식을 쌓아볼까요?</p>
                <div className="main-page-buttons">
                    <Button onClick={() => handleNavigate('/add-bookmark')} className="main-page-btn">
                        <span>북마크 등록하기</span>
                    </Button>
                    <Button onClick={() => handleNavigate('/bookmarks')} className="main-page-btn">
                        <span>북마크 목록 보기</span>
                    </Button>
                    <Button onClick={() => handleNavigate('/summaries')} className="main-page-btn">
                        <span>요약글 목록 보기</span>
                    </Button>
                    <Button onClick={() => handleNavigate('/random')} className="main-page-btn">
                        <span>랜덤 글 읽기</span>
                    </Button>
                </div>
            </div>
        </Layout>
    );
};

export default MainPage;
