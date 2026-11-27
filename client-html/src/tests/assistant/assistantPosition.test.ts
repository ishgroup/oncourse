/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ASSISTANT_EDGE_MARGIN, ASSISTANT_POSITION_STORAGE_KEY } from '../../js/containers/assistant/constants';
import { clampPosition, getStoredPosition, storePosition } from '../../js/containers/assistant/utils/position';

describe("assistant button and panel position", () => {
  afterEach(() => {
    localStorage.removeItem(ASSISTANT_POSITION_STORAGE_KEY);
  });

  it("keeps an element inside the window", () => {
    // jsdom's window is 1024 x 768
    expect(clampPosition({ right: -20, bottom: 5000 }, 44, 44)).toEqual({
      right: ASSISTANT_EDGE_MARGIN,
      bottom: 768 - 44 - ASSISTANT_EDGE_MARGIN
    });
    expect(clampPosition({ right: 5000, bottom: -20 }, 420, 600)).toEqual({
      right: 1024 - 420 - ASSISTANT_EDGE_MARGIN,
      bottom: ASSISTANT_EDGE_MARGIN
    });
    expect(clampPosition({ right: 100, bottom: 200 }, 44, 44)).toEqual({ right: 100, bottom: 200 });
  });

  it("stores the button's and the panel's positions apart", () => {
    storePosition("button", { right: 10, bottom: 20 });
    storePosition("panel", { right: 30, bottom: 40 });
    storePosition("button", { right: 50, bottom: 60 });

    expect(getStoredPosition("button")).toEqual({ right: 50, bottom: 60 });
    expect(getStoredPosition("panel")).toEqual({ right: 30, bottom: 40 });
  });

  it("has no position until one is stored, and ignores a broken one", () => {
    expect(getStoredPosition("button")).toBeNull();

    localStorage.setItem(ASSISTANT_POSITION_STORAGE_KEY, "not json");
    expect(getStoredPosition("button")).toBeNull();

    localStorage.setItem(ASSISTANT_POSITION_STORAGE_KEY, JSON.stringify({ button: { right: "far", bottom: 20 } }));
    expect(getStoredPosition("button")).toBeNull();
  });
});
