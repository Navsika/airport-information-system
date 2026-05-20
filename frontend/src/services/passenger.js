import api from './api';

export const passenger = {
  getAll: (params) => {
    const { lastName, firstName } = params || {};
    return api.get('/passengers', { params: { lastName, firstName } }).then(res => res.data);
  },
  getList: () => api.get('/passengers/list').then(res => res.data),
  searchByPassport: (passportNumber) => api.get('/passengers/search', { params: { passportNumber } }).then(res => res.data),
  create: (data) => api.post('/passengers', data).then(res => res.data),
  getById: (id) => api.get(`/passengers/${id}`).then(res => res.data),
};
