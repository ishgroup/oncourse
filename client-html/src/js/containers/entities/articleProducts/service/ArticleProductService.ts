import { ArticleProduct, ArticleProductApi, Diff } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class ArticleProductService {
  readonly articleProductApi = new ArticleProductApi(new DefaultHttpService());

  public getArticleProduct(id: number): Promise<any> {
    return this.articleProductApi.get(id);
  }

  public updateArticleProduct(id: number, articleProduct: ArticleProduct): Promise<any> {
    return this.articleProductApi.update(id, articleProduct);
  }

  public createArticleProduct(articleProduct: ArticleProduct): Promise<any> {
    return this.articleProductApi.create(articleProduct);
  }

  public bulkChange(diff: Diff): Promise<any> {
    return this.articleProductApi.bulkChange(diff);
  }
}

export default new ArticleProductService();
