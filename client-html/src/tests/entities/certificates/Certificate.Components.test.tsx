import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import CertificateEditView from "../../../js/containers/entities/certificates/components/CertificateEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered CertificateEditView", () => {
  mockedEditView({
    entity: "Certificate",
    EditView: CertificateEditView,
    record: mockecApi => mockecApi.db.getCertificate(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#studentContactId input").val()).toContain(initialValues.studentName);
      expect(wrapper.find("#nationalCode input").val()).toContain(initialValues.nationalCode);
      expect(wrapper.find("#title input").val()).toContain(initialValues.title);
      expect(wrapper.find("#publicNotes input").val()).toContain("No value");
      expect(wrapper.find("#awardedOn input").val()).toContain(
        format(new Date(initialValues.awardedOn), III_DD_MMM_YYYY).toString()
      );
      expect(wrapper.find("#issuedOn input").val()).toContain(
        format(new Date(initialValues.issuedOn), III_DD_MMM_YYYY).toString()
      );
      expect(wrapper.find("#expiryDate input").val()).toContain("No value");
      expect(wrapper.find("#privateNotes input").val()).toContain("No value");
    }
  });
});
