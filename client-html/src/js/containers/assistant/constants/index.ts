/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const ASSISTANT_PANEL_WIDTH = 420;

export const ASSISTANT_PANEL_ID = "assistant-panel";

export const ASSISTANT_PANEL_TITLE_ID = "assistant-panel-title";

export const ASSISTANT_BUTTON_ID = "assistant-button";

// the button is a square with the icon until it shows its label
export const ASSISTANT_BUTTON_SIZE = 44;

export const ASSISTANT_HOST_ID = "assistant-host";

// where the user moved the button and the panel to
export const ASSISTANT_POSITION_STORAGE_KEY = "assistantPosition";

// the space kept between the button or the panel and the edges of the window
export const ASSISTANT_EDGE_MARGIN = 8;

// the server refuses longer messages
export const ASSISTANT_MAX_TEXT = 4000;

// suggestions shown before the first message, sent as the user's message when chosen
export const ASSISTANT_SUGGESTIONS = [
  "create_a_new_course",
  "schedule_classes",
  "check_conflicts",
  "under_enrolled_classes"
] as const;
