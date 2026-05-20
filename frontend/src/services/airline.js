import api from './api';

export const airline = {
  getAll: (params) => {
    const { iataCode, 
        airlineName, 
        country, 
        page = 0, size = 20 } = params || {};
    return api.get('/airlines', { 
        params: { iataCode, 
            airlineName, 
            country, 
            page, size } }).then(res => res.data);
  },
  getList: () => api.get('/airlines/list').then(res => res.data),
  getById: (id) => api.get(`/airlines/${id}`).then(res => res.data),
  create: (data) => api.post('/airlines', data).then(res => res.data),
  update: (id, data) => api.put(`/airlines/${id}`, data).then(res => res.data),
};