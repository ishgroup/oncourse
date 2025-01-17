import { Tag } from "@api/model";
import { CatalogItemType } from "../../../model/common/Catalog";

export interface TagsState {
  allTags: CatalogItemType[];
  allChecklists: CatalogItemType[];
  entityTags: {
    [key: string]: Tag[];
  };
  entitySpecialTags: {
    [key: string]: Tag[];
  };
}
