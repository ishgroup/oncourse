import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "ish-ui";
import BankingEditView from "../../../js/containers/entities/bankings/components/BankingEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered BankingEditView", () => {
  mockedEditView({
    entity: "Banking",
    EditView: BankingEditView,
    record: mockecApi => mockecApi.db.getBanking(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        settlementDate: format(new Date(initialValues.settlementDate), III_DD_MMM_YYYY).toString(),
      });
    }
  });
});
