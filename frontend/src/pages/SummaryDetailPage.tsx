import React from 'react';
import {useParams} from 'react-router-dom';
import Layout from '../components/layout/Layout';

// TODO: Implement summary detail page
const SummaryDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();

    return (
        <Layout title="요약 상세">
            <div style={{ padding: '24px' }}>
                <h2>요약 상세 정보</h2>
                <p>요약 ID: {id}</p>
                <p>상세 페이지는 곧 구현될 예정입니다.</p>
            </div>
        </Layout>
    );
};

export default SummaryDetailPage;
