import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateContact } from "../../../js/containers/entities/contacts/epics/EpicCreateContact";
import { createContact } from "../../../js/containers/entities/contacts/actions";

describe("Create contact epic tests", () => {
  it("EpicCreateContact should returns correct values", () => DefaultEpic({
    action: mockedApi => createContact(mockedApi.db.createNewContact()),
    epic: EpicCreateContact,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Contact created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Contact" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
