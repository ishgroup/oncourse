import {Redirects} from "../../../../../model";

export class RedirectSettingsState {
  rules: Redirects[] = [];
  refreshSettings?: boolean = false;
}
