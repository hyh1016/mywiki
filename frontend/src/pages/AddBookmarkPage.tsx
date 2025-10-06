import React, {useState} from 'react';
import Layout from '../components/layout/Layout';
import Button from '../components/common/Button';
import './AddBookmarkPage.css';
import {apiClient} from '../api/apiClient';

interface BookmarkResponse {
    id: number;
    url: string;
    title: string;
    description: string;
    image: string;
}

const AddBookmarkPage: React.FC = () => {
    const [url, setUrl] = useState('');
    const [message, setMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [bookmarkInfo, setBookmarkInfo] = useState<BookmarkResponse | null>(null);

    const handleUrlChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUrl(e.target.value);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setBookmarkInfo(null);

        if (!url) {
            setMessage('URL을 입력해주세요.');
            return;
        }
        setIsLoading(true);
        setMessage('');

        try {
            const response = await apiClient.post<BookmarkResponse>('/api/bookmarks', { url });
            setMessage('북마크가 성공적으로 등록되었습니다!');
            setBookmarkInfo(response.data);
        } catch (error) {
            console.error("Error adding bookmark:", error);
            setMessage('북마크 등록에 실패했습니다. 다시 시도해주세요.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Layout title="북마크 등록하기">
            <div className="add-bookmark-page-content">
                <div className="add-bookmark-container">
                    <h2>북마크 등록하기</h2>
                    <p>저장하고 싶은 URL을 입력해 북마크를 등록하세요.</p>
                    <form onSubmit={handleSubmit} className="add-bookmark-form">
                        <input
                            type="url"
                            value={url}
                            onChange={handleUrlChange}
                            placeholder="https://example.com"
                            className="url-input"
                            disabled={isLoading}
                        />
                        <Button type="submit" disabled={isLoading} className="add-bookmark-btn">
                            {isLoading ? '등록 중...' : '등록하기'}
                        </Button>
                    </form>
                    {message && <p className="message">{message}</p>}

                    {bookmarkInfo && (
                        <div className="bookmark-info-card">
                            {bookmarkInfo.image &&
                                <img src={bookmarkInfo.image} alt={bookmarkInfo.title} className="bookmark-image"/>
                            }
                            <div className="bookmark-text-content">
                                <h3 className="bookmark-title">{bookmarkInfo.title}</h3>
                                <p className="bookmark-description">{bookmarkInfo.description}</p>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </Layout>
    );
};

export default AddBookmarkPage;
