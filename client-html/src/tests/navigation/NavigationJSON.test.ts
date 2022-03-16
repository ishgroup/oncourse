import navigation from "../../js/common/components/navigation/navigation.json";

describe("test navigation JSON validity", () => {
  it("all categories should have features", () => expect(navigation.categories).not.toEqual(
    expect.arrayContaining([
      expect.objectContaining({ features: [] })
    ])
  ));
  test("all categories should have valid feature keys", () => {
    const validKeys = navigation.features.map(f => f.key);
    
    for (const cat of navigation.categories) {
      expect(cat.features.find(f => !validKeys.includes(f))).toBeUndefined();
    }
  });
});