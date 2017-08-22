import classnames from "classnames";
import CartItem from "./CartItem";
import nativeExtend from "./CartClassItem.extend";

const extend = Object.assign({}, nativeExtend);

class CartClassItem extends CartItem {

  render() {
    return extend.render.apply({
      props: {
        courseClass: this.props.item,
        pending: this.state.pending
      },
      methods: {
        remove: this.remove
      },
      utils: {classnames}
    });
  }
}

export default CartClassItem;
