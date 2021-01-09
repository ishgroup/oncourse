import { format } from "date-fns";
import AuditsEditView from "../../js/containers/audits/components/AuditsEditView";
import { III_DD_MMM_YYYY_HH_MM } from "../../js/common/utils/dates/format";
import { mockedEditView } from "../common/MockedEditView.Components";

describe("Virtual rendered AuditsEditView", () => {
  it("Audit components should render with given values", () => {
    mockedEditView({
      entity: "Audit",
      EditView: AuditsEditView,
      record: mockedApi => mockedApi.db.getAudit(1),
      render: (wrapper, initialValues) => {
        expect(wrapper.find("#created").text()).toContain(format(new Date(initialValues.created), III_DD_MMM_YYYY_HH_MM));
        expect(wrapper.find("#entityIdentifier").text()).toContain(initialValues.entityIdentifier);
        expect(wrapper.find("#entityId").text()).toContain(initialValues.entityId);
        expect(wrapper.find("#action").text()).toContain(initialValues.action);
        expect(wrapper.find("#message").text()).toContain(initialValues.message);
      }
    });
  });
});
