import { format } from "date-fns";
import AuditsEditView from "../../js/containers/audits/components/AuditsEditView";
import { III_DD_MMM_YYYY_HH_MM } from "../../js/common/utils/dates/format";
import { mockedEditView } from "../common/MockedEditView.Components";

describe("Virtual rendered AuditsEditView", () => {
  mockedEditView({
    entity: "Audit",
    EditView: AuditsEditView,
    record: mockedApi => mockedApi.db.getAudit(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        created: format(new Date(initialValues.created), III_DD_MMM_YYYY_HH_MM),
        entityIdentifier: initialValues.entityIdentifier,
        entityId: initialValues.entityId,
        action: initialValues.action,
        message: initialValues.message,
      });
    }
  });
});
