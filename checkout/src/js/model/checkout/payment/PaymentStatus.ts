
import {createStringEnum} from "../../../common/utils/EnumUtils";

export const PaymentStatus = createStringEnum([
  'IN_PROGRESS',
  'FAILED',
  'SUCCESSFUL',
  'UNDEFINED',
  'SUCCESSFUL_BY_PASS',
  'SUCCESSFUL_WAITING_COURSES',
]);

export type PaymentStatus = keyof typeof PaymentStatus;
