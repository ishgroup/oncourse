import nativeExtend from './CartProductItem.extend';
import customExtend from './CartProductItem.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class CartProductItem extends React.Component {

    constructor() {
        super();
        this.remove = (e) => {
            e.stopPropagation();
            this.props.remove(this.props.product.id);
        };
    }

    render() {
        return extend.render.apply({
            props: {
                product: this.props.product
            },
            methods: {
                remove: this.props.remove
            }
        });
    }
}

CartProductItem.propTypes = {
    product: React.PropTypes.object,
    remove: React.PropTypes.func
};