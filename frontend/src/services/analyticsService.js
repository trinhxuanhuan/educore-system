import api from './api';

export const analyticsService = {
  getMyAnalytics: () => api.get('/analytics/me'),
};
