import {PageUrl} from "./PageUrl";

export class Page {

  /**
   * unique id of the page
   */
  id?: number;

  /**
   * serial number of the page. Can be used to proceed to page by technical path /page/10
   */
  serialNumber?: number;

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
  urls?: PageUrl[];
}

