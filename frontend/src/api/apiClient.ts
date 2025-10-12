import axios from 'axios';

const apiClient = axios.create({
    baseURL: process.env.REACT_APP_API_BASE_URL || '',
    withCredentials: true, // 세션 쿠키 사용을 위해 필수
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // 401 에러 발생 시 로그인 페이지로 리다이렉트
            // 상태를 초기화하기 위해 페이지를 새로고침하는 것이 좋습니다.
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export { apiClient };
