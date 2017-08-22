import {combineEpics} from "redux-observable";
import {EpicCheckout} from "./enrol/epics/EpicCheckout";
import {reduxFormEpics} from "./epics/reduxFormEpics";
import {WebEpic} from "./web/epics/WebEpic";

export const EpicRoot = combineEpics(
  WebEpic,
  EpicCheckout,
  reduxFormEpics,
);
