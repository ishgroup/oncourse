import { Store } from 'redux';
import { IshState } from '../../services/IshState';
import { REWRITE_CONTACT_NODES_TO_STATE } from '../containers/summary/actions/Actions';
import { Voucher } from '../../model';
import { RedeemVoucherProduct } from '../../model';
import { addRedeemVoucherProductsToState } from '../actions/Actions';

function createEpicRefreshRedeemVouchers() {
  return (action$, store: Store<IshState>) => action$
    .ofType(REWRITE_CONTACT_NODES_TO_STATE)
    .map(action => {
      const vouchers: RedeemVoucherProduct[] = [];
      const state = store.getState();
      const notOnlyVouchers = Object.keys(action.payload?.entities || {}).some(k => [
        'enrolments',
        'applications',
        'articles',
        'memberships',
        'waitingLists'
      ].includes(k))

      if (notOnlyVouchers) {
        Object.keys(action.payload?.entities?.vouchers || {}).forEach(key => {
          const voucher: Voucher = action.payload?.entities?.vouchers[key];
          const stateProduct = state.products.entities[voucher?.productId];
          const stateVoucher = state.checkout.redeemedVoucherProducts.find(v => v.id === voucher?.productId);
          if (voucher && stateProduct && voucher.selected && !voucher.classes.length) {
            vouchers.push({
              id: voucher.productId,
              enabled: stateVoucher?.enabled,
              name: stateProduct.name,
              amount: voucher.total
            })
          }
        })
      }
      return addRedeemVoucherProductsToState(vouchers);
    });
}

export default createEpicRefreshRedeemVouchers();
