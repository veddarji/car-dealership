import axiosInstance from './axiosInstance';

export const getAllVehicles = (params) =>
  axiosInstance.get('/api/vehicles', { params });

export const getVehicleById = (id) =>
  axiosInstance.get(`/api/vehicles/${id}`);

export const searchVehicles = (params) =>
  axiosInstance.get('/api/vehicles/search', { params });

export const createVehicle = (data) =>
  axiosInstance.post('/api/vehicles', data);

export const updateVehicle = (id, data) =>
  axiosInstance.put(`/api/vehicles/${id}`, data);

export const deleteVehicle = (id) =>
  axiosInstance.delete(`/api/vehicles/${id}`);

export const purchaseVehicle = (id, quantity) =>
  axiosInstance.post(`/api/vehicles/${id}/purchase`, { quantity });

export const restockVehicle = (id, quantity) =>
  axiosInstance.post(`/api/vehicles/${id}/restock`, { quantity });
