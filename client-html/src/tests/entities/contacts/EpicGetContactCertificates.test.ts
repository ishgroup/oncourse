import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetContactCertificates } from "../../../js/containers/entities/contacts/epics/EpicGetContactCertificates";
import {
  GET_CONTACT_CERTIFICATES_FULFILLED,
  getContactCertificates
} from "../../../js/containers/entities/contacts/actions";

describe("Get contact certificates epic tests", () => {
  it("EpicGetContactCertificates should returns correct values", () => DefaultEpic({
    action: getContactCertificates(1),
    epic: EpicGetContactCertificates,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainCertificates();
      const certificates = response.rows.map(({ values, id }) => {
        const fullQualification = values[0] === "true";
        const qualificationLevel = values[6];
        const qualificationName = qualificationLevel ? `${qualificationLevel} ${values[2]}` : values[2];

        return {
          id: Number(id),
          fullQualification,
          nationalCode: values[1],
          qualificationName,
          certificateNumber: values[3],
          createdOn: values[4],
          lastPrintedOn: values[5]
        };
      });

      return [
        {
          type: GET_CONTACT_CERTIFICATES_FULFILLED,
          payload: { certificates }
        }
      ];
    }
  }));
});
