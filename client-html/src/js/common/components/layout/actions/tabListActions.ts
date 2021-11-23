import { IAction } from "../../../actions/IshAction";
import { TOGGLE_TAB_LIST_EXPANDED } from "../reducers/tabListReducer";
import { availableEntities, PlainSearchEntity } from "../../../../model/common/Plain";
import {LSGetItem, LSSetItem} from "../../../utils/storage";

const tabListLocalstorageKey = "localstorage_key_tab_list";

export interface TabsListStateEntity {
  expanded: number[];
}

export type TabsListState = {
  [key in PlainSearchEntity]?: TabsListStateEntity;
}

let initial = availableEntities.reduce((p, c) => {
  p[c] = {
    expanded: [],
  };
  return p;
}, {});

const getStoredExpandedItem = LSGetItem(tabListLocalstorageKey);

if (getStoredExpandedItem) initial = JSON.parse(getStoredExpandedItem);

export const tabsListReducer = (state: TabsListState = initial, action: IAction): any => {
  switch (action.type) {
    case TOGGLE_TAB_LIST_EXPANDED: {
      const expandedItems = {
        ...state,
        ...{
          [action.payload.rootEntity]: {
            expanded: action.payload.expanded,
          },
        },
      };

      LSSetItem(tabListLocalstorageKey, JSON.stringify(expandedItems));
      return expandedItems;
    }
    default:
      return state;
  }
};
