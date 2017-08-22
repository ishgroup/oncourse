
import {createStringEnum} from "../../../common/utils/EnumUtils";

export const PaymentStatus = createStringEnum([
  'IN_PROGRESS',
  'FAILED',
  'SUCCESSFUL',
  'UNDEFINED',
  'SUCCESSFUL_BY_PASS',
]);

export type PaymentStatus = keyof typeof PaymentStatus;
