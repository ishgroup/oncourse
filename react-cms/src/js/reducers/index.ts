import {State} from "./State";
import {IAction} from "../actions/IshAction";

export const reducer = (state: State = new State(), action: IAction<any>): State => {
  switch (action.type) {

    default:
      return state;
  }
};
