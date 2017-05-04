import {SyncAction, TimedAction} from "../actions/SyncAction";
import {IshAction} from "../actions/IshAction";
import {LocalStorageService} from "./LocalStorageService";
import differenceBy from "lodash/differenceBy";
import unionBy from "lodash/unionBy";

export class MergeService {
  merge(action: SyncAction): IshAction<any>[] {
    return this.mergeInternal(action);
  }

  private mergeInternal(action: SyncAction): IshAction<any>[] {
    /**
     * is actions empty
     *  - try to load state from LocalStorage - send actions as result
     * is there are actions - check if merge needed
     *  - if so - get actions (sort by timestamp, then filter by time), and add they to result array
     *  - if no - add actions to LocalStorage
     *
     * merge cookies in epics
     */
    if (action.payload.length) {
      return mergeActions(action.payload);
    } else {
      // No actions to save, just load existing actions
      return LocalStorageService.get<TimedAction<any>[]>("actions") || [];
    }
  }
}

function saveActions(actions: TimedAction<any>[]) {
  LocalStorageService.set("actions", actions);
}

function mergeActions(actions: TimedAction<any>[]): IshAction<any>[] {
  const savedActions = (LocalStorageService.get<TimedAction<any>[]>("actions") || []);

  // Merge or just write
  if (savedActions.length) {
    const union = unionBy(actions, savedActions, "timestamp");
    const diff = differenceBy(actions, savedActions, 'timestamp');

    saveActions(union);

    return differenceBy(actions, diff, "timestamp")
      .sort((a1, a2) => a1.timestamp - a2.timestamp);
  } else {
    saveActions(actions);
    return [];
  }
}

