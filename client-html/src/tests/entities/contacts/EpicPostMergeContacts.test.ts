import { DefaultEpic } from "../../common/Default.Epic";
import {
  POST_MERGE_CONTACTS_FULFILLED,
  postMergeContacts
} from "../../../js/containers/entities/contacts/actions";
import { EpicPostMergeContacts } from "../../../js/containers/entities/contacts/epics/EpicPostMergeContacts";
import { openContactLink } from "../../../js/containers/entities/contacts/utils";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";

describe("Submit merge contacts epic tests", () => {
  it("EpicPostMergeContacts should returns correct values", () => DefaultEpic({
    action: mockedApi => postMergeContacts(mockedApi.db.submitMergeContacts()),
    epic: EpicPostMergeContacts,
    beforeProcess: () => {
      setTimeout(() => openContactLink(22), 2000);
    },
    processData: () => [
      {
        type: POST_MERGE_CONTACTS_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Contacts merged successfully" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Contact", listUpdate: true, savedID: 22 }
      }
    ]
  }));
});
