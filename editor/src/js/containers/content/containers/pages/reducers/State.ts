import {Page} from "../../../../../model";

export interface PageState extends Page {
  renderHtml?: string;
}

export class PagesState {
  items: PageState[] = [];
  editMode: boolean = true;
}
