import { format } from "date-fns";
import TransactionsEditView from "../../../js/containers/entities/transactions/components/TransactionsEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import { formatCurrency } from "../../../js/common/utils/numbers/numbersNormalizing";

describe("Virtual rendered TransactionsEditView", () => {
  mockedEditView({
    entity: "AccountTransaction",
    EditView: TransactionsEditView,
    record: mockecApi => mockecApi.db.getAccountTransaction(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        amount: formatCurrency(initialValues.amount, ""),
        transactionDate: format(new Date(initialValues.transactionDate), III_DD_MMM_YYYY).toString(),
      });
    }
  });
});