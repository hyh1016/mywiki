import React, {useCallback, useRef, useState} from 'react';
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

interface BookmarkCursorResponse {
    content: Bookmark[];
    nextCursor: number | null;
}

const BookmarkListPage: React.FC = () => {
    const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [cursor, setCursor] = useState<number | null>(null);
    const [hasMore, setHasMore] = useState(true);

    const observer = useRef<IntersectionObserver | null>(null);

    const fetchNextPage = useCallback(() => {
        if (isLoading || !hasMore) return;

        setIsLoading(true);
        setError(null);

        apiClient.get<BookmarkCursorResponse>('/api/bookmarks', {params: {cursor, size: 10}})
            .then(response => {
                const {content: newBookmarks, nextCursor} = response.data;
                setBookmarks(prev => [...prev, ...newBookmarks]);
                setCursor(nextCursor);
                setHasMore(nextCursor !== null);
            })
            .catch(err => {
                setError('북마크 목록을 불러오는데 실패했습니다.');
                console.error(err);
            })
            .finally(() => {
                setIsLoading(false);
            });
    }, [cursor, hasMore, isLoading]);

    const loaderRef = useCallback((node: HTMLDivElement | null) => {
        if (isLoading) return;
        if (observer.current) observer.current.disconnect();
        observer.current = new IntersectionObserver(entries => {
            if (entries[0].isIntersecting && hasMore) {
                fetchNextPage();
            }
        });
        if (node) observer.current.observe(node);
    }, [isLoading, hasMore, fetchNextPage]);




    return (
        <Layout title="북마크 목록">
            <div className="bookmark-list-page-content">
                {error && <p className="error-message">{error}</p>}
                <div className="bookmarks-container">
                    {bookmarks.map((bookmark) => (
                        <Link
                            key={bookmark.id}
                            to={`/bookmarks/${bookmark.id}`}
                            state={{bookmark}}
                            className="bookmark-card-link"
                        >
                            <div className="bookmark-card">
                                <div className="bookmark-card-image-wrapper">
                                    {bookmark.image && <img src={bookmark.image} alt={bookmark.title}
                                                            className="bookmark-card-image"/>}
                                </div>
                                <div className="bookmark-card-content">
                                    <h3 className="bookmark-card-title">{bookmark.title}</h3>
                                    <p className="bookmark-card-description">{bookmark.description}</p>
                                </div>
                            </div>
                        </Link>
                    ))}
                </div>

                {/* This element will trigger fetching more data */}
                <div ref={loaderRef}/>

                {isLoading && <p>로딩 중...</p>}
                {!isLoading && !hasMore && bookmarks.length === 0 && (
                    <p>등록된 북마크가 없습니다.</p>
                )}
            </div>
        </Layout>
    );
};

export default BookmarkListPage;
