import {ContactNodesStorage} from "../../enrol/containers/summary/reducers/State";

export type StoredCartData = ContactNodesStorage & {
  payerId?: string;
}
