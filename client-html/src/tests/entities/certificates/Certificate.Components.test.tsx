import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import CertificateEditView from "../../../js/containers/entities/certificates/components/CertificateEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered CertificateEditView", () => {
  mockedEditView({
    entity: "Certificate",
    EditView: CertificateEditView,
    record: mockecApi => mockecApi.db.getCertificate(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        studentContactId: initialValues.studentName,
        nationalCode: initialValues.nationalCode,
        title: initialValues.title,
        awardedOn: format(new Date(initialValues.awardedOn), III_DD_MMM_YYYY).toString(),
        issuedOn: format(new Date(initialValues.issuedOn), III_DD_MMM_YYYY).toString(),
        expiryDate: format(new Date(initialValues.expiryDate), III_DD_MMM_YYYY).toString(),
        privateNotes: initialValues.privateNotes,
        publicNotes: initialValues.publicNotes,
      });
    }
  });
});
