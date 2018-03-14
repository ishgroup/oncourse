import {CheckoutSettings} from "../../../../../model";

export class CheckoutSettingsState {
  allowCreateContactOnEnrol?: boolean;
  allowCreateContactOnWaitingList?: boolean;
  allowCreateContactOnMailingList?: boolean;
  collectParentDetails?: boolean;
  enrolmentMinAge?: number;
  refreshSettings?: boolean = false;
}
