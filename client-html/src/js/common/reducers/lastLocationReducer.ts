import { IAction } from "../actions/IshAction";
import { CLEAR_LAST_LOCATION, SET_LAST_LOCATION } from "../actions";

export const lastLocationReducer = (state: string = null, action: IAction<any>): any => {
  switch (action.type) {
    case SET_LAST_LOCATION: {
      return action.payload;
    }

    case CLEAR_LAST_LOCATION: {
      return null;
    }

    default:
      return state;
  }
};
