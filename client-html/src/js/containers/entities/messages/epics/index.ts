import { combineEpics } from "redux-observable";
import { EpicGetMessage } from "./EpicGetMessage";
import { EpicDeleteMessage } from "./EpicDeleteMessage";
import { EpicSendMessage } from "./EpicSendMessage";

export const EpicMessage = combineEpics(EpicGetMessage, EpicDeleteMessage, EpicSendMessage);
