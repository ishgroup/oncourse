/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ProcessResult } from '@api/model';
import { Epic, ofType, StateObservable } from 'redux-observable';
import { from, Observable, timer } from 'rxjs';
import { catchError, filter, mergeMap } from 'rxjs/operators';
import { closeSendMessage, FETCH_FAIL, FETCH_SUCCESS, sendMessageFailed } from '../../../../common/actions';
import { IAction } from '../../../../common/actions/IshAction';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import ProcessService from '../../../../common/services/ProcessService';
import { State } from '../../../../reducers/state';
import { POLL_SEND_MESSAGE_PROCESS, pollSendMessageProcess } from '../actions';

const POLL_DELAY = 1000;

// Polling stops as soon as the dialog is closed/reopened or another send has started
const isCurrentProcess = (state: State, processId: string) =>
  state.sendMessage.sending && state.sendMessage.processId === processId;

const processStatus = (process: ProcessResult, processId: string): IAction<any>[] => {
  switch (process?.status) {
    case "Finished":
      return [
        closeSendMessage(),
        {
          type: FETCH_SUCCESS,
          payload: { message: "All messages sent" }
        }
      ];
    case "In progress":
      return [pollSendMessageProcess(processId)];
    case "Not found":
    case "Failed":
    default:
      return [
        sendMessageFailed(),
        {
          type: FETCH_FAIL,
          payload: { message: process?.message || "Messages sending failed" }
        }
      ];
  }
};

export const EpicPollSendMessageProcess: Epic<any, any> = (action$: Observable<any>, state$: StateObservable<State>) => action$.pipe(
  ofType(POLL_SEND_MESSAGE_PROCESS),
  mergeMap(({ payload: { processId } }) => timer(POLL_DELAY).pipe(
    filter(() => isCurrentProcess(state$.value, processId)),
    mergeMap(() => from(ProcessService.getProcessStatus(processId)).pipe(
      mergeMap(process => (isCurrentProcess(state$.value, processId) ? processStatus(process, processId) : [])),
      catchError(response => (isCurrentProcess(state$.value, processId)
        ? [sendMessageFailed(), ...FetchErrorHandler(response, "Messages sending failed")]
        : []))
    ))
  ))
);
