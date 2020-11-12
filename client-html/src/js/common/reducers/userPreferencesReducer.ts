import { IAction } from "../actions/IshAction";
import { GET_USER_PREFERENCES_FULFILLED } from "../actions";

export interface UserPreferencesState {
  [key: string]: string;
}

export const userPreferencesReducer = (state: UserPreferencesState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_USER_PREFERENCES_FULFILLED: {
      const { preferences } = action.payload;

      return {
        ...state,
        ...preferences
      };
    }

    default:
      return state;
  }
};
