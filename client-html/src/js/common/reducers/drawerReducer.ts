import { CLOSE_DRAWER, OPEN_DRAWER } from "../actions";
import { IAction } from "../actions/IshAction";

export const drawerReducer = (state: any = {opened: false}, action: IAction<any>): any => {
  switch (action.type) {
    case OPEN_DRAWER: {
      return {
        opened: true
      };
    }

    case CLOSE_DRAWER: {
      return {
        opened: false
      };
    }

    default:
      return state;
  }
};
