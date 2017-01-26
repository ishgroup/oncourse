import classnames from 'classnames';
import CartItem from './CartItem';
import nativeExtend from './CartProductItem.extend';
import customExtend from './CartProductItem.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class CartProductItem extends CartItem {

    render() {
        return extend.render.apply({
            props: {
                product: this.props.item,
                pending: this.state.pending
            },
            methods: {
                remove: this.remove
            },
            utils: { classnames }
        });
    }
}

export default CartProductItem;