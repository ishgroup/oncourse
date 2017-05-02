import {AxiosRequestConfig} from "axios";

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
