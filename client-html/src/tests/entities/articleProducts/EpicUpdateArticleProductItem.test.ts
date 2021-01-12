import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_ARTICLE_PRODUCT_ITEM,
  updateArticleProduct
} from "../../../js/containers/entities/articleProducts/actions";
import { EpicUpdateArticleProductItem } from "../../../js/containers/entities/articleProducts/epics/EpicUpdateArticleProductItem";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";

describe("Update article product epic tests", () => {
  it("EpicUpdateArticleProductItem should returns correct values", () => DefaultEpic({
    action: updateArticleProduct("1", {
      "code": "qq50",
      "corporatePasses": [],
      "description": "test description",
      "feeExTax": 45.45,
      "id": 1,
      "incomeAccountId": 15,
      "relatedSellables": [],
      "name": "$50 Application",
      "taxId": 1,
      "totalFee": 50,
      "status": "Can be purchased in office & online"
    }),
    epic: EpicUpdateArticleProductItem,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Product Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ArticleProduct", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_ARTICLE_PRODUCT_ITEM,
        payload: "1"
      }
    ]
  }));
});
