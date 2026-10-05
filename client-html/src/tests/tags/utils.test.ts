import { getAvailableTagIds, removeUnavailableTagIds } from "../../js/containers/tags/utils";

describe("Tag utils tests", () => {
  describe("getAvailableTagIds", () => {
    test("returns undefined for missing or empty tag trees", () => {
      expect(getAvailableTagIds(undefined)).toBeUndefined();
      expect(getAvailableTagIds([])).toBeUndefined();
    });

    test("collects root and nested child tag IDs recursively", () => {
      const tags = [
        {
          id: 1,
          childTags: [
            { id: 2, childTags: [{ id: 3, childTags: [] }] },
            { id: 4, childTags: [] }
          ]
        },
        { id: 5, childTags: [{ id: 2, childTags: [] }] }
      ];

      expect(getAvailableTagIds(tags)).toEqual(new Set([1, 2, 3, 4, 5]));
    });

    test("ignores nodes without an ID", () => {
      const tags = [{ childTags: [{ id: 10 }] }, { id: 20 }];

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
});
