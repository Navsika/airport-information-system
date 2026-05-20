// src/services/scheduleService.js
import api from './api';

export const schedule = {
  searchByCities: (departureCity, arrivalCity) => api.get('/schedules/search', { params: { departureCity, arrivalCity } }).then(res => res.data),
  create: (data) => api.post('/schedules', data).then(res => res.data),
  getByFlightNumber: (flightNumber) => api.get(`/schedules/search/${flightNumber}`).then(res => res.data),
};