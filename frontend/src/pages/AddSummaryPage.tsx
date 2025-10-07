import React from 'react';
import Layout from '../components/layout/Layout';
import './AddSummaryPage.css';

const AddSummaryPage: React.FC = () => {
    return (
        <Layout title="요약 작성하기">
            <div className="add-summary-page-content">
                <h2>요약 작성하기</h2>
                <p>이곳에서 북마크한 글의 내용을 요약합니다.</p>
                {/* 요약 작성 폼이 여기에 추가될 예정입니다. */}
            </div>
        </Layout>
    );
};

export default AddSummaryPage;
