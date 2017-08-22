import * as React from "react";
import {MouseEvent} from "react";
import classnames from "classnames";
import {stopPropagation} from "../../../common/utils/HtmlUtils";

import {ConfirmOrderDialog} from "../addButton/ConfirmOrderDialog";
import {Product} from "../../../model";

export class BuyButton extends React.Component<BuyButtonProps, BuyButtonState> {
  constructor() {
    super();

    this.state = {
      showedPopup: false,
      isAlreadyAdded: false,
      pending: false
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
        isAlreadyAdded: true
      });
    } else {
      this.setState({
        pending: true
      });

      this.props.addProduct(this.props.product);

      this.setState({
        showedPopup: true,
        isAlreadyAdded: false,
        pending: false
      });
    }
  };

  closePopup = () => {
    this.setState({
      showedPopup: false
    });
  };

  componentDidMount() {
    const {id, requestProductById} = this.props;

    requestProductById(id);
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
          {isAdded ? "Added" : "BUY NOW"}
        </a>
        {this.state.showedPopup && <ConfirmOrderDialog id={id}
                                                       name={name}
                                                       isAlreadyAdded={this.state.isAlreadyAdded}
                                                       close={this.closePopup}
                                                       checkoutPath={checkoutPath}/>}
      </div>
    );
  }
}

export interface BuyButtonProps {
  readonly id: string;
  readonly product: Product;
  readonly isAdded: boolean;
  readonly checkoutPath: string;
  readonly addProduct: (product: Product) => void;
  readonly requestProductById: (id: string) => void;
}

interface BuyButtonState {
  readonly showedPopup: boolean,
  readonly isAlreadyAdded: boolean,
  readonly pending: boolean;
}
