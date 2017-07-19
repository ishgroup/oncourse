import {PaymentResponse, CorporatePass} from "../../../../model";

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