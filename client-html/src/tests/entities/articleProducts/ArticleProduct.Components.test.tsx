import ArticleProductEditView from "../../../js/containers/entities/articleProducts/components/ArticleProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";
import { formatCurrency } from "../../../js/common/utils/numbers/numbersNormalizing";

describe("Virtual rendered ArticleProductEditView", () => {
  mockedEditView({
    entity: "ArticleProduct",
    EditView: ArticleProductEditView,
    record: mockedApi => mockedApi.db.getArticleProduct(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        code: initialValues.code,
        feeExTax: formatCurrency(initialValues.feeExTax, ""),
        totalFee: formatCurrency(initialValues.totalFee, ""),
        status: initialValues.status,
        dataCollectionRuleId: initialValues.dataCollectionRuleId.toString(),
        description: initialValues.description,
      });
    }
  });
});