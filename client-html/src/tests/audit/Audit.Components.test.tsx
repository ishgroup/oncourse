import { format } from "date-fns";
import AuditsEditView from "../../js/containers/audits/components/AuditsEditView";
import { III_DD_MMM_YYYY_HH_MM } from "../../js/common/utils/dates/format";
import { mockedEditView } from "../common/MockedEditView.Components";

describe("Virtual rendered AuditsEditView", () => {
  mockedEditView({
    entity: "Audit",
    EditView: AuditsEditView,
    record: mockedApi => mockedApi.db.getAudit(1),
    render: ({ screen, initialValues }) => {
      expect(screen.getByLabelText('Date and time').value).toBe(format(new Date(initialValues.created), III_DD_MMM_YYYY_HH_MM));
      expect(screen.getByLabelText("Entity name").value).toBe(initialValues.entityIdentifier);
      expect(screen.getByLabelText("Entity ID").value).toBe(initialValues.entityId);
      expect(screen.getByLabelText("Action").value).toBe(initialValues.action);
      expect(screen.getByLabelText("Message").value).toBe(initialValues.message);
    }
  });
});
