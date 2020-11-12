/**
 * Service to make queries for link rest protocol
 **/

export class SearchService {
  static buildSearchString(items: string[]) {
    if (!items) return "";

    return items.join(",");
  }
}
