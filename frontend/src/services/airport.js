import api from './api';

export const airport = {
  getAll: (params) => {
    const { country, 
        city, 
        iataCode, 
        page = 0, size = 20 } = params || {};
    return api.get('/airports', { 
        params: { country, 
            city, 
            iataCode, 
            page, size } }).then(res => res.data);
  },
  getList: () => api.get('/airports/list').then(res => res.data),
  getById: (id) => api.get(`/airports/${id}`).then(res => res.data),
  create: (data) => api.post('/airports', data).then(res => res.data),
  update: (id, data) => api.put(`/airports/${id}`, data).then(res => res.data),
};