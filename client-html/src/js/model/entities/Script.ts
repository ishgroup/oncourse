/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Script } from "@api/model";

export type ScriptComponentType = "Message" | "Script" | "Query" | "Report";

export interface ScriptComponent {
  type: ScriptComponentType;
  [key: string]: any;
}

export interface ScriptExtended extends Script {
  components: ScriptComponent[];
  imports: string[];
  body: string;
}
