import {
  applyTagsChange,
  getAssignableTagIds,
  getAvailableTagIds,
  getMenuTagIds,
  removeUnavailableEntityTags,
  removeUnavailableTagIds
} from "../../js/containers/tags/utils";

const getChecklistMenu = (...ids: number[]) => [{
  active: false,
  tagBody: { id: ids[0], childTags: [] },
  children: ids.slice(1).map(id => ({ active: false, tagBody: { id, childTags: [] }, children: [] }))
}];

describe("Tag utils tests", () => {
  describe("getAvailableTagIds", () => {
    test("returns undefined for missing or empty tag trees", () => {
      expect(getAvailableTagIds(undefined)).toBeUndefined();
      expect(getAvailableTagIds([])).toBeUndefined();
    });

    test("collects nested child tag IDs recursively", () => {
      const tags = [
        {
          id: 1,
          childrenCount: 2,
          childTags: [
            { id: 2, childrenCount: 1, childTags: [{ id: 3, childrenCount: 0, childTags: [] }] },
            { id: 4, childrenCount: 0, childTags: [] }
          ]
        },
        { id: 5, childrenCount: 1, childTags: [{ id: 2, childrenCount: 0, childTags: [] }] }
      ];

      expect(getAvailableTagIds(tags)).toEqual(new Set([1, 2, 3, 4, 5]));
    });

    test("ignores tag groups on the first level which have no children", () => {
      const tags = [
        { id: 1, childrenCount: 0, childTags: [] },
        { id: 2, childrenCount: 1, childTags: [{ id: 3, childrenCount: 0, childTags: [] }] }
      ];

      expect(getAvailableTagIds(tags)).toEqual(new Set([2, 3]));
    });

    test("ignores nodes without an ID", () => {
      const tags = [{ childrenCount: 1, childTags: [{ id: 10 }] }, { id: 20, childrenCount: 1, childTags: [] }];

      expect(getAvailableTagIds(tags)).toEqual(new Set([10, 20]));
    });
  });

  describe("removeUnavailableTagIds", () => {
    test("removes IDs that are not in the available set while preserving order", () => {
      const availableTagIds = new Set([2, 4]);

      expect(removeUnavailableTagIds([4, 1, 2, 3], availableTagIds)).toEqual([4, 2]);
    });

    test("returns undefined for an undefined value", () => {
      expect(removeUnavailableTagIds(undefined, new Set([1]))).toBeUndefined();
    });

    test("returns an empty array when all IDs are unavailable", () => {
      expect(removeUnavailableTagIds([1, 3], new Set([2]))).toEqual([]);
    });
  });

  describe("applyTagsChange", () => {
    // the form value holds the tag ids and the ids of the completed checklist tasks, the tag input
    // only knows the ones of the tag menu it renders
    test("keeps the ids which are not in the menu when a tag is removed", () => {
      expect(applyTagsChange([11, 1], [11], [])).toEqual([1]);
    });

    test("keeps the ids which are not in the menu when a tag is added", () => {
      expect(applyTagsChange([11, 1], [11], [11, 13])).toEqual([11, 1, 13]);
    });

    test("applies the change when the menu holds every id", () => {
      expect(applyTagsChange([11, 12], [11, 12], [12])).toEqual([12]);
    });

    test("returns the new value when there is nothing to preserve", () => {
      expect(applyTagsChange([], [], [11])).toEqual([11]);
      expect(applyTagsChange(undefined, undefined, [11])).toEqual([11]);
    });
  });

  describe("getMenuTagIds", () => {
    test("returns undefined for missing or empty menus", () => {
      expect(getMenuTagIds(undefined)).toBeUndefined();
      expect(getMenuTagIds([])).toBeUndefined();
    });

    test("collects the IDs of the tag bodies below the first level of a menu tag tree", () => {
      expect(getMenuTagIds(getChecklistMenu(1, 2, 3))).toEqual(new Set([2, 3]));
    });

    test("ignores menus without tasks", () => {
      expect(getMenuTagIds([{ active: false, tagBody: { id: 1 }, children: [] }])).toEqual(new Set());
    });
  });

  describe("getAssignableTagIds", () => {
    const entityTags = [{ id: 1, childrenCount: 1, childTags: [{ id: 2, childrenCount: 0, childTags: [] }] }];

    test("returns undefined when the tags of the entity are not loaded", () => {
      expect(getAssignableTagIds(undefined, getChecklistMenu(3))).toBeUndefined();
    });

    test("contains the tags of the entity and the ids of its checklist tasks", () => {
      expect(getAssignableTagIds(entityTags, getChecklistMenu(3, 4))).toEqual(new Set([1, 2, 4]));
    });

    test("returns the tags of the entity when no checklists are loaded", () => {
      expect(getAssignableTagIds(entityTags, undefined)).toEqual(new Set([1, 2]));
    });
  });

  describe("removeUnavailableEntityTags", () => {
    const item = { id: 5, name: "Course", tags: [1, 99] };

    test("removes tag ids which are not assignable to the entity", () => {
      expect(removeUnavailableEntityTags(item, new Set([1, 2]))).toEqual({ id: 5, name: "Course", tags: [1] });
    });

    test("keeps the item untouched when all tag ids are assignable", () => {
      expect(removeUnavailableEntityTags(item, new Set([1, 99]))).toBe(item);
    });

    test("keeps the item untouched when the tags of the entity are unknown", () => {
      expect(removeUnavailableEntityTags(item, undefined)).toBe(item);
    });

    test("keeps the item untouched when it has no tags", () => {
      const withoutTags = { id: 5, name: "Course" };

      expect(removeUnavailableEntityTags(withoutTags, new Set([1]))).toBe(withoutTags);
    });
  });
});
