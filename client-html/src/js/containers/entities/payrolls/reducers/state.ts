import { WagesToProcess } from "@api/model";

export interface PayrollsState {
  preparedWages?: WagesToProcess;
  generated?: boolean;
}
