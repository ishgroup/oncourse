import axios from "axios";

const instance = axios.create();
instance.defaults.baseURL = "/p/";

export const defaultAxios = instance;
