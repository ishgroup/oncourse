import {SpecialPageItem, SpecialPages} from "../../../../../model";

export class SpecialPageSettingsState implements SpecialPages {
  rules: SpecialPageItem[] = [];
  refreshSettings?: boolean = false;
}
