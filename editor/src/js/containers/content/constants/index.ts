import {ContentMode} from "../../../model";

export const CONTENT_MODES: {
  id: ContentMode,
  title: string
}[] = [
  {id: "textile", title: "legacy"},
  {id: "html", title: "html"},
  {id: "md", title: "markdown"}
];

export const DEFAULT_CONTENT_MODE_ID = CONTENT_MODES[0].id;
