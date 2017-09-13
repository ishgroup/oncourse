import * as Actions from "../actions";
import {CmsConfig} from "../../configLoader";

/**
 * Handle changing config properties.
 */

export const configReducer = (state: CmsConfig = new CmsConfig({}), action): any => {
  switch (action.type) {
    case Actions.UPDATE_CMS_CONFIG:
      return action.payload;
    default:
      return state;
  }
};
