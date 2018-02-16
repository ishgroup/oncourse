import {MenuItem} from "../../../../../model";

interface MenuItemsState extends MenuItem {
  expanded?: boolean;
}

export class MenuState {
  items: MenuItemsState[] = [];
}
