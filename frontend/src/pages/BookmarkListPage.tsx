import React, {useCallback, useMemo, useRef, useState} from 'react';
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
    readAt: string | null;
}

interface BookmarkCursorResponse {
    content: Bookmark[];
    nextCursor: number | null;
}

type FilterMode = 'all' | 'unread' | 'read';

const calculateDaysAgo = (dateString: string): string => {
    const now = new Date();
    const readDate = new Date(dateString);

    const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const startOfReadDate = new Date(readDate.getFullYear(), readDate.getMonth(), readDate.getDate());

    const diffTime = startOfToday.getTime() - startOfReadDate.getTime();
    const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) {
        return '오늘 읽음';
    } else if (diffDays === 1) {
        return '어제 읽음';
    } else {
        return `${diffDays}일 전 읽음`;
    }
};

const BookmarkListPage: React.FC = () => {
    const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [cursor, setCursor] = useState<number | null>(null);
    const [hasMore, setHasMore] = useState(true);
    const [filter, setFilter] = useState<FilterMode>('all');

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

    const handleDeleteBookmark = async (e: React.MouseEvent, bookmarkId: number) => {
        e.preventDefault();
        e.stopPropagation();

        if (!window.confirm('북마크를 삭제하시겠습니까?')) {
            return;
        }

        const deleteBookmarkAction = () => {
            apiClient.delete(`/api/bookmarks/${bookmarkId}`)
                .then(() => {
                    setBookmarks(prevBookmarks => prevBookmarks.filter(b => b.id !== bookmarkId));
                })
                .catch(err => {
                    alert('북마크 삭제에 실패했습니다.');
                    console.error(err);
                });
        };

        try {
            // Summary 존재 여부 확인
            await apiClient.get(`/api/summaries/exists?bookmarkId=${bookmarkId}`);

            // 존재하면 바로 제거하는 대신 확인하고 삭제
            if (window.confirm('이 북마크에 작성된 요약이 있습니다. 그래도 삭제하시겠습니까?')) {
                deleteBookmarkAction();
            }
        } catch (error: any) {
            if (error.response && error.response.status === 404) {
                deleteBookmarkAction();
            } else {
                console.error('요약 존재 여부 확인 중 오류 발생:', error);
                alert('북마크 삭제에 실패했습니다.');
            }
        }
    };
    const filteredBookmarks = useMemo(() => {
        switch (filter) {
            case 'read':
                return bookmarks.filter(b => b.readAt);
            case 'unread':
                return bookmarks.filter(b => !b.readAt);
            case 'all':
            default:
                return bookmarks;
        }
    }, [bookmarks, filter]);


    return (
        <Layout title="북마크 목록">
            <div className="bookmark-list-page-content">
                <div className="filter-buttons">
                    <button onClick={() => setFilter('all')} className={filter === 'all' ? 'active' : ''}>모두</button>
                    <button onClick={() => setFilter('unread')} className={filter === 'unread' ? 'active' : ''}>읽지 않은 것</button>
                    <button onClick={() => setFilter('read')} className={filter === 'read' ? 'active' : ''}>읽은 것</button>
                </div>

                {error && <p className="error-message">{error}</p>}
                <div className="bookmarks-container">
                    {filteredBookmarks.map((bookmark) => (
                        <div key={bookmark.id} className="bookmark-card-wrapper">
                            <Link
                                to={`/bookmarks/${bookmark.id}`}
                                state={{bookmark}}
                                className="bookmark-card-link"
                            >
                                <div className={`bookmark-card ${bookmark.readAt ? 'read' : ''}`}>
                                    <div className="bookmark-card-image-wrapper">
                                        {bookmark.image && <img src={bookmark.image} alt={bookmark.title}
                                                                className="bookmark-card-image"/>}
                                    </div>
                                    <div className="bookmark-card-content">
                                        <h3 className="bookmark-card-title">{bookmark.title}</h3>
                                        <p className="bookmark-card-description">{bookmark.description}</p>
                                        {bookmark.readAt && (
                                            <p className="bookmark-read-status">
                                                {calculateDaysAgo(bookmark.readAt)}
                                            </p>
                                        )}
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
                {!isLoading && filteredBookmarks.length === 0 && (
                    <p>
                        {filter === 'all' && '등록된 북마크가 없습니다.'}
                        {filter === 'unread' && '읽지 않은 북마크가 없습니다.'}
                        {filter === 'read' && '읽은 북마크가 없습니다.'}
                    </p>
                )}
            </div>
        </Layout>
    );
};

export default BookmarkListPage;
