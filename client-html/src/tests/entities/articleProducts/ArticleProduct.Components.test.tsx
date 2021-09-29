import ArticleProductEditView from "../../../js/containers/entities/articleProducts/components/ArticleProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ArticleProductEditView", () => {
  mockedEditView({
    entity: "ArticleProduct",
    EditView: ArticleProductEditView,
    record: mockecApi => mockecApi.db.getArticleProduct(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#code input").val()).toContain(initialValues.code);
      expect(wrapper.find("#description").text()).toContain(initialValues.description);
      expect(wrapper.find("#feeExTax input").val()).toContain(initialValues.feeExTax);
      expect(wrapper.find("#totalFee input").val()).toContain(initialValues.totalFee);
      expect(wrapper.find("#status input").val()).toContain(initialValues.status);
    }
  });
});
