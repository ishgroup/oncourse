import classnames from "classnames";
import CartItem from "./CartItem";
import nativeExtend from "./CartCourseItem.extend";

const extend = Object.assign({}, nativeExtend);

class CartCourseItem extends CartItem {

  render() {
    return extend.render.apply({
      props: {
        course: this.props.item,
        pending: this.state.pending,
      },
      methods: {
        remove: this.remove,
      },
      utils: {classnames},
    });
  }
}

export default CartCourseItem;
