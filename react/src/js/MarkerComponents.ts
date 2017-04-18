import {ComponentClass, StatelessComponent} from "react";
import {EnrolRoot} from "./containers/EnrolRoot";
import {Fees} from "./containers/Fees";
import EnrolButton from "./containers/EnrolButton";
import {CartRoot} from "./containers/CartRoot";
import BuyButton from "./containers/BuyButton";
import PopupContainer from "./containers/PopupContainer";
import Promotions from "./containers/Promotions";
import {LegacyModal} from "./components/modal/LegacyModal";

export class MarkerComponents {

  /**
   * Renders enrol application: add student, details, summary, payment.
   *
   * Corresponding React Component: {@link EnrolRoot}
   * Example Usage: <div data-cid="enrol"></div>
   * Tapestry template to use: Checkout.tml
   */
  static ENROL: MarkerToComponent = {
    id: "enrol",
    component: EnrolRoot,
    props: {}
  };

  /**
   * Renders cart component.
   *
   * Corresponding React Component: {@link CartRoot}
   * Example Usage: <div data-cid="cart"></div>
   * Tapestry template to use: ShortList.tml
   */
  static CART: MarkerToComponent = {
    id: "cart",
    component: CartRoot,
    props: {}
  };

  /**
   * Renders promotions component.
   *
   * Corresponding React Component: {@link Promotions}
   * Example Usage: <div data-cid="promotions"></div>
   * Tapestry template to use: BodyHeader.tml
   */
  static PROMOTIONS: MarkerToComponent = {
    id: "promotions",
    component: Promotions,
    props: {}
  };

  /**
   * Renders fees component.
   *
   * Corresponding React Component: {@link Fees}
   * Example Usage: <div data-cid="fees"  data-prop-id="${id}"></div>
   * Tapestry template to use: CourseClassItem.tml
   */
  static FEES: MarkerToComponent = {
    id: "fees",
    component: Fees,
    props: {id: "string"}
  };

  /**
   * Renders enrol button.
   *
   * Corresponding React Component: {@link EnrolButton}
   * Example Usage: <div data-cid="enrol-button"  data-prop-id="${id}"></div>
   * Tapestry template to use: CourseClassItem.tml
   */
  static ENROL_BUTTON: MarkerToComponent = {
    id: "enrol-button",
    component: EnrolButton,
    props: {id: "string"}
  };

  /**
   * Renders buy button.
   *
   * Corresponding React Component: {@link BuyButton}
   * Example Usage: <div data-cid="buy-button"  data-prop-id="${id}"></div>
   * Tapestry template to use: CourseClassItem.tml
   */
  static BUY_BUTTON: MarkerToComponent = {
    id: "buy-button",
    component: BuyButton,
    props: {id: "string"}
  };

  /**
   * Renders modal component.
   * Should be single component!
   *
   * Corresponding React Component: {@link LegacyModal}
   * Example Usage: <div data-cid="modal"></div>
   * Tapestry template to use: PageStructure.tml
   */
  static MODAL: MarkerToComponent = {
    id: "modal",
    component: LegacyModal,
    props: {}
  };

  /**
   * Renders old modal component.
   *
   * Corresponding React Component: {@link PopupContainer}
   * Example Usage: <div data-cid="popup"></div>
   * Tapestry template to use: PageStructure.tml
   */
  static POPUP: MarkerToComponent = {
    id: "popup",
    component: PopupContainer,
    props: {}
  };
}

export interface MarkerToComponent {
  readonly id: string;
  readonly component: ComponentClass<any> | StatelessComponent<any>;
  readonly props: { [key: string]: string };
}
