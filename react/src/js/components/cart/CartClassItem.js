import classnames from 'classnames';
import CartItem from './CartItem';
import nativeExtend from './CartClassItem.extend';
import customExtend from './CartClassItem.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class CartOrderItem extends CartItem {

    render() {
        return extend.render.apply({
            props: {
                courseClass: this.props.item,
                pending: this.state.pending
            },
            methods: {
                remove: this.remove
            },
            utils: { classnames }
        });
    }
}

export default CartOrderItem;