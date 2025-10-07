import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import Layout from '../components/layout/Layout';
import Button from '../components/common/Button';
import {apiClient} from '../api/apiClient';
import './BookmarkDetailPage.css';

interface Bookmark {
    id: number;
    url: string;
    title: string;
    description: string;
    image: string;
}

const BookmarkDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { state } = useLocation();

    const [bookmark, setBookmark] = useState<Bookmark | null>(state?.bookmark || null);
    const [isLoading, setIsLoading] = useState(!state?.bookmark);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchBookmark = async () => {
            try {
                const response = await apiClient.get<Bookmark>(`/api/bookmarks/${id}`);
                setBookmark(response.data);
            } catch (err) {
                setError('북마크 정보를 불러오는데 실패했습니다.');
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        if (!bookmark && id) {
            fetchBookmark();
        }
    }, [id, bookmark]);

    const handleReadClick = () => {
        if (bookmark) {
            window.open(bookmark.url, '_blank', 'noopener,noreferrer');
        }
    };

    const handleWriteSummaryClick = () => {
        navigate(`/summaries/new?bookmarkId=${id}`);
    };

    if (isLoading) {
        return <Layout title="로딩 중..."><div className="bookmark-detail-page-content"><p>로딩 중...</p></div></Layout>;
    }

    if (error) {
        return <Layout title="오류"><div className="bookmark-detail-page-content"><p className="error-message">{error}</p></div></Layout>;
    }

    if (!bookmark) {
        return <Layout title="북마크 없음"><div className="bookmark-detail-page-content"><p>해당 북마크를 찾을 수 없습니다.</p></div></Layout>;
    }

    return (
        <Layout title="북마크 상세">
            <div className="bookmark-detail-page-content">
                <div className="bookmark-info-card">
                    {bookmark.image &&
                        <img src={bookmark.image} alt={bookmark.title} className="bookmark-image"/>
                    }
                    <div className="bookmark-text-content">
                        <h2 className="bookmark-title">{bookmark.title}</h2>
                        <p className="bookmark-description">{bookmark.description}</p>
                    </div>
                </div>
                <div className="detail-page-buttons">
                    <Button onClick={handleReadClick} className="detail-page-btn">
                        글 읽기
                    </Button>
                    <Button onClick={handleWriteSummaryClick} className="detail-page-btn">
                        요약 작성하기
                    </Button>
                </div>
            </div>
        </Layout>
    );
};

export default BookmarkDetailPage;
