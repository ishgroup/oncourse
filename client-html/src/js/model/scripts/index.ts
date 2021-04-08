
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
