import TransactionsEditView from "../../../js/containers/entities/transactions/components/TransactionsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";
import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";

describe("Virtual rendered TransactionsEditView", () => {
  mockedEditView({
    entity: "AccountTransaction",
    EditView: TransactionsEditView,
    record: mockecApi => mockecApi.db.getAccountTransaction(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#fromAccount").text()).toContain("No value");
      expect(wrapper.find("#amount").text()).toContain(initialValues.amount);

      expect(wrapper.find("#transactionDate").text()).toContain(
        format(new Date(initialValues.transactionDate), III_DD_MMM_YYYY).toString()
      );
    }
  });
});
