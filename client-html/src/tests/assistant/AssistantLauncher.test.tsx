/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  act, fireEvent, render, screen, waitFor, within
} from '@testing-library/react';
import * as React from 'react';
import { AssistantApiMock } from '../../dev/mock/api/assistant/AssistantApiMock';
import FullScreenEditView from '../../js/common/components/list-view/components/full-screen-edit-view/FullScreenEditView';
import { GET_USER_PREFERENCES_FULFILLED } from '../../js/common/actions';
import { ASSISTANT_AVAILABLE_KEY } from '../../js/constants/Config';
import TestStore from '../../js/constants/Store';
import { setAssistantOpen } from '../../js/containers/assistant/actions';
import AssistantLauncher from '../../js/containers/assistant/components/AssistantLauncher';
import {
  ASSISTANT_BUTTON_SIZE,
  ASSISTANT_HOST_ID,
  ASSISTANT_PANEL_ID,
  ASSISTANT_POSITION_STORAGE_KEY
} from '../../js/containers/assistant/constants';
import { DEFAULT_ASSISTANT_POSITION } from '../../js/containers/assistant/utils/position';
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

  it("hides when the assistant was turned off for the user meanwhile", async () => {
    // the availability comes from the store, as loaded at login, and the mocked server now says it is off
    TestStore.dispatch({
      type: GET_USER_PREFERENCES_FULFILLED,
      payload: { preferences: { [ASSISTANT_AVAILABLE_KEY]: "true" } }
    });
    mockedAPI.api.onPost(MESSAGE_URL).reply(403, { errorMessage: "The onCourse Assistant is not available to you" });
    renderLauncher({ systemUser: { firstName: "Ann" } });
    fireEvent.click(screen.getByRole("button", { name: "Ask onCourse AI" }));

    fireEvent.change(input(), { target: { value: "hi" } });
    fireEvent.keyDown(input(), { key: "Enter" });

    await waitFor(() => expect(screen.queryByRole("dialog", { name: /onCourse Assistant/ })).not.toBeInTheDocument());
    expect(screen.queryByRole("button", { name: "Ask onCourse AI" })).not.toBeInTheDocument();
    expect(TestStore.getState().userPreferences[ASSISTANT_AVAILABLE_KEY]).toBe("false");
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

    const page = (editViewOpen: boolean, editViewMounted = true) => (
      <TestEntry state={availableState}>
        {editViewMounted && (
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
        )}
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

    it("keeps the panel on the page when the edit view is unmounted while it is open", async () => {
      const { rerender } = render(page(true));

      fireEvent.click(screen.getByRole("button", { name: "Ask onCourse AI" }));
      fireEvent.change(input(), { target: { value: "draft" } });

      rerender(page(true, false));

      const host = document.getElementById(ASSISTANT_HOST_ID);
      expect(host.isConnected).toBe(true);
      expect(host.parentElement).toBe(document.body);
      const panel = await screen.findByRole("dialog", { name: /onCourse Assistant/ });
      expect(panel.closest("[aria-hidden='true']")).toBeNull();
      expect(input()).toHaveValue("draft");
    });

    it("moves the panel to the page when the edit view is taken off it before its cleanup", () => {
      const { rerender } = render(page(true));
      fireEvent.click(screen.getByRole("button", { name: "Ask onCourse AI" }));

      // the edit view's dialog leaves the page first, as it can while unmounting
      const host = document.getElementById(ASSISTANT_HOST_ID);
      const editViewRoot = [...document.body.children].find(child => child.contains(host));
      editViewRoot.remove();
      expect(host.isConnected).toBe(false);

      // the next render of the launcher places the host again
      rerender(page(true));

      expect(host.isConnected).toBe(true);
      expect(host.parentElement).toBe(document.body);
      expect(screen.getByRole("dialog", { name: /onCourse Assistant/ })).toBeInTheDocument();

      // back for React to unmount it
      document.body.appendChild(editViewRoot);
    });
  });

  describe("dragged over the window", () => {
    // jsdom has no pointer events, a mouse event with a pointer id stands in for them
    class TestPointerEvent extends MouseEvent {
      pointerId: number;

      isPrimary: boolean;

      constructor(type: string, init: any = {}) {
        super(type, init);
        this.pointerId = init.pointerId ?? 1;
        this.isPrimary = init.isPrimary ?? true;
      }
    }

    const storedPositions = () => JSON.parse(localStorage.getItem(ASSISTANT_POSITION_STORAGE_KEY));

    beforeAll(() => {
      (window as any).PointerEvent = TestPointerEvent;
    });

    afterAll(() => {
      delete (window as any).PointerEvent;
    });

    afterEach(() => {
      localStorage.removeItem(ASSISTANT_POSITION_STORAGE_KEY);
    });

    it("moves the button, keeps its position and doesn't open the panel at the end of the drag", () => {
      renderLauncher();
      const button = screen.getByRole("button", { name: "Ask onCourse AI" });

      fireEvent.pointerDown(button, { clientX: 900, clientY: 600 });
      fireEvent.pointerMove(button, { clientX: 800, clientY: 500 });
      fireEvent.pointerUp(button, { clientX: 800, clientY: 500 });
      fireEvent.click(button);

      const moved = {
        right: DEFAULT_ASSISTANT_POSITION.right + 100,
        bottom: DEFAULT_ASSISTANT_POSITION.bottom + 100
      };
      expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
      expect(storedPositions()).toEqual({ button: moved });
      expect(button).toHaveStyle({ right: `${moved.right}px`, bottom: `${moved.bottom}px` });

      // a click without a drag still opens the panel
      fireEvent.click(button);
      expect(screen.getByRole("dialog", { name: /onCourse Assistant/ })).toBeInTheDocument();
    });

    it("puts the button where the user left it, growing towards the middle of the window", () => {
      // jsdom's window is 1024 pixels wide, so this is in its left half
      localStorage.setItem(ASSISTANT_POSITION_STORAGE_KEY, JSON.stringify({ button: { right: 900, bottom: 300 } }));

      renderLauncher();

      const button = screen.getByRole("button", { name: "Ask onCourse AI" });
      expect(button).toHaveStyle({ left: `${1024 - 900 - ASSISTANT_BUTTON_SIZE}px`, bottom: "300px" });
      expect(button).toHaveAttribute("data-grows", "right");
    });

    it("moves the panel by its header, but not by its Close button", () => {
      const panel = openPanel();
      const title = within(panel).getByRole("heading", { name: /onCourse Assistant/ });

      fireEvent.pointerDown(title, { clientX: 500, clientY: 300 });
      fireEvent.pointerMove(title, { clientX: 450, clientY: 280 });
      fireEvent.pointerUp(title, { clientX: 450, clientY: 280 });

      const close = within(panel).getByRole("button", { name: "Close" });
      fireEvent.pointerDown(close, { clientX: 450, clientY: 280 });
      fireEvent.pointerMove(close, { clientX: 300, clientY: 100 });
      fireEvent.pointerUp(close, { clientX: 300, clientY: 100 });

      const moved = {
        right: DEFAULT_ASSISTANT_POSITION.right + 50,
        bottom: DEFAULT_ASSISTANT_POSITION.bottom + 20
      };
      expect(storedPositions()).toEqual({ panel: moved });
      expect(panel).toHaveStyle({ right: `${moved.right}px`, bottom: `${moved.bottom}px` });
    });
  });

  describe("where the browser animates", () => {
    const animations: { element: Element; animation: { onfinish: () => void; cancel: jest.Mock } }[] = [];

    beforeEach(() => {
      animations.length = 0;
      (Element.prototype as any).animate = function animate() {
        const animation = { onfinish: null, cancel: jest.fn() };
        animations.push({ element: this, animation });
        return animation;
      };
    });

    afterEach(() => {
      delete (Element.prototype as any).animate;
    });

    const panelAnimations = () => animations.filter(({ element }) => element.id === ASSISTANT_PANEL_ID);

    it("grows the panel out of the button and collapses it back before showing the button", async () => {
      const panel = openPanel();
      expect(panelAnimations()).toHaveLength(1);

      fireEvent.click(within(panel).getByRole("button", { name: "Close" }));

      // still there while it collapses
      expect(screen.getByRole("dialog", { name: /onCourse Assistant/ })).toBe(panel);
      expect(screen.queryByRole("button", { name: "Ask onCourse AI" })).not.toBeInTheDocument();

      act(() => panelAnimations()[1].animation.onfinish());

      expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
      await waitFor(() => expect(screen.getByRole("button", { name: "Ask onCourse AI" })).toHaveFocus());
    });

    it("stays open when it is opened again while it collapses", () => {
      openPanel();
      fireEvent.keyDown(input(), { key: "Escape" });
      const collapse = panelAnimations()[1].animation;

      act(() => {
        TestStore.dispatch(setAssistantOpen(true));
      });

      expect(collapse.cancel).toHaveBeenCalled();
      expect(screen.getByRole("dialog", { name: /onCourse Assistant/ })).toBeInTheDocument();
    });
  });
});
