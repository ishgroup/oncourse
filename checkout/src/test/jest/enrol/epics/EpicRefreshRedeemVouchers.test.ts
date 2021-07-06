import { DefaultEpicTest } from '../../common/utils/DefaultEpicTest';
import {
  rewriteContactNodesToState
} from '../../../../js/enrol/containers/summary/actions/Actions';
import EpicRefreshRedeemVouchers from '../../../../js/enrol/epics/EpicRefreshRedeemVouchers';
import {
  mockArray,
  mockContactNode,
  mockEnrolment, mockProduct,
  mockState,
  mockVoucher
} from '../../../../dev/mocks/mocks/MockFunctions';
import { addRedeemVoucherProductsToState } from '../../../../js/enrol/actions/Actions';
import { normalize } from 'normalizr';
import { ProductsSchema } from '../../../../js/NormalizeSchema';


describe("Create EpicRefreshRedeemVouchers epic tests", () => {
  test("EpicRefreshRedeemVouchers should return empty array on adding vouchers only", () => {
    const contactNode = mockContactNode();
    contactNode.vouchers = mockArray(mockVoucher);

    DefaultEpicTest({
      action: rewriteContactNodesToState([contactNode]),
      epic: EpicRefreshRedeemVouchers,
      result: addRedeemVoucherProductsToState([])
    })
  });

  test("EpicRefreshRedeemVouchers should return redeemVoucherProduct array on adding vouchers and other items", () => {
    const contactNode = mockContactNode();
    contactNode.vouchers = [mockVoucher()];
    contactNode.vouchers[0].selected = true;
    contactNode.enrolments = [mockEnrolment()];
    const customStore = mockState();
    const product = mockProduct('VOUCHER');
    product.id = contactNode.vouchers[0].productId;
    const normalized = normalize(product, ProductsSchema);

    const redeemVoucherProduct = {
      id: contactNode.vouchers[0].productId,
      enabled: true,
      name: product.name,
      amount: contactNode.vouchers[0].total
    }

    customStore.products = { result: normalized.result, entities: {...normalized.entities.products} };
    customStore.checkout.redeemedVoucherProducts = [redeemVoucherProduct];

    DefaultEpicTest({
      action: rewriteContactNodesToState([contactNode]),
      epic: EpicRefreshRedeemVouchers,
      result: addRedeemVoucherProductsToState([redeemVoucherProduct]),
      customStore
    })
  });
});
