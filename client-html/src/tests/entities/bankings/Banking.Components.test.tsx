import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import BankingEditView from "../../../js/containers/entities/bankings/components/BankingEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered BankingEditView", () => {
  mockedEditView({
    entity: "Banking",
    EditView: BankingEditView,
    record: mockecApi => mockecApi.db.getBanking(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("div.textField").text()).toContain(initialValues.adminSite);
      expect(wrapper.find("#settlementDate").text()).toContain(
        format(new Date(initialValues.settlementDate), III_DD_MMM_YYYY).toString()
      );
    }
  });
});
