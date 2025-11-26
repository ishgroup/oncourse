import { GoogleMapsCoordinates } from '../../../../model/google';
import { IAction } from '../../../actions/IshAction';
import { GET_GOOGLE_GEOCODE_DETAILS_FULFILLED } from '../actions/index';

export interface GoogleApiResponse {
  geocode?: GoogleMapsCoordinates;
}

export const googleApiReducer = (state: GoogleApiResponse = {}, action: IAction<any>): GoogleApiResponse => {
  switch (action.type) {
    case GET_GOOGLE_GEOCODE_DETAILS_FULFILLED: {
      return {
        ...state,
        geocode: {
          ...action.payload
        }
      };
    }

    default:
      return state;
  }
};
