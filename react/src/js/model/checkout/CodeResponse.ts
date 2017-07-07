import {RedeemVoucher} from "./../checkout/RedeemVoucher";
import {Promotion} from "./../web/Promotion";

export class CodeResponse {
  promotiom?: Promotion;
  voucher?: RedeemVoucher;
}

