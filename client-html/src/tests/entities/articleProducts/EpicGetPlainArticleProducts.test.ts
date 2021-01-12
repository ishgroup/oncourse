import { ArticleProduct } from "@api/model";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_PLAIN_ARTICLE_PRODUCT_ITEMS_FULFILLED,
  getPlainArticleProducts
} from "../../../js/containers/entities/articleProducts/actions";
import {
  defaultArticleProductMap,
  EpicGetPlainArticleProducts
} from "../../../js/containers/entities/articleProducts/epics/EpicGetPlainArticleProducts";
import { getCustomColumnsMap } from "../../../js/common/utils/common";

describe("Get plain article products epic tests", () => {
  it("EpicGetPlainArticleProducts should returns correct values", () => DefaultEpic({
    action: getPlainArticleProducts(),
    epic: EpicGetPlainArticleProducts,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainArticleProductList();
      const rows = response.rows;
      const columns = null;
      const offset = 0;
      const pageSize = 20;
      const items: ArticleProduct[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultArticleProductMap);
      return [
        {
          type: GET_PLAIN_ARTICLE_PRODUCT_ITEMS_FULFILLED,
          payload: { items, offset, pageSize }
        }
      ];
    }
  }));
});
