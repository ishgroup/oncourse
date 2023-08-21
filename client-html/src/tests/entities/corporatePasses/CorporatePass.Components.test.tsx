import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "ish-ui";
import CorporatePassEditView from "../../../js/containers/entities/corporatePasses/components/CorporatePassEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered CorporatePassEditView", () => {
  mockedEditView({
    entity: "CorporatePass",
    EditView: CorporatePassEditView,
    record: mockecApi => mockecApi.db.getCorporatePass(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        contactId: initialValues.contactFullName,
        password: initialValues.password,
        invoiceEmail: initialValues.invoiceEmail,
        expiryDate: format(new Date(initialValues.expiryDate), III_DD_MMM_YYYY).toString(),
      });
    }
  });
});
