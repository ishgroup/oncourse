import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_MERGE_CONTACTS_FULFILLED,
  getMergeContacts
} from "../../../js/containers/entities/contacts/actions";
import { EpicGetMergeContacts } from "../../../js/containers/entities/contacts/epics/EpicGetMergeContacts";

describe("Get merge contacts epic tests", () => {
  it("EpicGetMergeContacts should returns correct values", () => DefaultEpic({
    action: mockedApi => getMergeContacts(mockedApi.db.getContact("1"), mockedApi.db.getContact("2")),
    epic: EpicGetMergeContacts,
    processData: mockedApi => {
      const mergeData = mockedApi.db.getMergeContacts();
      const contactA = mockedApi.db.getContact("1");
      const contactB = mockedApi.db.getContact("2");

      return [
        {
          type: GET_MERGE_CONTACTS_FULFILLED
        },
        initialize("MergeContactsForm", {
          mergeData,
          mergeRequest: {
            contactA,
            contactB,
            data: Object.assign({}, ...mergeData.mergeLines.filter(l => l.a === l.b).map(l => ({ [l.key]: "A" })))
          }
        })
      ];
    }
  }));
});
