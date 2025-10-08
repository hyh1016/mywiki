import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import Layout from '../components/layout/Layout';
import {apiClient} from '../api/apiClient';
import './SummaryListPage.css';

interface Summary {
    id: number;
    content: string;
    updatedAt: string;
    bookmark: {
        id: number;
        url: string;
        title: string;
        description: string;
        image: string;
    };
}

const SummaryListPage: React.FC = () => {
    const [summaries, setSummaries] = useState<Summary[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchSummaries = async () => {
            try {
                const response = await apiClient.get<{ summaries: Summary[] }>('/api/summaries');
                setSummaries(response.data.summaries);
            } catch (err) {
                setError('요약글 목록을 불러오는데 실패했습니다.');
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchSummaries();
    }, []);

    return (
        <Layout title="요약글 목록">
            <div className="summary-list-page-content">
                {isLoading && <p>로딩 중...</p>}
                {error && <p className="error-message">{error}</p>}
                {!isLoading && !error && (
                    <div className="summaries-container">
                        {summaries.length > 0 ? (
                            summaries.map(summary => (
                                <Link key={summary.id} to={`/summaries/${summary.id}`} className="summary-card-link">
                                    <div className="summary-card">
                                        <div className="summary-card-image-wrapper">
                                            {summary.bookmark?.image && <img src={summary.bookmark.image} alt={summary.bookmark.title} className="summary-card-image" />}
                                        </div>
                                        <div className="summary-card-content">
                                            <h3 className="summary-card-title">{summary.bookmark.title} 요약</h3>
                                            <p className="summary-card-source">{summary.content}</p>
                                            <p className="summary-card-date">{new Date(summary.updatedAt).toLocaleDateString()}</p>
                                        </div>
                                    </div>
                                </Link>
                            ))
                        ) : (
                            <p>작성된 요약글이 없습니다.</p>
                        )}
                    </div>
                )}
            </div>
        </Layout>
    );
};

export default SummaryListPage;
