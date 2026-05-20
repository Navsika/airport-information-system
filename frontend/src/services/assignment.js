import api from './api';

export const assignmentService = {
  getByFlightId: (flightId) => api.get(`/flights/${flightId}/crew`).then(res => res.data),
  assign: (flightId, data) => api.post(`/flights/${flightId}/crew`, data).then(res => res.data),
  remove: (flightId, assignmentId) => api.delete(`/flights/${flightId}/crew/${assignmentId}`).then(res => res.data),
};