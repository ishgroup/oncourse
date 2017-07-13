import {PaymentResponse} from "../../../../model/checkout/payment/PaymentResponse";
import {CorporatePassResponse} from "../../../../model/checkout/payment/CorporatePassResponse";

export class State {
  value?: PaymentResponse;
  corporatePass?: CorporatePassResponse;
  currentTab: Tabs = Tabs.creditCard;
}

export enum Tabs {
  creditCard,
  corporatePass,
}