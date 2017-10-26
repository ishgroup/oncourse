import {PaymentResponse, CorporatePass} from "../../../../model";

export class State {
  value?: PaymentResponse;
  corporatePass?: CorporatePass = {};
  currentTab: Tabs = Tabs.creditCard;
  fetching?: boolean;
  resetOnInit?: boolean = false;
  corporateTabAvailable?: boolean;
  result: any = [];
}

export enum Tabs {
  creditCard,
  corporatePass,
}
