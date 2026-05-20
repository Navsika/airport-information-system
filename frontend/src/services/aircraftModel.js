import api from './api';

export const aircraftModel = {
  getAll: (params) => {
    const { modelName, 
        manufacturer, 
        passengerCapacity, 
        cargoCapacity, 
        maxSpeed, 
        page = 0, 
        size = 20 } = params || {};
    return api.get('/models', { 
        params: { modelName, 
            manufacturer, 
            passengerCapacity, 
            cargoCapacity, 
            maxSpeed, 
            page, size } }).then(res => res.data);
  },
  getById: (id) => api.get(`/models/${id}`).then(res => res.data),
  getList: () => api.get('/models/list').then(res => res.data),
  create: (data) => api.post('/models', data).then(res => res.data),
};