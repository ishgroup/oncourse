import { IAction } from "../actions/IshAction";
import { SET_LAST_LOCATION, CLEAR_LAST_LOCATION } from "../actions";

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
