import {PageUrl} from "./PageUrl";

export class Page {

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

  /**
   * Indicates that page is system, content is not editable
   */
  reservedURL?: boolean;
  urls?: PageUrl[];
}

