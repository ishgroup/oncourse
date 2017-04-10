import {Action} from "redux";

export interface IshAction<T> extends Action {
  payload: T;
  error?: boolean;
}
