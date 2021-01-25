import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CONTACT_ENROLMENTS_FULFILLED,
  getContactEnrolments
} from "../../../js/containers/entities/contacts/actions";
import {
  contactEnrolmentMap,
  EpicGetContactEnrolments
} from "../../../js/containers/entities/contacts/epics/EpicGetContactEnrolments";

describe("Get contact enrolments epic tests", () => {
  it("EpicGetContactEnrolments should returns correct values", () => DefaultEpic({
    action: getContactEnrolments(1),
    epic: EpicGetContactEnrolments,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainEnrolments({
        columns: "invoiceLine.invoice.invoiceNumber,createdOn,courseClass.uniqueCode,courseClass.course.name,displayStatus",
        search: "student.contact.id == 1"
      });
      const enrolments = response.rows.map(contactEnrolmentMap);

      return [
        {
          type: GET_CONTACT_ENROLMENTS_FULFILLED,
          payload: { enrolments }
        }
      ];
    }
  }));
});
