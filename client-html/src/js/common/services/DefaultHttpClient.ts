import axios from "axios";
import * as Constants from "../api/Constants";

const instance = axios.create();
instance.defaults.baseURL = Constants.CONTEXT;

export const defaultAxios = instance;
