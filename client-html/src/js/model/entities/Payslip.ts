import { PayLine } from "@api/model";

export interface PayLineWithDefer extends PayLine {
  deferred: boolean;
}
