import {Promotion} from "./Promotion";
import {RedeemVoucher} from "./RedeemVoucher";


export class SubmitCodeResponse {
  voucher: RedeemVoucher | null;
  promotion: Promotion | null;
}

