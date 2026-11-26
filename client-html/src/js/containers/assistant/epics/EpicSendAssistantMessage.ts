/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import { Epic, ofType, StateObservable } from "redux-observable";
import { from, Observable, of } from "rxjs";
import { catchError, exhaustMap, filter, map, takeUntil } from "rxjs/operators";
import { IAction } from "../../../common/actions/IshAction";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { State } from "../../../reducers/state";
import { GET_IS_LOGGED_FULFILLED } from "../../preferences/actions";
import { SEND_ASSISTANT_MESSAGE, sendAssistantMessageFulfilled, sendAssistantMessageRejected } from "../actions";
import AssistantService from "../services/AssistantService";

export const assistantErrorActions = (response: any): IAction<any>[] => {
  if (response?.status === 401) {
    // redirects to the login page, which also clears the conversation
    return FetchErrorHandler(response);
  }
  return [sendAssistantMessageRejected(response?.data?.errorMessage || $t("assistant_unavailable_try_again"))];
};

// Not EpicUtils.Create: a reply can take half a minute, and the global fetch progress would block the page meanwhile.
// The panel shows its own progress and errors instead. Messages sent while waiting for a reply are ignored.
// The reply is dropped when the user is logged out meanwhile: it is not for the next user in the tab,
// and the next user's messages are not ignored while it is waited for.
export const EpicSendAssistantMessage: Epic<any, any> = (
  action$: Observable<any>,
  state$: StateObservable<State>
): Observable<any> => action$.pipe(
  ofType(SEND_ASSISTANT_MESSAGE),
  exhaustMap(({ payload }) => from(AssistantService.sendMessage({
    conversationId: state$.value.assistant?.conversationId || undefined,
    text: payload.text
  })).pipe(
    // before catchError, which dispatches the logout of a 401 to this message itself
    takeUntil(action$.pipe(ofType(GET_IS_LOGGED_FULFILLED), filter(action => action.payload === false))),
    map(reply => sendAssistantMessageFulfilled(reply)),
    catchError(response => of(...assistantErrorActions(response)))
  ))
);
