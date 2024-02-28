import { Tag, TagApi } from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

class TagsService {
  readonly tagApi = new TagApi(new DefaultHttpService());

  public getChecklists(entityName: string, id?: number): Promise<Tag[]> {
    return this.tagApi.getChecklists(entityName, id);
  }  
  
  public getTag(id: number): Promise<Tag> {
    return this.tagApi.getTag(id);
  }

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

  updateSpecial(tag: Tag[]): Promise<any> {
    return this.tagApi.updateSpecial(tag);
  }

  getSpecialTags(entityName: string): Promise<Array<Tag>> {
    return this.tagApi.getSpecialTags(entityName);
  }
}

export default new TagsService();
