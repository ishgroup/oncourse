import {RedirectSettings} from "../../../../../model/settings/RedirectSettings";

export class RedirectSettingsState {
  rules: RedirectSettings[] = [];
  refreshSettings?: boolean = false;
}
