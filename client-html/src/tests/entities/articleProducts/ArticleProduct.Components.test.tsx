import ArticleProductEditView from "../../../js/containers/entities/articleProducts/components/ArticleProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ArticleProductEditView", () => {
  mockedEditView({
    entity: "ArticleProduct",
    EditView: ArticleProductEditView,
    record: mockecApi => mockecApi.db.getArticleProduct(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#code").text()).toContain(initialValues.code);
      expect(wrapper.find("#description").text()).toContain(initialValues.description);
      expect(wrapper.find("#incomeAccountId").text()).toContain("No value");
      expect(wrapper.find("#feeExTax").text()).toContain(initialValues.feeExTax);
      expect(wrapper.find("#totalFee").text()).toContain(initialValues.totalFee);
      expect(wrapper.find("#taxId").text()).toContain("No value");
      expect(wrapper.find("#status").text()).toContain(initialValues.status);
    }
  });
});
