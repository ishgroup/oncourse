import axios from "axios";
import {DynamicConfig} from "../../config/DynamicConfig";
import *  as Constants from "../api/Constants";

const instance = axios.create();
instance.defaults.baseURL = Constants.CONTEXT;
instance.defaults.headers.common[Constants.HEADER_XORIGIN] = DynamicConfig.getOrigin();

export const defaultAxios = instance;
