import {PageUrl} from "./PageUrl";

export class Page {

  /**
   * unique id of the page
   */
  id?: number;

  /**
   * title of the page
   */
  title?: string;

  /**
   * theme Id for the page
   */
  themeId?: number;

  /**
   * Html source of the page
   */
  html?: string;

  /**
   * Has page visible
   */
  visible?: boolean;
  urls?: PageUrl[];
}

