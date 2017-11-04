import {Page} from "../../../../../model";

export class PageState extends Page {
  renderHtml?: string;
}

export class PagesState {
  items: PageState[] = [];
  editMode: boolean = true;
  fetching: boolean = false;
}
