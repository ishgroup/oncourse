import {IAction} from "../../../../actions/IshAction";
import {MessageState} from "./State";
import {SHOW_MESSAGE, CLEAR_MESSAGE} from "../actions";

export const messageReducer = (state: MessageState = new MessageState(), action: IAction<any>): MessageState => {
  switch (action.type) {
    case SHOW_MESSAGE:
      return {
        ...state,
        message: action.payload.message,
        success: action.payload.success,
      };
    case CLEAR_MESSAGE:
      return {
        ...state,
        message: "",
        success: true,
      };
    default:
      return {
        ...state,
      };
  }
};
