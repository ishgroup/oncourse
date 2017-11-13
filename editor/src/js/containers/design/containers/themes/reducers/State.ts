import {Theme} from "../../../../../model";
import {Layout} from "../../../../../model/Layout";

export class ThemesState {
  items: Theme[] = [];
  fetching: boolean = false;
  layouts: Layout[] = [];
}
