import {PageUrl} from "./PageUrl";

export class Page {

  /**
   * unique id of the page
   */
  id?: number;
  
  /**
   * page number
   */
  number?: number;

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
  content?: string;

  /**
   * Has page visible
   */
  visible?: boolean;
  urls?: PageUrl[];
}

