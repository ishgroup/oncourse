import {Url} from "../PageUrl";

export class SavePageRequest {
  id: number;
  title: string;
  themeId: number;
  html: string;
  urls: Url[];
  visible: boolean;
}
