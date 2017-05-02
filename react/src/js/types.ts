import {AxiosRequestConfig} from "axios";
import {ReactNode} from "react";

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

export interface BaseProps {
  readonly children?: ReactNode;
}
