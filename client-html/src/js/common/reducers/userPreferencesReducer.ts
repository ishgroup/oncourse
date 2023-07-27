import { IAction } from "../actions/IshAction";
import { GET_USER_PREFERENCES_FULFILLED } from "../actions";
import { SET_READ_NEWS_LOCAL } from "../components/list-view/actions";
import { READ_NEWS } from "../../constants/Config";
import { PreferenceEnum } from "@api/model";

export type UserPreferencesState = {
  [K in PreferenceEnum]?: K extends "payment.gateway.type" ? "EWAY" | "EWAY_TEST" | "WINDCAVE" | "TEST" | "DISABLED" | "OFFLINE" : string;
}

export const userPreferencesReducer = (state: UserPreferencesState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_USER_PREFERENCES_FULFILLED: {
      const {preferences} = action.payload;

      return {
        ...state,
        ...preferences
      };
    }

    case SET_READ_NEWS_LOCAL: {
      const stateCopy = JSON.parse(JSON.stringify(state));
      stateCopy[READ_NEWS] = stateCopy[READ_NEWS] + `,${action.payload}`;

      return {
        ...stateCopy,
      };
    }

    default:
      return state;
  }
};
