/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

// the test store first: the epic and the store import each other
import { DefaultEpic } from '../common/Default.Epic';
import { mockedAPI } from '../TestEntry';
import { waitFor } from '@testing-library/react';
import { StateObservable } from 'redux-observable';
import { lastValueFrom, Subject } from 'rxjs';
import { toArray } from 'rxjs/operators';
import TestStore from '../../js/constants/Store';
import { GET_IS_LOGGED_FULFILLED } from '../../js/containers/preferences/actions';
import { AssistantApiMock, MOCK_CONVERSATION_ID } from '../../dev/mock/api/assistant/AssistantApiMock';
import {
  SEND_ASSISTANT_MESSAGE_FULFILLED,
  SEND_ASSISTANT_MESSAGE_REJECTED,
  sendAssistantMessage
} from '../../js/containers/assistant/actions';
import { EpicSendAssistantMessage } from '../../js/containers/assistant/epics/EpicSendAssistantMessage';
import { AssistantInitialState } from '../../js/containers/assistant/reducers';

const MESSAGE_URL = "/v1/assistant/message";

describe("Send a message to the onCourse Assistant epic", () => {
  beforeEach(() => {
    mockedAPI.api.resetHistory();
  });

  afterEach(() => {
    // back to the default mocked reply
    AssistantApiMock.apply(mockedAPI);
  });

  it("returns the assistant's reply", () => DefaultEpic({
    action: sendAssistantMessage("Create a course"),
    epic: EpicSendAssistantMessage,
    store: () => ({ assistant: { ...AssistantInitialState } }),
    processData: () => [
      {
        type: SEND_ASSISTANT_MESSAGE_FULFILLED,
        payload: { conversationId: MOCK_CONVERSATION_ID, text: "Mock reply to: Create a course" }
      }
    ]
  }));

  it("continues the conversation in the state", async () => {
    await DefaultEpic({
      action: sendAssistantMessage("hi"),
      epic: EpicSendAssistantMessage,
      store: () => ({ assistant: { ...AssistantInitialState, conversationId: "c1" } }),
      processData: () => [
        {
          type: SEND_ASSISTANT_MESSAGE_FULFILLED,
          payload: { conversationId: "c1", text: "Mock reply to: hi" }
        }
      ]
    });

    expect(JSON.parse(mockedAPI.api.history.post.at(-1).data)).toEqual({ conversationId: "c1", text: "hi" });
  });

  it("shows the server's message when the assistant is not available", () => {
    mockedAPI.api.onPost(MESSAGE_URL).reply(403, { errorMessage: "The onCourse Assistant is not available to you" });

    return DefaultEpic({
      action: sendAssistantMessage("hi"),
      epic: EpicSendAssistantMessage,
      store: () => ({ assistant: { ...AssistantInitialState } }),
      processData: () => [
        {
          type: SEND_ASSISTANT_MESSAGE_REJECTED,
          payload: { message: "The onCourse Assistant is not available to you" }
        }
      ]
    });
  });

  it("drops the reply when the user is logged out meanwhile", async () => {
    const answers: ((text: string) => void)[] = [];
    mockedAPI.api.onPost(MESSAGE_URL).reply(() => new Promise(resolve => {
      answers.push(text => resolve([200, { conversationId: "c1", text }]));
    }));

    const action$ = new Subject<any>();
    const state$ = new StateObservable<any>(new Subject(), { ...TestStore.getState(), assistant: { ...AssistantInitialState } });
    const output = lastValueFrom(EpicSendAssistantMessage(action$, state$, {}).pipe(toArray()));

    action$.next(sendAssistantMessage("from the previous user"));
    await waitFor(() => expect(answers).toHaveLength(1));

    action$.next({ type: GET_IS_LOGGED_FULFILLED, payload: false });
    // the next user's message is not ignored as sent while waiting for a reply
    action$.next(sendAssistantMessage("from the next user"));
    await waitFor(() => expect(answers).toHaveLength(2));

    answers[0]("For the previous user");
    answers[1]("For the next user");
    // the epic completes after the reply it waits for
    action$.complete();

    await expect(output).resolves.toEqual([
      { type: SEND_ASSISTANT_MESSAGE_FULFILLED, payload: { conversationId: "c1", text: "For the next user" } }
    ]);
  });

  it("asks to try again when the model can't answer", () => {
    mockedAPI.api.onPost(MESSAGE_URL).reply(503);

    return DefaultEpic({
      action: sendAssistantMessage("hi"),
      epic: EpicSendAssistantMessage,
      store: () => ({ assistant: { ...AssistantInitialState } }),
      processData: () => [
        {
          type: SEND_ASSISTANT_MESSAGE_REJECTED,
          payload: { message: "The onCourse Assistant can't answer right now. Please try again." }
        }
      ]
    });
  });
});
