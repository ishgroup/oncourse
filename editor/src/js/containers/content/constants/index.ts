import {ContentMode} from "../../../model";

export const CONTENT_MODES: {
  id: ContentMode,
  title: string,
}[] = [
  {id: "md", title: "rich text"},
  {id: "textile", title: "legacy"},
  {id: "html", title: "advanced (html)"},
];

export const DEFAULT_CONTENT_MODE_ID = CONTENT_MODES[1].id;
