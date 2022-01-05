import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { getArticleProduct } from "../../../js/containers/entities/articleProducts/actions";
import { EpicGetArticleProduct } from "../../../js/containers/entities/articleProducts/epics/EpicGetArticleProduct";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get article product epic tests", () => {
  it("EpicGetArticleProduct should returns correct values", () => DefaultEpic({
    action: getArticleProduct("1"),
    epic: EpicGetArticleProduct,
    processData: mockedApi => {
      const articleProduct = mockedApi.db.getArticleProduct(1);
      return [
        getNoteItems("ArticleProduct", articleProduct.id, LIST_EDIT_VIEW_FORM_NAME),
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: articleProduct, name: articleProduct.name }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, articleProduct)
      ];
    }
  }));
});
