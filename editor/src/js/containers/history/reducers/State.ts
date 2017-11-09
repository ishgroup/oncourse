import {Version} from "../../../model";

export class HistoryState {
  versions: Version[] = [];
  fetching: boolean = false;
}
