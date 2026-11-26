/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import * as React from 'react';
import { AssistantApiMock } from '../../dev/mock/api/assistant/AssistantApiMock';
import FullScreenEditView from '../../js/common/components/list-view/components/full-screen-edit-view/FullScreenEditView';
import { ASSISTANT_AVAILABLE_KEY } from '../../js/constants/Config';
import TestStore from '../../js/constants/Store';
import AssistantLauncher from '../../js/containers/assistant/components/AssistantLauncher';
import { ASSISTANT_HOST_ID } from '../../js/containers/assistant/constants';
import { GET_IS_LOGGED_FULFILLED } from '../../js/containers/preferences/actions';
import { mockedAPI, TestEntry } from '../TestEntry';

const MESSAGE_URL = "/v1/assistant/message";

const availableState = {
  userPreferences: { [ASSISTANT_AVAILABLE_KEY]: "true" },
  systemUser: { firstName: "Ann" }
};

const renderLauncher = (state: object = availableState) => render(
  <TestEntry state={state}>
    <AssistantLauncher />
  </TestEntry>
);

const openPanel = () => {
  renderLauncher();
  fireEvent.click(screen.getByRole("button", { name: "Ask onCourse AI" }));
  return screen.getByRole("dialog", { name: /onCourse Assistant/ });
};

const input = () => screen.getByRole("textbox", { name: "Message to the onCourse Assistant" });

const postedTexts = () => mockedAPI.api.history.post
  .filter(request => request.url === MESSAGE_URL)
  .map(request => JSON.parse(request.data).text);

