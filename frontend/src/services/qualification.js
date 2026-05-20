import api from './api';

export const qualification = {
  getByPilotId: (pilotId) => api.get(`/employees/${pilotId}/qualifications`).then(res => res.data),
  create: (pilotId, data) => api.post(`/employees/${pilotId}/qualifications`, data).then(res => res.data),
  update: (pilotId, qualificationId, data) => api.put(`/employees/${pilotId}/qualifications/${qualificationId}`, data).then(res => res.data),
};