import axiosClient from './axiosClient';
import type { LoginRequest, LoginResponse } from '../types/Auth';

export const authApi = {
  login: (data: LoginRequest) =>
    axiosClient.post<LoginResponse>('/auth/login', data),
};

