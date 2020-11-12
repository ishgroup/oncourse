import { IAction } from "../../../common/actions/IshAction";

export interface FinaliseState {}

export const securityReducer = (state: FinaliseState = {}, action: IAction<any>): any => {
  switch (action.type) {
    default:
      return state;
  }
};
