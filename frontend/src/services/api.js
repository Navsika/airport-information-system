import axios from 'axios'

const api = axios.create({
    baseURL:"http://localhost:8080/airport-info-system/api",
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.response.use(
    (response) => {
        if (response.data && typeof response.data === 'object' && 'success' in response.data) {
            if (!response.data.success) {
                const message = response.data.error?.message || 'Ошибка сервера';
                return Promise.reject(new Error(message));
            }
            response.data = response.data.data;
        }
        return response;
    },
    (error) => {
        const message = error.response?.data?.error?.message || error.message || 'Ошибка сети';
        return Promise.reject(new Error(message));
    }
);

export default api;
