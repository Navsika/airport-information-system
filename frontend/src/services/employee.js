import api from './api';

export const employee = {
  getAll: (params) => {
    const { 
        category, 
        lastName, 
        hireDateFrom, 
        hireDateTo, 
        page = 0, size = 20 } = params || {};
    return api.get('/employees', { 
        params: { category, 
            lastName, 
            hireDateFrom, 
            hireDateTo, 
            page, size } }).then(res => res.data);
  },
  getList: () => api.get('/employees/list').then(res => res.data),
  getById: (id) => api.get(`/employees/${id}`).then(res => res.data),
  create: (data) => api.post('/employees', data).then(res => res.data),
  update: (id, data) => api.put(`/employees/${id}`, data).then(res => res.data),
};
