import CartClassItem from './CartClassItem';
import CartProductItem from './CartProductItem';
import classnames from 'classnames';
import {plural} from '../../lib/utils.ts';
import nativeExtend from './Cart.extend';
import customExtend from './Cart.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class Cart extends React.Component {

    constructor() {
        super();

        this.state = {
            showedShortList: false
        };

        this.toggleShortList = () => {
            this.setState({
                showedShortList: !this.state.showedShortList
            });
        };
    }

    componentWillReceiveProps(nextProps) {
        if(!nextProps.classes.length) {
            this.setState({
                showedShortList: false
            });
        }
    }

    render() {
        return extend.render.apply({
            props: {
                classes: this.props.classes,
                products: this.props.products,
                showedShortList: this.state.showedShortList
            },
            methods: {
                toggleShortList: this.toggleShortList,
                removeClass: this.props.removeClass,
                removeProduct: this.props.removeProduct
            },
            utils: { plural, classnames },
            components: { CartProductItem, CartClassItem }
        });
    }
}

Cart.propTypes = {
    classes: React.PropTypes.array,
    products: React.PropTypes.array,
    removeClass: React.PropTypes.func,
    removeProduct: React.PropTypes.func
};

export default Cart;
