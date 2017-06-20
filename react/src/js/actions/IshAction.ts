import {Action} from "redux";

export interface IAction<P> extends Action {
  type: string;
  payload?: P;
  meta?: any
}

export interface IshAction<T> extends IAction<T> {
  payload: T;
  error?: boolean;
}
