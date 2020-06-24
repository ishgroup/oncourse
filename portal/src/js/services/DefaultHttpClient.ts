import axios from "axios";

const instance = axios.create();
instance.defaults.baseURL = "/a/";

export const defaultAxios = instance;
