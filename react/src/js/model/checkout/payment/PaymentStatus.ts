
import {createStringEnum} from "common/utils/EnumUtils";

export const PaymentStatus = createStringEnum([
      'IN_PROGRESS',
      'FAIL',
      'SUCCESSFUL',
      'UNDEFINED'
]);

export type PaymentStatus = keyof typeof PaymentStatus;


