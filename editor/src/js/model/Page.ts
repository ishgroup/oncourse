import {Url} from "./PageUrl";

export class Page {
  id: number;
  title: string;
  theme: string;
  layout: string;
  html: string;
  urls: Url[];
  visible: boolean;
}

