import api from './api';

export const ticket = {
  getByFlightId: (flightId) => api.get(`/flights/${flightId}/tickets`).then(res => res.data),
  sell: (flightId, data) => api.post(`/flights/${flightId}/tickets`, data).then(res => res.data),
};