import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {apiClient} from '../api/apiClient';
import {Bookmark} from '../types/bookmark';

export const useRandomBookmark = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchAndNavigate = async () => {
        if (isLoading) return;
        setIsLoading(true);
        setError(null);
        try {
            const response = await apiClient.get<Bookmark>('/api/bookmarks/random');
            const randomBookmark = response.data;
            navigate(`/bookmarks/${randomBookmark.id}`, { state: { bookmark: randomBookmark } });
        } catch (err: any) {
            let errorMessage = '랜덤 북마크를 가져오는 중 오류가 발생했습니다.';
            if (err.response && (err.response.status === 404 || err.response.status === 500)) {
                errorMessage = '등록된 북마크가 없습니다. 먼저 북마크를 추가해주세요.';
            }
            setError(errorMessage);
            alert(errorMessage);
            console.error('Error fetching random bookmark:', err);
        } finally {
            setIsLoading(false);
        }
    };

    return { fetchAndNavigate, isLoading, error };
};
