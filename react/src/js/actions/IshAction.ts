import {Action} from "redux";
import extend = hbs.Utils.extend;

export interface IAction extends Action {
  type: string;
}

export interface IshAction<T> extends IAction {
  payload: T;
  error?: boolean;
}
