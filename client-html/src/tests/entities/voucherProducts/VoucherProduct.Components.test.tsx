import VoucherProductEditView from "../../../js/containers/entities/voucherProducts/components/VoucherProductEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered VoucherProductEditView", () => {
  mockedEditView({
    entity: "VoucherProduct",
    EditView: VoucherProductEditView,
    record: mockecApi => mockecApi.db.getVoucherProduct(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        code: initialValues.code,
        expiryDays: initialValues.expiryDays,
        maxCoursesRedemption: initialValues.maxCoursesRedemption,
        feeExTax: initialValues.feeExTax.toString(),
        status: initialValues.status,
        dataCollectionRuleId: initialValues.dataCollectionRuleId.toString(),
        description: initialValues.description,
      });
    }
  });
});
