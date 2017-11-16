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
   * Unique identifier of for theme
   */
  layoutId?: number;
  schema?: ThemeSchema;
}

