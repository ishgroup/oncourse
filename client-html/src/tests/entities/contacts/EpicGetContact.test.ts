import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetContact, formatContactRelationIds } from "../../../js/containers/entities/contacts/epics/EpicGetContact";
import { GET_CONTACT_FULFILLED, getContact } from "../../../js/containers/entities/contacts/actions";
import { getUserPreferences } from "../../../js/common/actions";
import { AVETMIS_ID_KEY, REPLICATION_ENABLED_KEY } from "../../../js/constants/Config";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get contact epic tests", () => {
  it("EpicGetContact should returns correct values", () => DefaultEpic({
    action: getContact(1),
    epic: EpicGetContact,
    processData: mockedApi => {
      const contact = mockedApi.db.getContact(1);
      if (contact.student && contact.student.usiStatus) {
        const status = contact.student.usiStatus;

        if (status === "Exemption") {
          contact.student.usi = "INDIV";
        }

        if (status === "International") {
          contact.student.usi = "INTOFF";
        }
      }

      if (contact.relations && contact.relations.length) {
        contact.relations = formatContactRelationIds(contact.relations);
      }

      if (contact.financialData.length > 0) {
        contact.financialData = contact.financialData.sort((a, b) => (new Date(b.createdOn) > new Date(a.createdOn) ? 1 : -1));
      }

      return [
        {
          type: GET_CONTACT_FULFILLED,
          payload: { contact }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: contact, name: contact.lastName }
        },
        getUserPreferences([REPLICATION_ENABLED_KEY, AVETMIS_ID_KEY]),
        getNoteItems("Contact", 1 as any, LIST_EDIT_VIEW_FORM_NAME),
        initialize(LIST_EDIT_VIEW_FORM_NAME, contact)
      ];
    }
  }));
});
