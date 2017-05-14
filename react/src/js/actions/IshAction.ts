import {Action} from "redux";

export interface IAction extends Action {
  type: string;
}

export interface IshAction<T> extends IAction {
  payload: T;
  error?: boolean;
}
