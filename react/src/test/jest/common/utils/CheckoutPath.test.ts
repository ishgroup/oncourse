import {Bootstrap} from "../../../../js/common/utils/Bootstrap";
import {CreateStore} from "../../../../js/CreateStore";
import {HTMLMarkers} from "../../../../js/common/services/HTMLMarker";

test('reading enrol path from cart element', () => {
  const container: HTMLElement = document.createElement('div');
  container.innerHTML = '<div id="root"><div data-cid="cart" data-prop-enrol-path="/checkout"/></div>';

  document.body.innerHTML = container.innerHTML;

  const store = CreateStore();
  const bootstrap = new Bootstrap(store);
  bootstrap.register(HTMLMarkers.CART);
  bootstrap.start(false);
  expect(store.getState().checkoutPath).toBe("/checkout");
});


test('reading enrol path from enrol element', () => {
  const container: HTMLElement = document.createElement('div');
  container.innerHTML = '<div id="root"><div data-cid="enrol" data-prop-enrol-path="/checkout"/></div>';
  document.body.innerHTML = container.innerHTML;

  const store = CreateStore();
  const bootstrap = new Bootstrap(store);
  bootstrap.register(HTMLMarkers.CHECKOUT);
  bootstrap.start(false);
  expect(store.getState().checkoutPath).toBe("/checkout");
});




