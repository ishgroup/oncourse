import { ArticleProduct } from "@api/model";

export interface ArticleProductState {
  items?: ArticleProduct[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}
