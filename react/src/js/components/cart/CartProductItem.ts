import classnames from 'classnames';
import CartItem from './CartItem';
import nativeExtend from './CartProductItem.extend';

const custom = {}; // require("./CartProductItem.custom")
const extend = Object.assign({}, nativeExtend, custom);

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
