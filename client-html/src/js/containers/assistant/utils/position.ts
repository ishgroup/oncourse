/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LSGetItem, LSSetItem } from '../../../common/utils/storage';
import { APP_BAR_HEIGHT } from '../../../constants/Config';
import { ASSISTANT_EDGE_MARGIN, ASSISTANT_POSITION_STORAGE_KEY } from '../constants';

/**
 * Distances from the right and bottom edges of the window, so the button and the panel stay in their corner
 * when the window is resized.
 */
export interface AssistantPosition {
  right: number;
  bottom: number;
}

export type AssistantDraggable = "button" | "panel";

type StoredPositions = Partial<Record<AssistantDraggable, AssistantPosition>>;

// above the bottom app bar of lists, so it doesn't cover its buttons
export const DEFAULT_ASSISTANT_POSITION: AssistantPosition = { right: 24, bottom: APP_BAR_HEIGHT + 16 };

const isPosition = (value: any): value is AssistantPosition =>
  Boolean(value) && Number.isFinite(value.right) && Number.isFinite(value.bottom);

const readPositions = (): StoredPositions => {
  try {
    const stored = JSON.parse(LSGetItem(ASSISTANT_POSITION_STORAGE_KEY));
    return stored && typeof stored === "object" ? stored : {};
  } catch {
    return {};
  }
};

export const getStoredPosition = (item: AssistantDraggable): AssistantPosition => {
  const position = readPositions()[item];
  return isPosition(position) ? { right: position.right, bottom: position.bottom } : null;
};

export const storePosition = (item: AssistantDraggable, position: AssistantPosition) => {
  LSSetItem(ASSISTANT_POSITION_STORAGE_KEY, JSON.stringify({ ...readPositions(), [item]: position }));
};

const clamp = (value: number, min: number, max: number) => Math.max(min, Math.min(value, Math.max(min, max)));

/**
 * @return the position that keeps an element of the size inside the window
 */
export const clampPosition = (position: AssistantPosition, width: number, height: number): AssistantPosition => ({
  right: clamp(position.right, ASSISTANT_EDGE_MARGIN, window.innerWidth - width - ASSISTANT_EDGE_MARGIN),
  bottom: clamp(position.bottom, ASSISTANT_EDGE_MARGIN, window.innerHeight - height - ASSISTANT_EDGE_MARGIN)
});
