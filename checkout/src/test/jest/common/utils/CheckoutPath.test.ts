import {Bootstrap} from "../../../../js/common/utils/Bootstrap";
import {CreateStore} from "../../../../js/CreateStore";
import {HTMLMarkers} from "../../../../js/common/services/HTMLMarker";
import {DefaultConfig} from "../../../../js/constants/Config";

it('reading checkout path from cart element', () => {
  const container: HTMLElement = document.createElement('div');
  container.innerHTML = '<div id="root"><div data-cid="cart" data-prop-checkout-path="/checkout"/></div>';

  document.body.innerHTML = container.innerHTML;

  const store = CreateStore();
  const bootstrap = new Bootstrap(store);
  bootstrap.register(HTMLMarkers.CART);
  bootstrap.start(false);
  expect(store.getState().config.checkoutPath).toBe(DefaultConfig.CHECKOUT_PATH);
});


it('reading checkout path from checkout element', () => {
  const container: HTMLElement = document.createElement('div');
  container.innerHTML = '<div id="root"><div data-cid="checkout" data-prop-checkout-path="/checkout"/></div>';
  document.body.innerHTML = container.innerHTML;

  const store = CreateStore();
  const bootstrap = new Bootstrap(store);
  bootstrap.register(HTMLMarkers.CHECKOUT);
  bootstrap.start(false);
  expect(store.getState().config.checkoutPath).toBe(DefaultConfig.CHECKOUT_PATH);
});
