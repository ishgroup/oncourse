
import {createStringEnum} from "common/utils/EnumUtils";

export const PaymentStatus = createStringEnum([
      'in progress',
      'failed',
      'successful',
      'error'
]);

export type PaymentStatus = keyof typeof PaymentStatus;


