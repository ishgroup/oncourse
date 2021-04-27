import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import ApplicationEditView from "../../../js/containers/entities/applications/components/ApplicationEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered ApplicationEditView", () => {
  mockedEditView({
    entity: "Application",
    EditView: ApplicationEditView,
    record: mockecApi => mockecApi.db.getApplication(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#contactId").text()).toContain(initialValues.contactId);
      expect(wrapper.find("#courseId").text()).toContain(initialValues.courseId);
      expect(wrapper.find("#tags").text()).toContain(initialValues.tags[0].name);
      expect(wrapper.find("#applicationDate").text()).toContain(
        format(new Date(initialValues.applicationDate), III_DD_MMM_YYYY).toString()
      );
      expect(wrapper.find("#source").text()).toContain(initialValues.source);
      expect(wrapper.find("#status").text()).toContain(initialValues.status);
      expect(wrapper.find("#feeOverride").text()).toContain(initialValues.feeOverride);
      expect(wrapper.find("#enrolBy").text()).toContain(format(new Date(initialValues.enrolBy), III_DD_MMM_YYYY).toString());
      expect(wrapper.find("#createdBy").text()).toContain(initialValues.createdBy || "No value");
      expect(wrapper.find("#reason").text()).toContain(initialValues.reason);
    }
  });
});
