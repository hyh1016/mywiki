import React, {useCallback, useRef, useState} from 'react';
import {Link} from 'react-router-dom';
import Layout from '../components/layout/Layout';
import {apiClient} from '../api/apiClient';
import TrashIcon from '../components/common/TrashIcon';
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

    const handleDeleteBookmark = (e: React.MouseEvent, bookmarkId: number) => {
        e.preventDefault();
        e.stopPropagation();

        if (window.confirm('북마크를 삭제하시겠습니까?')) {
            apiClient.delete(`/api/bookmarks/${bookmarkId}`)
                .then(() => {
                    setBookmarks(prevBookmarks => prevBookmarks.filter(b => b.id !== bookmarkId));
                })
                .catch(err => {
                    alert('북마크 삭제에 실패했습니다.');
                    console.error(err);
                });
        }
    };


    return (
        <Layout title="북마크 목록">
            <div className="bookmark-list-page-content">
                {error && <p className="error-message">{error}</p>}
                <div className="bookmarks-container">
                    {bookmarks.map((bookmark) => (
                        <div key={bookmark.id} className="bookmark-card-wrapper">
                            <Link
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
                            <button
                                className="bookmark-delete-button"
                                onClick={(e) => handleDeleteBookmark(e, bookmark.id)}
                                aria-label="북마크 삭제"
                            >
                                <TrashIcon/>
                            </button>
                        </div>
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
