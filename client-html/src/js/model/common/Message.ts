/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Binding, MessageType, Recipients, SendMessageRequest } from "@api/model";

export interface AppMessage {
  message: string;
  success?: boolean;
  persist?: boolean;
}

export interface MessageExtended extends SendMessageRequest {
  selectAll?: boolean;
  bindings?: Binding[];
  messageType?: MessageType;
  recipientsCount?: number;
}

export interface MessageData {
  filtered?: {
    [K in MessageType]?: Recipients;
  };
  selected?: {
    [K in MessageType]?: Recipients;
  };
}
