import MembershipProductEditView
  from "../../../js/containers/entities/membershipProducts/components/MembershipProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";
import { formatCurrency } from "ish-ui";

describe("Virtual rendered MembershipProductEditView", () => {
  mockedEditView({
    entity: "MembershipProduct",
    EditView: MembershipProductEditView,
    record: mockecApi => mockecApi.db.getMembershipProduct(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        code: initialValues.code,
        description: initialValues.description,
        feeExTax: formatCurrency(initialValues.feeExTax, ""),
        totalFee: formatCurrency(initialValues.totalFee, ""),
        status: initialValues.status,
        dataCollectionRuleId: initialValues.dataCollectionRuleId.toString(),
      });
    }
  });
});