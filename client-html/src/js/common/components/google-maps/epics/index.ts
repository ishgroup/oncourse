import { combineEpics } from "redux-observable";
import { EpicGetGoogleGeocode } from "./EpicGetGoogleGeocode";

export const EpicGoogleMaps = combineEpics(EpicGetGoogleGeocode);
