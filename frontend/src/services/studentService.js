import api from './api';

export const studentService = {
  getMyProfile: () => api.get('/students/me'),
};
