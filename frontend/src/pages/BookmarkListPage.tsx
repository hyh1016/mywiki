import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import Layout from '../components/layout/Layout';
import {apiClient} from '../api/apiClient';
import './BookmarkListPage.css';

interface Bookmark {
    id: number;
    url: string;
    title: string;
    description: string;
    image: string;
}

const BookmarkListPage: React.FC = () => {
    const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchBookmarks = async () => {
            try {
                const response = await apiClient.get<{ bookmarks: Bookmark[] }>('/api/bookmarks');
                setBookmarks(response.data.bookmarks);
            } catch (err) {
                setError('북마크 목록을 불러오는데 실패했습니다.');
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchBookmarks();
    }, []);

    return (
        <Layout title="북마크 목록">
            <div className="bookmark-list-page-content">
                {isLoading && <p>로딩 중...</p>}
                {error && <p className="error-message">{error}</p>}
                {!isLoading && !error && (
                    <div className="bookmarks-container">
                        {bookmarks.length > 0 ? (
                            bookmarks.map(bookmark => (
                                <Link key={bookmark.id} to={`/bookmarks/${bookmark.id}`} state={{ bookmark }} className="bookmark-card-link">
                                    <div className="bookmark-card">
                                        <div className="bookmark-card-image-wrapper">
                                            {bookmark.image && <img src={bookmark.image} alt={bookmark.title} className="bookmark-card-image" />}
                                        </div>
                                        <div className="bookmark-card-content">
                                            <h3 className="bookmark-card-title">{bookmark.title}</h3>
                                            <p className="bookmark-card-description">{bookmark.description}</p>
                                        </div>
                                    </div>
                                </Link>
                            ))
                        ) : (
                            <p>등록된 북마크가 없습니다.</p>
                        )}
                    </div>
                )}
            </div>
        </Layout>
    );
};

export default BookmarkListPage;
