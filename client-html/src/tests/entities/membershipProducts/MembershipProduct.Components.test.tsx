import MembershipProductEditView from "../../../js/containers/entities/membershipProducts/components/MembershipProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe.skip("Virtual rendered MembershipProductEditView", () => {
  mockedEditView({
    entity: "MembershipProduct",
    EditView: MembershipProductEditView,
    record: mockecApi => mockecApi.db.getMembershipProduct(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        code: initialValues.code,
        description: initialValues.description,
        feeExTax: initialValues.feeExTax.toString(),
        totalFee: initialValues.totalFee.toString(),
        status: initialValues.status,
        dataCollectionRuleId: initialValues.dataCollectionRuleId.toString(),
      });
    }
  });
});
