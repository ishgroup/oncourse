import { getRecordItemBeforeUpdate } from "../../../js/containers/entities/common/utils";
import { State } from "../../../js/reducers/state";

const getState = (entityTags: any, checklists: any[] = []) => ({
  tags: { entityTags },
  list: { checkedChecklists: checklists, uncheckedChecklists: [] }
} as State);

const entityTags = [{ id: 1, childrenCount: 1, childTags: [{ id: 2, childrenCount: 0, childTags: [] }] }];

const checklistMenu = (...ids: number[]) => ({
  active: false,
  tagBody: { id: ids[0], childTags: [] },
  children: ids.slice(1).map(id => ({ active: false, tagBody: { id, childTags: [] }, children: [] }))
});

describe("getRecordItemBeforeUpdate tests", () => {
  it("removes tag ids which are not available for the entity", () => {
    const item = { id: 5, name: "Course", tags: [1, 2, 99] };

    expect(getRecordItemBeforeUpdate(item, "Course", getState({ Course: entityTags }))).toEqual({
      id: 5,
      name: "Course",
      tags: [1, 2]
    });
  });

  it("keeps completed checklist tasks and removes their checklist", () => {
    const item = { id: 5, name: "Course", tags: [1, 10, 11, 99] };

    expect(getRecordItemBeforeUpdate(item, "Course", getState({ Course: entityTags }, [checklistMenu(10, 11)]))).toEqual({
      id: 5,
      name: "Course",
      tags: [1, 11]
    });
  });

  it("removes ids of tag groups, they can not be assigned to a record", () => {
    const item = { id: 5, name: "Course", tags: [1, 2] };

    expect(getRecordItemBeforeUpdate(item, "Course", getState({ Course: [{ id: 3, childrenCount: 0, childTags: [] }] }))).toEqual({
      id: 5,
      name: "Course",
      tags: []
    });
  });

  it("keeps the tags of the record when the tags of the entity are not loaded", () => {
    const item = { id: 5, name: "Course", tags: [99] };

    expect(getRecordItemBeforeUpdate(item, "Course", getState({}))).toBe(item);
  });

  it("does not touch the tags of entities without loaded tags", () => {
    const item = { id: 5, tags: [99] };

    expect(getRecordItemBeforeUpdate(item, "Sale", getState({ Course: entityTags }))).toBe(item);
  });
});
