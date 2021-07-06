import { Product } from '../../../../js/model';
import { RedeemVoucherProductsReducer } from '../../../../js/enrol/reducers/Reducer';
import {
  addRedeemVoucherProductsToState,
  removeRedeemVoucherProduct,
  setRedeemVoucherProductActivity
} from '../../../../js/enrol/actions/Actions';
import { FULFILLED } from '../../../../js/common/actions/ActionUtils';
import { Actions } from '../../../../js/web/actions/Actions';
import { normalize } from 'normalizr';
import { ProductsSchema } from '../../../../js/NormalizeSchema';
import { mockArray, mockProduct, mockRedeemVoucherProduct } from '../../../../dev/mocks/mocks/MockFunctions';
import * as faker from 'faker';

describe('test RedeemVoucherProductsReducer', () => {
  test('test ADD_REDEEM_VOUCHER_PRODUCTS_TO_STATE action', () => {
    const products = mockArray(mockRedeemVoucherProduct);
    expect(RedeemVoucherProductsReducer(
      [],
      addRedeemVoucherProductsToState(products))
    ).toEqual(products);
  });

  test('test SET_REDEEM_VOUCHER_PRODUCT_ACTIVITY action', () => {
    const prevState = mockArray(mockRedeemVoucherProduct);
    const stateProduct = prevState[faker.datatype.number(prevState.length - 1)];
    const enabled = faker.datatype.boolean();
    const newState = RedeemVoucherProductsReducer(prevState, setRedeemVoucherProductActivity(stateProduct.id, enabled));
    expect(newState).toContainEqual({...stateProduct, enabled});
  });

  test('test REMOVE_REDEEM_VOUCHER_PRODUCT action', () => {
    const prevState = mockArray(mockRedeemVoucherProduct);
    const stateProduct = prevState[faker.datatype.number(prevState.length)];
    const newState = RedeemVoucherProductsReducer(prevState, removeRedeemVoucherProduct(stateProduct));
    expect(newState).not.toContainEqual(stateProduct);
  });

  test('test REMOVE_PRODUCT_FROM_CART action', () => {
    const product: Product = mockProduct('VOUCHER');
    const prevState = [{
      id: product.id,
      enabled: true,
      name: product.name,
      amount: 100
    }]

    const action = {
      type: FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART),
      payload: normalize(product, ProductsSchema),
    }

    expect(RedeemVoucherProductsReducer(prevState, action))
      .toEqual([]);
  });
})

