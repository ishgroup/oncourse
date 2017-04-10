import {AxiosRequestConfig} from "axios";
import {WrappedFieldInputProps} from "redux-form";

export interface ReactRouter {
  push: (string) => void;
}

export interface ReactRouterContext {
  router?: ReactRouter;
}

export interface RestError<T> {
  data: any;
  status: number;
  statusText: string;
  headers: any;
  config: AxiosRequestConfig;
}
