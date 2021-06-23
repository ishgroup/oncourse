import {Contact} from "./../web/Contact";

export class RedeemVoucher {
  id: string;
  enabled: boolean;
  payer: Contact;
  name: string;
  code: string;
}
