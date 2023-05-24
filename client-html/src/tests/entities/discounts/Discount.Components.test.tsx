import { format } from "date-fns";
import DiscountEditView from "../../../js/containers/entities/discounts/components/DiscountEditView";
import { discountTypes } from "../../../js/containers/entities/discounts/components/DiscountGeneral";
import { formatCurrency } from "../../../js/common/utils/numbers/numbersNormalizing";
import { mockedEditView } from "../../common/MockedEditView.Components";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";

describe("Virtual rendered DiscountEditView", () => {
  mockedEditView({
    entity: "Discount",
    EditView: DiscountEditView,
    record: mockApi => mockApi.db.getDiscount(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        discountType: discountTypes.find(dt => dt.value === initialValues.discountType).label,
        discountPercent: initialValues.discountPercent * 100,
        rounding: initialValues.rounding,
        predictedStudentsPercentage: initialValues.predictedStudentsPercentage * 100,
        code: initialValues.code,
        validFrom: format(new Date(initialValues.validFrom), III_DD_MMM_YYYY),
        validTo: format(new Date(initialValues.validTo), III_DD_MMM_YYYY),
        availableFor: initialValues.availableFor,
        hideOnWeb: initialValues.hideOnWeb,
        description: initialValues.description,
        addByDefault: initialValues.addByDefault,
        minEnrolments: initialValues.minEnrolments,
        minValue: formatCurrency(initialValues.minValue, ""),
      });
    }
  });
});
