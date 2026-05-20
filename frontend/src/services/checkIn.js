// src/services/checkInService.js
import api from './api';

export const checkIn = {
  create: (ticketId, data) => api.post(`/tickets/${ticketId}/check-in`, data).then(res => res.data),
  getByTicketId: (ticketId) => api.get(`/tickets/${ticketId}/check-in`).then(res => res.data),
  getByFlightId: (flightId) => api.get(`/flights/${flightId}/check-ins`).then(res => res.data),
  cancel: (ticketId) => api.delete(`/tickets/${ticketId}/check-in`).then(res => res.data),
};