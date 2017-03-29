import {IshAction} from "./IshAction";

export interface TimedAction<T> extends IshAction<T> {
  timestamp: number;
}

export interface SyncAction extends IshAction<TimedAction<any>[]> {

}
