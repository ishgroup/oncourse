import { GetCustomFieldTypes } from "../customFieldTypes/GetCustomFieldTypes.Epic";

describe("Get waitingList custom field types epic tests", () => {
  it("GetWaitingListCustomFieldTypes should return correct actions", () => {
    return GetCustomFieldTypes("WaitingList");
  });
});
