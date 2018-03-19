import {CheckoutSettings} from "../../../../../model";

export class CheckoutSettingsState {
  allowCreateContactOnEnrol?: boolean;
  allowCreateContactOnWaitingList?: boolean;
  allowCreateContactOnMailingList?: boolean;
  collectParentDetails?: boolean;
  enrolmentMinAge?: number;
  contactAgeWhenNeedParent?: number;
  refreshSettings?: boolean = false;
}
