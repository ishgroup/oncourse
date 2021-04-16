import {RedirectItem} from "../../../../../model";

export class RedirectSettingsState {
  rules: RedirectItem[] = [];
  refreshSettings?: boolean = false;
}
