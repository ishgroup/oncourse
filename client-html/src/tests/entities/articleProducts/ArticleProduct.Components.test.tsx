import ArticleProductEditView from "../../../js/containers/entities/articleProducts/components/ArticleProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ArticleProductEditView", () => {
  mockedEditView({
    entity: "ArticleProduct",
    EditView: ArticleProductEditView,
    record: mockedApi => mockedApi.db.getArticleProduct(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        code: initialValues.code,
        incomeAccountId: initialValues.incomeAccountId.toString(),
        feeExTax: initialValues.feeExTax.toString(),
        totalFee: initialValues.totalFee.toString(),
        status: initialValues.status,
        dataCollectionRuleId: initialValues.dataCollectionRuleId.toString(),
        description: initialValues.description,
      });
    }
  });
});
