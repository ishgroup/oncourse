import * as React from 'react';
import {CommonCartItem} from "../../services/IshState";

export default class CartItem extends React.Component<CartItemProps, CartItemState> {

  constructor() {
    super();

    this.state = {
      pending: false
    };
  }

  remove = () => {
    if (this.state.pending) {
      return;
    }

    this.setState({pending: true});
    this.props.remove(this.props.item.id)
      .fail(() => {
        this.setState({pending: false});
      });
  };
}

interface CartItemProps {
  item: CommonCartItem;
  remove: (id: number) => JQueryPromise<{}>;
}

interface CartItemState {
  pending: boolean;
}
