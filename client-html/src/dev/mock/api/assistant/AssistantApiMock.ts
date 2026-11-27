/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { parseJson, promiseResolve } from "../../MockAdapter";

export const MOCK_CONVERSATION_ID = "mock-conversation";

export function AssistantApiMock() {
  this.api.onPost("/v1/assistant/message").reply(config => {
    const { conversationId, text } = parseJson(config.data);

    return promiseResolve(config, {
      conversationId: conversationId || MOCK_CONVERSATION_ID,
      text: `Mock reply to: ${text}`
    });
  });
}
