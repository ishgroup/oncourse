import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_APPLICATION_ITEM_FULFILLED,
  updateApplication
} from "../../../js/containers/entities/applications/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateApplicationItem } from "../../../js/containers/entities/applications/epics/EpicUpdateApplicationItem";
import { FETCH_SUCCESS } from "../../../js/common/actions";

describe("Update application epic tests", () => {
  it("EpicUpdateApplicationItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateApplication("1", {
      id: 1,
      source: "office",
      studentName: "studentName 1",
      courseName: "courseName 1",
      createdOn: "2021-01-09T11:42:23.549Z",
      applicationDate: "2021-01-09T11:42:23.549Z",
      enrolBy: "2021-01-09T11:42:23.549Z",
      modifiedOn: "2021-01-09T11:42:23.549Z",
      status: "Withdrawn",
      feeOverride: 1,
      contactId: 1,
      courseId: 1,
      createdBy: null,
      customFields: {},
      documents: [],
      reason: "Your application has been approved as being eligible to pay the second qualification fee.",
      tags: [mockedApi.db.getTag(1)]
    }),
    epic: EpicUpdateApplicationItem,
    processData: () => [
      {
        type: UPDATE_APPLICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Application Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Application", listUpdate: true, savedID: "1" }
      }
    ]
  }));
});
