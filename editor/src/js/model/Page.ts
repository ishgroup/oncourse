import {Url} from "./PageUrl";

export class Page {
  id: number;
  title: string;
  themeId: number;
  html: string;
  urls: Url[];
  visible: boolean;
}

