import {Errors} from "./menuitem/Errors";

export class MenuItem {

  /**
   * Unique identifier of menu item
   */
  id?: number;

  /**
   * title of menu item
   */
  title?: string;

  /**
   * url for menu link
   */
  url?: string;
  children?: MenuItem[];
  errors?: Errors;
}

