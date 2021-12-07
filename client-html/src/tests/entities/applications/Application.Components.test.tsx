import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import ApplicationEditView from "../../../js/containers/entities/applications/components/ApplicationEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";


// TODO Enable test on fix

describe("Virtual rendered ApplicationEditView", () => {
  mockedEditView({
    entity: "Application",
    EditView: ApplicationEditView,
    record: mockecApi => mockecApi.db.getApplication(1),
    render: (wrapper, initialValues) => {
      // expect(wrapper.find("#contactId input").val()).toContain(initialValues.contactId);
      // expect(wrapper.find("#courseId input").val()).toContain(initialValues.courseId);
      // expect(wrapper.find("#applicationDate input").val()).toContain(
      //   format(new Date(initialValues.applicationDate), III_DD_MMM_YYYY).toString()
      // );
      // expect(wrapper.find("#source input").val()).toContain(initialValues.source);
      // expect(wrapper.find("#status input").val()).toContain(initialValues.status);
      // expect(wrapper.find("#feeOverride input").val()).toContain(initialValues.feeOverride);
      // expect(wrapper.find("#enrolBy input").val()).toContain(format(new Date(initialValues.enrolBy), III_DD_MMM_YYYY).toString());
      // expect(wrapper.find("#createdBy input").val()).toContain(initialValues.createdBy || "");
      // expect(wrapper.find("#reason textarea").val()).toContain(initialValues.reason);
    }
  });
});
