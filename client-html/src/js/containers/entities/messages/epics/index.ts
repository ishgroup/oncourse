import { combineEpics } from 'redux-observable';
import { EpicInterruptSendMessage } from './EpicInterruptSendMessage';
import { EpicPollSendMessageProcess } from './EpicPollSendMessageProcess';
import { EpicSendMessage } from './EpicSendMessage';

export const EpicMessage = combineEpics(
  EpicSendMessage,
  EpicPollSendMessageProcess,
  EpicInterruptSendMessage
);