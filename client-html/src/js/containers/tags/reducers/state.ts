import { Tag } from "@api/model";
import { CatalogItemType } from "../../../model/common/Catalog";

export interface TagsState {
  allTags: CatalogItemType[];
  entityTags: {
    [key: string]: Tag[];
  };
}
