/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  sendAssistantMessage,
  sendAssistantMessageFulfilled,
  sendAssistantMessageRejected,
  setAssistantOpen
} from '../../js/containers/assistant/actions';
import { AssistantInitialState, assistantReducer } from '../../js/containers/assistant/reducers';
import { GET_IS_LOGGED_FULFILLED } from '../../js/containers/preferences/actions';

describe("onCourse Assistant reducer", () => {
  it("starts closed and empty", () => {
    expect(assistantReducer(undefined, { type: "@@INIT" })).toEqual(AssistantInitialState);
  });

  it("opens and closes the panel", () => {
    const opened = assistantReducer(AssistantInitialState, setAssistantOpen(true));
    expect(opened.open).toBe(true);
    expect(assistantReducer(opened, setAssistantOpen(false)).open).toBe(false);
  });

  it("adds the user's message and waits for the reply", () => {
    const state = assistantReducer({ ...AssistantInitialState, error: "Old error" }, sendAssistantMessage("Create a course"));

    expect(state.messages).toEqual([expect.objectContaining({ role: "user", text: "Create a course" })]);
    expect(state.pending).toBe(true);
    expect(state.error).toBeNull();
  });

  it("ignores messages sent while waiting for a reply", () => {
    const waiting = assistantReducer(AssistantInitialState, sendAssistantMessage("first"));

    expect(assistantReducer(waiting, sendAssistantMessage("second"))).toBe(waiting);
  });

  it("adds the reply and keeps the conversation", () => {
    const waiting = assistantReducer(AssistantInitialState, sendAssistantMessage("hi"));
    const state = assistantReducer(waiting, sendAssistantMessageFulfilled({ conversationId: "c1", text: "Hello" }));

    expect(state.messages.map(m => [m.role, m.text])).toEqual([["user", "hi"], ["assistant", "Hello"]]);
    expect(new Set(state.messages.map(m => m.id)).size).toBe(2);
    expect(state.conversationId).toBe("c1");
    expect(state.pending).toBe(false);
  });

  it("shows the error and lets the user try again", () => {
    const waiting = assistantReducer(AssistantInitialState, sendAssistantMessage("hi"));
    const state = assistantReducer(waiting, sendAssistantMessageRejected("Can't answer"));

    expect(state.pending).toBe(false);
    expect(state.error).toBe("Can't answer");
    expect(state.messages).toHaveLength(1);
  });

  it("forgets everything on logout", () => {
    const waiting = assistantReducer({ ...AssistantInitialState, open: true }, sendAssistantMessage("hi"));
    const state = assistantReducer(waiting, sendAssistantMessageFulfilled({ conversationId: "c1", text: "Hello" }));

    expect(assistantReducer(state, { type: GET_IS_LOGGED_FULFILLED, payload: true })).toBe(state);
    expect(assistantReducer(state, { type: GET_IS_LOGGED_FULFILLED, payload: false })).toEqual(AssistantInitialState);
  });

  it("ignores a reply or an error that comes after logout", () => {
    const waiting = assistantReducer(AssistantInitialState, sendAssistantMessage("hi"));
    const loggedOut = assistantReducer(waiting, { type: GET_IS_LOGGED_FULFILLED, payload: false });

    expect(assistantReducer(loggedOut, sendAssistantMessageFulfilled({ conversationId: "c1", text: "Hello" }))).toBe(loggedOut);
    expect(assistantReducer(loggedOut, sendAssistantMessageRejected("Can't answer"))).toBe(loggedOut);
  });
});
