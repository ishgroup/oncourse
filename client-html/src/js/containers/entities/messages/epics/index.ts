import { combineEpics } from "redux-observable";
import { EpicSendMessage } from "./EpicSendMessage";

export const EpicMessage = combineEpics(EpicSendMessage);