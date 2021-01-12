import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicCreateArticleProduct } from "../../../js/containers/entities/articleProducts/epics/EpicCreateArticleProduct";
import { createArticleProduct } from "../../../js/containers/entities/articleProducts/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Create article product epic tests", () => {
  it("EpicCreateArticleProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createArticleProduct(mockedApi.db.mockedCreateArticleProduct()),
    epic: EpicCreateArticleProduct,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Product Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ArticleProduct" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
