import { combineEpics } from "redux-observable";
import { EpicGetMessage } from "./EpicGetMessage";
import { EpicSendMessage } from "./EpicSendMessage";

export const EpicMessage = combineEpics(EpicGetMessage, EpicSendMessage);
