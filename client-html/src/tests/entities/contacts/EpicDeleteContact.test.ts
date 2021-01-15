import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteContact } from "../../../js/containers/entities/contacts/epics/EpicDeleteContact";
import { deleteContact } from "../../../js/containers/entities/contacts/actions";

describe("Delete contact epic tests", () => {
  it("EpicDeleteContact should returns correct values", () => DefaultEpic({
    action: deleteContact(1),
    epic: EpicDeleteContact,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Contact was deleted" }
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
