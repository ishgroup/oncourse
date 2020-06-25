import axios from "axios";
import {CONTEXT} from "../Constants";

const instance = axios.create();
instance.defaults.baseURL = CONTEXT;

export const defaultAxios = instance;
