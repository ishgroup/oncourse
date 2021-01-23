import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetDocument } from "../../../js/containers/entities/documents/epics/EpicGetDocument";
import { GET_DOCUMENT_EDIT_FULFILLED, getDocument } from "../../../js/containers/entities/documents/actions";

describe("Get document epic tests", () => {
  it("EpicGetDocument should returns correct values", () => DefaultEpic({
    action: getDocument(1),
    epic: EpicGetDocument,
    processData: mockedApi => {
      const document = mockedApi.db.getDocument(1);
      return [
        {
          type: GET_DOCUMENT_EDIT_FULFILLED,
          payload: { document }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: document, name: document.name }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, document)
      ];
    }
  }));
});
