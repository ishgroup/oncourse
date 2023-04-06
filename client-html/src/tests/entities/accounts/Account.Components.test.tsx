import { mockedEditView } from "../../common/MockedEditView.Components";
import AccountsEditView from "../../../js/containers/entities/accounts/components/AccountsEditView";

describe("Virtual rendered AccountsEditView", () => {
  mockedEditView({
    entity: "Account",
    EditView: AccountsEditView,
    record: mockecApi => mockecApi.db.getAccount(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        accountCode: initialValues.accountCode,
        type: initialValues.type.toString(),
        description: initialValues.description
      });
    }
  });
});
