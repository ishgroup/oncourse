import { Epic } from 'redux-observable';
import 'rxjs';
import { SELECT_ITEM_REQUEST, updateItem } from '../containers/summary/actions/Actions';
import * as EpicUtils from '../../common/epics/EpicUtils';
import { getCheckoutModelFromBackend } from '../actions/Actions';
import { FULFILLED } from '../../common/actions/ActionUtils';
import CheckoutServiceV2 from '../services/CheckoutServiceV2';

const request: EpicUtils.Request<any, any> = {
  type: SELECT_ITEM_REQUEST,
  getData: CheckoutServiceV2.updateItem,
  processData: (value: any) => [
    { type: FULFILLED(SELECT_ITEM_REQUEST) },
    updateItem(value),
    getCheckoutModelFromBackend(),
  ],
};

export const EpicItemSelect: Epic<any, any> = EpicUtils.Create(request);
