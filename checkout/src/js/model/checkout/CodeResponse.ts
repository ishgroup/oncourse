import {RedeemVoucher} from "./RedeemVoucher";
import {Promotion} from "../web/Promotion";

export class CodeResponse {
  promotion?: Promotion;
  voucher?: RedeemVoucher;
}

