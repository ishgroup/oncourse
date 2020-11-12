import { DefaultHttpService } from "../../../common/services/HttpService";
import { TagApi, Tag } from "@api/model";

class TagsService {
  readonly tagApi = new TagApi(new DefaultHttpService());

  public getTags(entityName?: string): Promise<Tag[]> {
    return this.tagApi.get(entityName);
  }

  public updateTag(id: number, tag: Tag): Promise<Tag[]> {
    return this.tagApi.update(id, tag);
  }

  public create(tag: Tag): Promise<Tag[]> {
    return this.tagApi.create(tag);
  }

  public remove(id: number): Promise<Tag[]> {
    return this.tagApi.remove(id);
  }
}

export default new TagsService();
