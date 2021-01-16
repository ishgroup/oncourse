import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateContact } from "../../../js/containers/entities/contacts/epics/EpicUpdateContact";
import { GET_CONTACT, UPDATE_CONTACT_FULFILLED, updateContact } from "../../../js/containers/entities/contacts/actions";

describe("Update contact epic tests", () => {
  it("EpicUpdateContact should returns correct values", () => DefaultEpic({
    action: mockedApi => updateContact("2", mockedApi.db.createNewContact(2)),
    epic: EpicUpdateContact,
    processData: () => [
      {
        type: UPDATE_CONTACT_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Contact was updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Contact", listUpdate: true, savedID: "2" }
      },
      {
        type: GET_CONTACT,
        payload: "2"
      }
    ]
  }));
});
