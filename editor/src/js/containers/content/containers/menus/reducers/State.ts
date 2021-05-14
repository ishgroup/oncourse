import {MenuItem} from "../../../../../model";

interface MenuItemsState extends MenuItem {
  expanded?: boolean;
  dragId?: number;
}

export class MenuState {
  items: MenuItemsState[] = [];
}
