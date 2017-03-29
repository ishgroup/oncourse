import axios from "axios";

const instance = axios.create();
instance.defaults.baseURL = "/api/";
instance.defaults.headers.common["X-Origin"] = location.host;

export const defaultAxios = instance;
