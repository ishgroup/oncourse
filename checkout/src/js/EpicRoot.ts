import {combineEpics} from "redux-observable";
import {EpicCheckout} from "./enrol/epics/EpicCheckout";
import {reduxFormEpics} from "./epics/reduxFormEpics";
import {WebEpic} from "./web/epics/WebEpic";
import {GoogleAnalyticsEpic} from "./epics/GoogleAnalyticsEpic";

export const EpicRoot = combineEpics(
  WebEpic,
  EpicCheckout,
  reduxFormEpics,
  GoogleAnalyticsEpic,
);
