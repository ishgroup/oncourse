import { combineEpics } from "redux-observable";
import { EpicArchiveMessages } from "./EpicArchiveMessages";
import { EpicSendMessage } from "./EpicSendMessage";

export const EpicMessage = combineEpics(
  EpicArchiveMessages,
  EpicSendMessage
);