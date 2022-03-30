import { ProductItemStatus, ProductType, Sale, Tag } from "@api/model";

export interface SaleState {
  items?: Sale[];
  pending?: boolean;
  selectedSaleStatus?: ProductItemStatus;
  selectedSaleType?: ProductType;
  courseClassTags?: Tag[];
  error?: boolean;
}
