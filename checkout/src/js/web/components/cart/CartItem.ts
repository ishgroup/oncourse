import * as React from "react";
import {CommonCartItem} from "../../../services/IshState";

export default class CartItem extends React.Component<CartItemProps, CartItemState> {

  constructor(props) {
    super(props);

    this.state = {
      pending: false,
    };
  }

  remove = () => {
    if (this.state.pending) {
      return;
    }

    this.setState({pending: true});
    this.props.remove(this.props.item);
    this.setState({pending: false});
  }
}

interface CartItemProps {
  item: CommonCartItem;
  remove: (item: CommonCartItem) => void;
}

interface CartItemState {
  pending: boolean;
}
