import axios from "axios";
import * as Constants from "../constants";

const instance = axios.create();
instance.defaults.baseURL = Constants.CONTEXT;

(window as any).setBaseURL = (url) => {
  instance.defaults.baseURL = url;
}

export const defaultAxios = instance;
