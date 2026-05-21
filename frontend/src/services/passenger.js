import api from './api';

export const passenger = {
  getAll: (params) => {
    const { lastName, firstName } = params || {};
    return api.get('/passengers/list').then(res => {
      const normalizedLastName = (lastName || '').trim().toLowerCase();
      const normalizedFirstName = (firstName || '').trim().toLowerCase();
      return res.data.filter((item) => {
        const matchesLastName = !normalizedLastName || String(item.lastName || '').toLowerCase().startsWith(normalizedLastName);
        const matchesFirstName = !normalizedFirstName || String(item.firstName || '').toLowerCase().startsWith(normalizedFirstName);
        return matchesLastName && matchesFirstName;
      });
    });
  },
  getList: () => api.get('/passengers/list').then(res => res.data),
  create: (data) => api.post('/passengers', data).then(res => res.data),
  getById: (id) => api.get(`/passengers/${id}`).then(res => res.data),
};