describe("Ask onCourse AI", () => {
  beforeEach(() => {
    // the conversation lives in the shared test store, start each test without one
    TestStore.dispatch({ type: GET_IS_LOGGED_FULFILLED, payload: false });
    mockedAPI.api.resetHistory();
  });

  afterEach(() => {
    AssistantApiMock.apply(mockedAPI);
  });

  it("is hidden when the assistant is not available to the user", () => {
    renderLauncher({ userPreferences: { [ASSISTANT_AVAILABLE_KEY]: "false" } });

    expect(screen.queryByRole("button", { name: "Ask onCourse AI" })).not.toBeInTheDocument();
  });

  it("opens the panel with a greeting and suggestions", () => {
    const panel = openPanel();

    expect(within(panel).getByText(/Hi Ann, I can create courses, schedule classes/)).toBeInTheDocument();
    ["Create a new course", "Schedule classes", "Check conflicts", "Under-enrolled classes"].forEach(name => {
      expect(within(panel).getByRole("button", { name })).toBeInTheDocument();
    });
    expect(input()).toHaveFocus();
    expect(input()).toHaveAttribute("placeholder", "Describe a course, class or schedule…");
    expect(screen.queryByRole("button", { name: "Ask onCourse AI" })).not.toBeInTheDocument();
  });

  it("sends the message on Enter and announces the reply", async () => {
    openPanel();

    fireEvent.change(input(), { target: { value: "hi" } });
    fireEvent.keyDown(input(), { key: "Enter" });

    const log = screen.getByRole("log");
    expect(log).toHaveAttribute("aria-live", "polite");
    await waitFor(() => expect(within(log).getByText("Mock reply to: hi")).toBeInTheDocument());
    expect(within(log).getByText("hi")).toBeInTheDocument();
    expect(postedTexts()).toEqual(["hi"]);
    expect(input()).toHaveValue("");
  });

  it("adds a new line on Shift+Enter instead of sending", () => {
    openPanel();

    fireEvent.change(input(), { target: { value: "hi" } });
    fireEvent.keyDown(input(), { key: "Enter", shiftKey: true });

    expect(postedTexts()).toEqual([]);
    expect(input()).toHaveValue("hi");
  });

  it("sends a suggestion when it is chosen", async () => {
    openPanel();

    fireEvent.click(screen.getByRole("button", { name: "Check conflicts" }));

    await waitFor(() => expect(screen.getByText("Mock reply to: Check conflicts")).toBeInTheDocument());
    expect(postedTexts()).toEqual(["Check conflicts"]);
    expect(screen.queryByRole("button", { name: "Create a new course" })).not.toBeInTheDocument();
  });

  it("shows that the assistant is thinking until the reply comes", async () => {
    let answer: () => void;
    mockedAPI.api.onPost(MESSAGE_URL).reply(() => new Promise(resolve => {
      answer = () => resolve([200, { conversationId: "c1", text: "Done" }]);
    }));
    openPanel();

    fireEvent.change(input(), { target: { value: "hi" } });
    fireEvent.click(screen.getByRole("button", { name: "Send" }));

    await waitFor(() => expect(screen.getByRole("status")).toHaveTextContent("onCourse Assistant is thinking…"));
    fireEvent.change(input(), { target: { value: "more" } });
    expect(screen.getByRole("button", { name: "Send" })).toBeDisabled();

    await waitFor(() => expect(answer).toBeDefined());
    answer();

    await waitFor(() => expect(screen.getByText("Done")).toBeInTheDocument());
    expect(screen.getByRole("status")).toHaveTextContent("");
    expect(screen.getByRole("button", { name: "Send" })).toBeEnabled();
  });

  it("shows an error in the panel", async () => {
    mockedAPI.api.onPost(MESSAGE_URL).reply(403, { errorMessage: "The onCourse Assistant is not available to you" });
    openPanel();

    fireEvent.change(input(), { target: { value: "hi" } });
    fireEvent.keyDown(input(), { key: "Enter" });

    await waitFor(() => expect(screen.getByRole("alert")).toHaveTextContent("The onCourse Assistant is not available to you"));
  });

  it("closes on Escape and returns focus to the button", async () => {
    openPanel();

    fireEvent.keyDown(input(), { key: "Escape" });

    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    await waitFor(() => expect(screen.getByRole("button", { name: "Ask onCourse AI" })).toHaveFocus());
  });

  it("closes with the Close button", () => {
    openPanel();

    fireEvent.click(screen.getByRole("button", { name: "Close" }));

    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Ask onCourse AI" })).toBeInTheDocument();
  });

  describe("on a record's full screen edit view", () => {
    const RecordContent = () => <input aria-label="Course name" />;

    const page = (editViewOpen: boolean) => (
      <TestEntry state={availableState}>
        <FullScreenEditView
          form="AssistantLauncherTestForm"
          rootEntity="Course"
          EditViewContent={RecordContent}
          fullScreenEditView={editViewOpen}
          toogleFullScreenEditView={jest.fn()}
          showConfirm={jest.fn()}
          creatingNew
          hideTitle
        />
        <AssistantLauncher />
      </TestEntry>
    );

    const editView = () => screen.getByRole("dialog", { name: "" });

    beforeEach(() => {
      window.performance.getEntriesByName = jest.fn(() => []);
    });

    it("shows the button over the edit view, not hidden by it", () => {
      render(page(true));

      // queries by role skip elements hidden with aria-hidden, as the modal edit view hides the page under it
      expect(screen.getByRole("textbox", { name: "Course name" })).toBeInTheDocument();
      const button = screen.getByRole("button", { name: "Ask onCourse AI" });
      expect(editView()).toContainElement(button);
      expect(button.closest("[aria-hidden='true']")).toBeNull();
    });

    it("lets the user type and send messages while the record is open", async () => {
      render(page(true));

      fireEvent.click(screen.getByRole("button", { name: "Ask onCourse AI" }));
      const panel = screen.getByRole("dialog", { name: /onCourse Assistant/ });

      expect(editView()).toContainElement(panel);
      await waitFor(() => expect(input()).toHaveFocus());

      fireEvent.change(input(), { target: { value: "hi" } });
      fireEvent.keyDown(input(), { key: "Enter" });

      await waitFor(() => expect(within(screen.getByRole("log")).getByText("Mock reply to: hi")).toBeInTheDocument());
      expect(postedTexts()).toEqual(["hi"]);
      // the edit view doesn't take the focus back
      expect(input()).toHaveFocus();
    });

    it("keeps the conversation when the record is closed", async () => {
      const { rerender } = render(page(true));

      fireEvent.click(screen.getByRole("button", { name: "Ask onCourse AI" }));
      fireEvent.change(input(), { target: { value: "draft" } });

      rerender(page(false));

      const panel = await screen.findByRole("dialog", { name: /onCourse Assistant/ });
      expect(document.getElementById(ASSISTANT_HOST_ID).parentElement).toBe(document.body);
      expect(panel.closest("[aria-hidden='true']")).toBeNull();
      expect(input()).toHaveValue("draft");
    });
  });
});
