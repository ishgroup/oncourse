import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get articleProduct entities epic tests", () => {
  it("GetArticleProductEntities should returns correct actions", () => {
    // Expected response
    const articleProduct = mockedAPI.db.getArticleProducts();

    return GetEntities("ArticleProduct", articleProduct);
  });
});
