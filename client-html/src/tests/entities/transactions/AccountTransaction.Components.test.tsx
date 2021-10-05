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
      expect(wrapper.find("#amount input").val()).toContain(initialValues.amount);
      expect(wrapper.find("#transactionDate input").val()).toContain(
        format(new Date(initialValues.transactionDate), III_DD_MMM_YYYY).toString()
      );
    }
  });
});
