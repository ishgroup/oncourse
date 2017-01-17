import CartOrderItem from './CartOrderItem';
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
        if(!nextProps.courses.length) {
            this.setState({
                showedShortList: false
            });
        }
    }

    render() {
        return extend.render.apply({
            props: {
                courses: this.props.courses,
                showedShortList: this.state.showedShortList
            },
            methods: {
                toggleShortList: this.toggleShortList,
                onRemoveCourse: this.props.onRemoveCourse
            },
            components: { CartOrderItem }
        });
    }
}

Cart.propTypes = {
    courses: React.PropTypes.array,
    onRemoveCourse: React.PropTypes.func
};

export default Cart;