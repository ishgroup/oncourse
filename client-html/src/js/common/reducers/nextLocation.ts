import { NEXT_LOCATION } from "../actions";
import { IAction } from "../actions/IshAction";

export const setNextLocationReducer = (state: string = '', action: IAction) => {
  switch (action.type) {
    case NEXT_LOCATION: {
      return action.payload;
    }
    default:
      return state;
  }
}