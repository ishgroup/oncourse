import { IAction } from "../../../actions/IshAction";
import { GET_GOOGLE_GEOCODE_DETAILS_FULFILLED } from "../actions/index";

export interface GoogleApiResponse {
  responseJSON?: any;
}

export const googleApiReducer = (state: GoogleApiResponse = {}, action: IAction<any>): GoogleApiResponse => {
  switch (action.type) {
    case GET_GOOGLE_GEOCODE_DETAILS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
