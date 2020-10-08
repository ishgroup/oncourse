import {ContentMode, Page} from "../../../../../model";

export interface PageState extends Page {
  renderHtml?: string;
  contentMode?: ContentMode;
}

export class PagesState {
  items: PageState[] = [];
  editMode: boolean = true;
  currentPage?: Page = null;
}
