import axios from "axios";
import * as Constants from "../constants";

const instance = axios.create();
instance.defaults.baseURL = Constants.CONTEXT;

export const defaultAxios = instance;
