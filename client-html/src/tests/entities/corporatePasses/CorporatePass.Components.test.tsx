import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import CorporatePassEditView from "../../../js/containers/entities/corporatePasses/components/CorporatePassEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered CorporatePassEditView", () => {
  mockedEditView({
    entity: "CorporatePass",
    EditView: CorporatePassEditView,
    record: mockecApi => mockecApi.db.getCorporatePass(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#contactId").text()).toContain(initialValues.contactFullName);
      expect(wrapper.find("#password").text()).toContain(initialValues.password);
      expect(wrapper.find("#invoiceEmail").text()).toContain(initialValues.invoiceEmail);
      expect(wrapper.find("#expiryDate").text()).toContain(
        format(new Date(initialValues.expiryDate), III_DD_MMM_YYYY).toString()
      );
    }
  });
});
