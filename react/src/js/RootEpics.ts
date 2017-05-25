import {combineEpics} from "redux-observable";
import {CheckoutEpic} from "./enrol/epics/CheckoutEpic";
import {reduxFormEpics} from "./epics/reduxFormEpics";
import {WebEpic} from "./web/epics/WebEpic";

export const RootEpic = combineEpics(
  WebEpic,
  CheckoutEpic,
  reduxFormEpics,
);
