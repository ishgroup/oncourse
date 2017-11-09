import {IAction} from "../../../../actions/IshAction";
import {ModalState} from "./State";
import {SHOW_MODAL, HIDE_MODAL} from "../actions";

export const modalReducer = (state: ModalState = new ModalState(), action: IAction<any>): ModalState => {
  switch (action.type) {

    case SHOW_MODAL:
      return {
        ...state,
        ...action.payload,
        show: true,
      };

    case HIDE_MODAL:
      return {
        ...new ModalState(),
        show: false,
      };

    case `RNS_HIDE_NOTIFICATION`:
      return {
        ...state,
      };

    default:
      return {
        ...state,
        show: false,
      };
  }
};
