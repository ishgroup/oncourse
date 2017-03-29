import axios from "axios";
import {DynamicConfig} from "../config/DynamicConfig";

const instance = axios.create();
instance.defaults.baseURL = "/api/";
instance.defaults.headers.common["X-Origin"] = DynamicConfig.getOrigin();

export const defaultAxios = instance;
