import {IAction} from "../../../../actions/IshAction";
export const TRY_ANOTHER_CARD = "checkout/result/try/another/card";

export const tryAnotherCard = (): IAction<any> => {
  return {
    type: TRY_ANOTHER_CARD,
  };
};
