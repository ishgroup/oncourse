import {ThemeSchema} from "./ThemeSchema";

export class Theme {

  /**
   * Unique identifier of theme
   */
  id?: number;

  /**
   * title of theme
   */
  title?: string;

  /**
   * Layout id which is used for this theme
   */
  layoutId?: number;
  schema?: ThemeSchema;
}

