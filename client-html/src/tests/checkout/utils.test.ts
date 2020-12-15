import { mapPlainCourseFromUrlId } from "../../js/containers/checkout/utils";

describe("Checkout utils tests", () => {
  test("mapPlainCourseFromUrlId test", () => {
    const result = mapPlainCourseFromUrlId({}, {});
    expect(result).toMatchObject({
      checked: true,
      paymentPlans: [],
      priceExTax: 0,
      tax: 0,
      taxAmount: 0,
      type: "course",
      priceOverriden: null,
      discount: null,
      discounts: [],
      discountExTax: 0,
      studyReason: "Not stated"
    });
  });
});
