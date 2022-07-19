import { format } from "date-fns";
import DiscountEditView from "../../../js/containers/entities/discounts/components/DiscountEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";

describe("Virtual rendered DiscountEditView", () => {
  mockedEditView({
    entity: "Discount",
    EditView: DiscountEditView,
    record: mockecApi => mockecApi.db.getDiscount(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        discountType: initialValues.discountType,
        discountPercent: initialValues.discountPercent * 100,
        rounding: initialValues.rounding,
        predictedStudentsPercentage: initialValues.predictedStudentsPercentage * 100,
        code: initialValues.code,
        cosAccount: initialValues.cosAccount.toString(),
        validFrom: format(new Date(initialValues.validFrom), III_DD_MMM_YYYY),
        validTo: format(new Date(initialValues.validTo), III_DD_MMM_YYYY),
        availableOnWeb: initialValues.availableOnWeb,
        hideOnWeb: initialValues.hideOnWeb,
        description: initialValues.description,
        addByDefault: initialValues.addByDefault,
        minEnrolments: initialValues.minEnrolments,
        minValue: initialValues.minValue.toString(),
      });
    }
  });
});
