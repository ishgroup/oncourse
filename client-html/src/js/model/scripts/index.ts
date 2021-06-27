
import { Script } from "@api/model";

export type ScriptComponentType = "Message" | "Script" | "Query" | "Report";

export type ScriptViewMode = "Cards" | "Code";

export interface ScriptComponent {
  type: ScriptComponentType;
  [key: string]: any;
}

export interface ScriptExtended extends Script {
  components: ScriptComponent[];
  imports: string[];
  body: string;
}
