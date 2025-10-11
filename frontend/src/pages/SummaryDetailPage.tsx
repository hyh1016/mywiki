import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {apiClient} from '../api/apiClient';
import './SummaryDetailPage.css';
import Layout from "../components/layout/Layout";
import Button from "../components/common/Button";

// --- API Data Types ---
interface BookmarkResponse {
    id: number;
    url: string;
    title: string;
    description: string | null;
    image: string | null;
    createdAt: string;
}

interface SummaryDetailContentItem {
    id: number;
    title: string;
    description: string | null;
    content: string;
}

interface SummaryDetailSection {
    title: string;
    content: SummaryDetailContentItem[];
}

type SummaryDetailResponse = Record<string, SummaryDetailSection>;

interface SummaryResponse {
    id: number;
    bookmark: BookmarkResponse;
    contents: SummaryDetailResponse;
    createdAt: string;
    updatedAt: string;
}

const SummaryDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [summaryData, setSummaryData] = useState<SummaryResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchSummary = async () => {
            try {
                const response = await apiClient.get<SummaryResponse>(`/api/summaries/${id}`);
                setSummaryData(response.data);
            } catch (err) {
                setError('요약 정보를 불러오는데 실패했습니다.');
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        if (id) {
            fetchSummary();
        }
    }, [id]);

    const handleReadOriginal = () => {
        if (summaryData?.bookmark.url) {
            window.open(summaryData.bookmark.url, '_blank', 'noopener,noreferrer');
        }
    };

    const handleEdit = () => {
        navigate(`/summaries/${id}/edit`);
    };

    if (isLoading) {
        return <Layout title="로딩 중..."><div className="summary-detail-page-content"><p>로딩 중...</p></div></Layout>;
    }

    if (error) {
        return <Layout title="오류"><div className="summary-detail-page-content"><p className="error-message">{error}</p></div></Layout>;
    }

    if (!summaryData) {
        return <Layout title="요약 없음"><div className="summary-detail-page-content"><p>해당 요약을 찾을 수 없습니다.</p></div></Layout>;
    }

    return (
        <Layout title="요약 상세">
            <div className="summary-detail-page-content">
                <h1 className="summary-main-title">{summaryData.bookmark.title}의 요약</h1>

                {Object.entries(summaryData.contents).map(([sectionKey, sectionData]) => (
                    <section key={sectionKey} className="summary-detail-section">
                        <h2 className="section-title">{sectionData.title}</h2>
                        {sectionData.content.map(item => (
                            <div key={item.id} className="summary-item-card">
                                <h3 className="item-title">{item.title}</h3>
                                {item.description && <p className="item-description">{item.description}</p>}
                                <div className="item-content">
                                    {item.content}
                                </div>
                            </div>
                        ))}
                    </section>
                ))}

                <div className="detail-page-buttons">
                    <Button onClick={handleReadOriginal} className="detail-page-btn">
                        원문 읽기
                    </Button>
                    <Button onClick={handleEdit}>
                        수정하기
                    </Button>
                </div>
            </div>
        </Layout>
    );
};

export default SummaryDetailPage;