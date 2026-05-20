// src/services/flightService.js
import api from './api';

export const flight = {
  getAll: (params) => {
    const { status, 
        dateFrom, 
        dateTo, 
        departureAirportId, 
        arrivalAirportId, 
        aircraftRegNumber,
        page = 0, size = 50 } = params || {};
    return api.get('/flights', { 
        params: { status, 
            dateFrom, 
            dateTo, 
            departureAirportId, 
            arrivalAirportId, 
            aircraftRegNumber,
            page, size } }).then(res => res.data);
  },
  getById: (id) => api.get(`/flights/${id}`).then(res => res.data),
  update: (id, data, reasonOfChange) => api.patch(`/flights/${id}`, data, { params: { reasonOfChange } }).then(res => res.data),
  updateStatus: (id, status, reasonOfChange) => api.patch(`/flights/${id}/status`, { status, reasonOfChange }).then(res => res.data),
  getPassengers: (id) => api.get(`/flights/${id}/passengers`).then(res => res.data),
  getStatusHistory: (id) => api.get(`/flights/${id}/status-history`).then(res => res.data),
  getStats: (id) => api.get(`/flights/${id}/stats`).then(res => res.data),
};
