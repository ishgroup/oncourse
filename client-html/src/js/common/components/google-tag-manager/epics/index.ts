import { combineEpics } from "redux-observable";
import { EpicInitGoogleAnalytics } from "./EpicInitGoogleAnalytics";
import { EpicSendGoogleAnalytics } from "./EpicSendGoogleAnalytics";
import { EpicTrackNestedEditViewOpening } from "./EpicTrackNestedEditViewOpening";
import { EpicGetGoogleAnalyticsParameters } from "./EpicGetGoogleAnalyticsParameters";

export const EpicGoogleAnalytics = combineEpics(
  EpicGetGoogleAnalyticsParameters,
  EpicInitGoogleAnalytics,
  EpicSendGoogleAnalytics,
  EpicTrackNestedEditViewOpening
);
