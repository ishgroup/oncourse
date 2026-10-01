import TestStore from "../../../js/constants/Store";
import { mockedAPI } from "../../TestEntry";
import { State } from "../../../js/reducers/state";
import { StateObservable } from "redux-observable";
import { of, Subject, lastValueFrom } from "rxjs";
import { toArray } from "rxjs/operators";
import { updateEntityRecord } from "../../../js/containers/entities/common/actions";
import { EpicUpdateEntityRecord } from "../../../js/containers/entities/common/epics/EpicUpdateEntityRecord";

const AVAILABLE_TAG_ID = 2;
const CHECKLIST_TASK_ID = 11;
const UNAVAILABLE_TAG_ID = 987654;

const entityTags = [{ id: 1, name: "Group", childrenCount: 1, childTags: [{ id: AVAILABLE_TAG_ID, name: "Tag", childrenCount: 0, childTags: [] }] }];

const checkedChecklists = [{
  active: false,
  tagBody: { id: 10, name: "Checklist", childTags: [] },
  children: [{ active: false, tagBody: { id: CHECKLIST_TASK_ID, name: "Task", childTags: [] }, children: [] }]
}];

const getState = () => ({
  ...TestStore.getState(),
  tags: { ...TestStore.getState().tags, entityTags: { Course: entityTags } },
  list: { ...TestStore.getState().list, checkedChecklists, uncheckedChecklists: [] }
} as State);

const getUpdateRequest = (id: number) => mockedAPI.api.history.put.find(request => request.url === `/v1/list/entity/course/${id}`);

const update = async (tags: number[]) => {
  const course = { ...mockedAPI.db.getCourse(1), tags };
  const state = new StateObservable(new Subject(), getState());

  await lastValueFrom(EpicUpdateEntityRecord(of(updateEntityRecord(course.id, "Course", course)), state, {}).pipe(toArray()));

  return getUpdateRequest(course.id);
};

describe("EpicUpdateEntityRecord tests", () => {
  beforeEach(() => {
    mockedAPI.api.history.put = [];
  });

  it("sends the record without the tag ids which are not available for the entity", async () => {
    const request = await update([AVAILABLE_TAG_ID, UNAVAILABLE_TAG_ID]);

    expect(JSON.parse(request.data).tags).toEqual([AVAILABLE_TAG_ID]);
  });

  it("keeps the completed checklist tasks in the request", async () => {
    const request = await update([AVAILABLE_TAG_ID, CHECKLIST_TASK_ID]);

    expect(JSON.parse(request.data).tags).toEqual([AVAILABLE_TAG_ID, CHECKLIST_TASK_ID]);
  });
});
