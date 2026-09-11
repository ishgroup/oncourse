import { SpecialTag, SpecialTagType, Tag, TagApi } from '@api/model';
import { DefaultHttpService } from '../../../common/services/HttpService';

class TagsService {
  readonly tagApi = new TagApi(new DefaultHttpService());

  /**
   * On a list load the same entity's tags are asked for by several independent places at once -
   * the list side bar, the bulk edit drawer, the edit view, and for some entities a menu tags
   * epic on top of that. They all want the same response, so identical requests that are still
   * in flight share one promise.
   *
   * The entry is dropped as soon as the request settles, so this only collapses genuine
   * duplicates and never serves anything from a stale cache.
   */
  private readonly inFlight = new Map<string, Promise<Tag[]>>();

  private dedupe(key: string, request: () => Promise<Tag[]>): Promise<Tag[]> {
    const pending = this.inFlight.get(key);

    if (pending) {
      return pending;
    }

    const result = request().finally(() => {
      this.inFlight.delete(key);
    });

    this.inFlight.set(key, result);

    return result;
  }

  public getChecklists(entityName: string, id?: number): Promise<Tag[]> {
    return this.dedupe(`checklists:${entityName}:${id || ""}`, () => this.tagApi.getChecklists(entityName, id));
  }  
  
  public getTag(id: number): Promise<Tag> {
    return this.tagApi.getTag(id);
  }

  public getTags(entityName?: string): Promise<Tag[]> {
    return this.dedupe(`tags:${entityName || ""}`, () => this.tagApi.get(entityName));
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

  updateSpecial(childTags: Tag[], specialType: SpecialTagType): Promise<any> {
    return this.tagApi.updateSpecial({ childTags, specialType });
  }

  getSpecialTags(entityName: string): Promise<SpecialTag> {
    return this.tagApi.getSpecialTags(entityName);
  }
}

export default new TagsService();
