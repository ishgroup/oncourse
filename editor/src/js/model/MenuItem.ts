
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

  /**
   * Describes reason why menu item cannot be saved. Comes on 400 (Bad request) response
   */
  error?: string;

  /**
   * Any alerts for user. Do not affect on menus saving. Comes on 200 (Success) response
   */
  warning?: string;
}

