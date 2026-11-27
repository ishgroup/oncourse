/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export type AssistantRole = "user" | "assistant";

export interface AssistantChatMessage {
  id: string;
  role: AssistantRole;
  text: string;
}

export interface AssistantState {
  open: boolean;
  conversationId: string | null;
  messages: AssistantChatMessage[];
  pending: boolean;
  // the last error, shown in the panel
  error: string | null;
}
