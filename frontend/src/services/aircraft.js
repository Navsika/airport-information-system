import api from './api';

export const aircraft = {
  getAll: (params) => {
    const { modelId, 
        airlineId, 
        manufactureYear, 
        lastMaintenanceDate, 
        flightHours, 
        page = 0, size = 20 } = params || {};
    return api.get('/aircrafts', { 
        params: { modelId, 
            airlineId, 
            manufactureYear, 
            lastMaintenanceDate, 
            flightHours, 
            page, size } }).then(res => res.data);
  },
  getList: () => api.get('/aircrafts/list').then(res => res.data),
  getById: (id) => api.get(`/aircrafts/${id}`).then(res => res.data),
  getByRegistrationNumber: (registrationNumber) => api.get(`/aircrafts/registration/${registrationNumber}`).then(res => res.data),
  create: (data) => api.post('/aircrafts', data).then(res => res.data),
  update: (id, data) => api.put(`/aircrafts/${id}`, data).then(res => res.data),
};