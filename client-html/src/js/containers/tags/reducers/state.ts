import { Tag } from "@api/model";

export interface TagsState {
  allTags: Tag[];
  entityTags: {
    [key: string]: Tag[];
  };
}
