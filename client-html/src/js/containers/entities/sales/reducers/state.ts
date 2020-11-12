import { ProductItemStatus, ProductType, Sale, Tag } from "@api/model";

export interface SaleState {
  items?: Sale[];
  courseClassItems?: Sale[];
  pending?: boolean;
  selectedSaleStatus?: ProductItemStatus;
  selectedSaleType?: ProductType;
  courseClassTags?: Tag[];
}
