import {RedirectSettings} from "../../../../../model";

export class RedirectSettingsState {
  rules: RedirectSettings[] = [];
  refreshSettings?: boolean = false;
}
