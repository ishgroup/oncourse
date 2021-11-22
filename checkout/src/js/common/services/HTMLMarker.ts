import {ComponentClass, StatelessComponent} from "react";
import Checkout from "../../enrol/containers/Checkout";
import Cart from "../../web/containers/Cart";
import {Fees} from "../../web/containers/Fees";
import EnrolButton from "../../web/containers/EnrolButton";
import BuyButton from "../../web/containers/BuyButton";
import PopupContainer from "../../web/containers/PopupContainer";
import Promotions from "../../web/containers/Promotions";
import {LegacyModal} from "../../web/components/modal/LegacyModal";
import JoinButton from "../../web/containers/JoinButton";
import Suggestions from "../../web/containers/Suggestions";

export const ATTR_DATA_PROP_PREFIX: string = "data-prop-";
export const ATTR_DATA_CID: string = "data-cid";
export const DEFAULT_CHECKOUT_PATH = "/enrol/";

export class HTMLMarkers {

  /**
   * Renders checkout application: add contact, contact details, summary, payment.
   *
   * Corresponding React Component: {@link CheckoutRoot}
   * Example Usage: <div data-cid="checkout" data-prop-checkout-path="/checkout"></div>
   * To add this checkout app container to a college side, in cms we need create a new page
   * and add in the content this marker, this page url should be use as data-prop-checkout-path
   * for markers: CHECKOUT and CART
   */
  static CHECKOUT: HTMLMarker = {
    id: "checkout",
    component: Checkout,
    props: {
      checkoutPath: "string",
    },
  };

  /**
   * Renders checkout suggestions product.
   *
   * Corresponding React Component: {@link Suggestions}
   * Example Usage: <div data-cid="checkout-suggestions"></div>
   */
  static SUGGESTIONS: HTMLMarker = {
    id: "checkout-suggestions",
    component: Suggestions,
    props: {},
  };

  /**
   * Renders cart component.
   *
   * Corresponding React Component: {@link CartRoot}
   * Example Usage: <div data-cid="cart" data-prop-checkout-path="/checkout"></div>
   * Tapestry template to use: ShortList.tml
   */
  static CART: HTMLMarker = {
    id: "cart",
    component: Cart,
    props: {
      checkoutPath: "string",
    },
  };

  /**
   * Renders promotions component.
   *
   * Corresponding React Component: {@link Promotions}
   * Example Usage: <div data-cid="promotions"></div>
   * Tapestry template to use: BodyHeader.tml
   */
  static PROMOTIONS: HTMLMarker = {
    id: "promotions",
    component: Promotions,
    props: {},
  };

  /**
   * Renders fees component.
   *
   * Corresponding React Component: {@link Fees}
   * Example Usage: <div data-cid="fees"  data-prop-id="${courseClass.id}"></div>
   * Tapestry template to use: CourseClassItem.tml
   */
  static FEES: HTMLMarker = {
    id: "fees",
    component: Fees,
    props: {id: "string"},
  };

  /**
   * Renders checkout button.
   *
   * Corresponding React Component: {@link EnrolButton}
   * Example Usage: <div data-cid="enrol-button"  data-prop-id="${id}" id="oncourse-checkout"></div>
   * Tapestry template to use: CourseClassItem.tml
   */
  static ENROL_BUTTON: HTMLMarker = {
    id: "enrol-button",
    component: EnrolButton,
    props: {
      id: "string",
    },
  };

  /**
   * Renders join button.
   *
   * Corresponding React Component: {@link JoinButton}
   * Example Usage: <div data-cid="join-button"  data-prop-id="${id}"> {tapestry content*} </div>
   * Tapestry content will be wrapped by react into <a> tag
   * Tapestry template to use: CourseItem.tml
   */
  static JOIN_BUTTON: HTMLMarker = {
    id: "join-button",
    component: JoinButton,
    props: {
      id: "string",
    },
  };

  /**
   * Renders buy button.
   *
   * Corresponding React Component: {@link BuyButton}
   * Example Usage: <div data-cid="buy-button"  data-prop-id="${id}"></div>
   * Tapestry template to use: CourseClassItem.tml
   */
  static BUY_BUTTON: HTMLMarker = {
    id: "buy-button",
    component: BuyButton,
    props: {id: "string"},
  };

  /**
   * Renders modal component.
   * Should be single component!
   *
   * Corresponding React Component: {@link LegacyModal}
   * Example Usage: <div data-cid="modal"></div>
   * Tapestry template to use: PageStructure.tml
   */
  static MODAL: HTMLMarker = {
    id: "modal",
    component: LegacyModal,
    props: {},
  };

  /**
   * Renders old modal component.
   *
   * Corresponding React Component: {@link PopupContainer}
   * Example Usage: <div data-cid="popup"></div>
   * Tapestry template to use: PageStructure.tml
   */
  static POPUP: HTMLMarker = {
    id: "popup",
    component: PopupContainer,
    props: {},
  };

  /**
   * Renders fees-range component.
   *
   * Corresponding React Component: {@link Fees}
   * Example Usage: <div data-cid="fees"  data-prop-id="${courseClass.id}"></div>
   * Tapestry template to use: CourseClassItem.tml
   */
  static FEES_RANGE: HTMLMarker = {
    id: "fees-range",
    component: Fees,
    props: {id: "string"},
  };

}

export interface HTMLMarker {
  readonly id: string;
  readonly component: ComponentClass<any> | StatelessComponent<any>;
  readonly props: { [key: string]: string };
}
