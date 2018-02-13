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
   * theme id used for the page
   */
  themeId?: number;

  /**
   * Rich text content
   */
  content?: string;

  /**
   * Is the page visible
   */
  visible?: boolean;

  /**
   * Indicates that page is a special system page and content is not editable
   */
  reservedURL?: boolean;
  urls?: PageUrl[];
}

