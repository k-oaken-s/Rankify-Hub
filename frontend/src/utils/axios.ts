import axios from "axios";

import { getApiBaseUrl } from "./getApiBaseUrl";

console.log("Axios instance being created");

const api = axios.create({
  baseURL: getApiBaseUrl(),
  withCredentials: true,
});

console.log("Setting up interceptor");

// リクエストインターセプターでトークンを確実に付与
api.interceptors.request.use((config) => {
  console.log("Cookies:", document.cookie);
  const token = document.cookie
    .split("; ")
    .find((row) => row.startsWith("admin_token="))
    ?.split("=")[1];

  console.log("Found token:", token);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // FormDataの場合、Content-Typeはブラウザが自動設定するため削除
  if (config.data instanceof FormData) {
    delete config.headers["Content-Type"];
  }

  return config;
});

export default api;
