import { _toRequestType, FULFILLED } from "../../../actions/ActionUtils";

export const GET_GOOGLE_GEOCODE_DETAILS = _toRequestType("get/google/maps/geocode");
export const GET_GOOGLE_GEOCODE_DETAILS_FULFILLED = FULFILLED(GET_GOOGLE_GEOCODE_DETAILS);

export const getGeocodeDetails = (address: string) => ({
  type: GET_GOOGLE_GEOCODE_DETAILS,
  payload: { address }
});
