import {PaymentResponse} from "../../../../model/checkout/payment/PaymentResponse";
import {CorporatePass} from "../../../../model/checkout/corporatepass/CorporatePass";

export class State {
  value?: PaymentResponse;
  corporatePass?: CorporatePass;
  corporatePassAvailable: boolean = false;
  currentTab: Tabs = Tabs.creditCard;
}

export enum Tabs {
  creditCard,
  corporatePass,
}