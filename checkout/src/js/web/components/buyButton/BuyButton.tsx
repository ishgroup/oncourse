import React, {MouseEvent} from "react";
import classnames from "classnames";
import handleViewport from 'react-in-viewport';
import {stopPropagation} from "../../../common/utils/HtmlUtils";
import {ConfirmOrderDialog} from "../addButton/ConfirmOrderDialog";
import {Product} from "../../../model";
import {sendProductDetailsImpressionEvent, sendProductImpressionEvent} from "../../../services/GoogleAnalyticsService";
import {findPriceInDOM} from "../../../common/utils/DomUtils";

class BuyButtonBase extends React.Component<BuyButtonProps, BuyButtonState> {
  constructor(props) {
    super(props);

    this.state = {
      showedPopup: false,
      isAlreadyAdded: false,
      pending: false,
      isViewEventSent: false,
    };
  }

  addProduct = (e: MouseEvent<HTMLAnchorElement>) => {
    if (this.state.pending) {
      return;
    }

    if (this.props.isAdded) {
      stopPropagation(e);
      this.setState({
        showedPopup: true,
        isAlreadyAdded: true,
      });
    } else {
      this.setState({
        pending: true,
      });

      this.props.addProduct(this.props.product);

      this.setState({
        showedPopup: true,
        isAlreadyAdded: false,
        pending: false,
        isViewEventSent: false,
      });
    }
  }

  closePopup = () => {
    this.setState({
      showedPopup: false,
    });
  }

  componentDidMount() {
    const {id, requestProductById} = this.props;
    requestProductById(id);
  }

  componentDidUpdate() {
    const {inViewport, product} = this.props;
    const {isViewEventSent} = this.state;

    if (inViewport && !isViewEventSent) {
      this.setState({
        isViewEventSent: true
      })

      const eventData = {
        id: product.id,
        name: product.name,
        category: "",
        variant: product.type,
        quantity: 1,
        price: findPriceInDOM(product.id)
      };

      sendProductImpressionEvent(eventData);

      if(document.querySelector('[class="courseDescription"]')) {
        sendProductDetailsImpressionEvent(eventData)
      }
    }
  }

  render() {
    const {isAdded, id, checkoutPath} = this.props;
    const {canBuy, isPaymentGatewayEnabled, name} = this.props.product;

    if (!canBuy || !isPaymentGatewayEnabled) {
      return null;
    }

    return (
      <div className="classAction">
        <a className={classnames("enrolAction", {"enrol-added-class": isAdded})}
           onClick={this.addProduct}>
          {isAdded ? "Added" : "Buy now"}
        </a>
        {this.state.showedPopup &&
          <ConfirmOrderDialog
            id={id}
            name={name}
            isAlreadyAdded={this.state.isAlreadyAdded}
            close={this.closePopup}
            checkoutPath={checkoutPath}
          />
        }
      </div>
    );
  }
}


export const BuyButton = handleViewport(BuyButtonBase);

export interface BuyButtonProps {
  readonly id: string;
  readonly product: Product;
  readonly isAdded: boolean;
  readonly inViewport: boolean;
  readonly checkoutPath: string;
  readonly addProduct: (product: Product) => void;
  readonly requestProductById: (id: string) => void;
}

interface BuyButtonState {
  readonly showedPopup: boolean;
  readonly isAlreadyAdded: boolean;
  readonly pending: boolean;
  readonly isViewEventSent: boolean;
}
