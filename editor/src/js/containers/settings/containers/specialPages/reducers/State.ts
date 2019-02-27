import {SpecialPageItem, SpecialPages, URLMatchRule} from "../../../../../model";

export class SpecialPageSettingsState implements SpecialPages {
  rules: SpecialPageItem[] = [];
  refreshSettings?: boolean = false;
}

export const URLMatchRuleKeys = Object.keys(URLMatchRule);
