import nativeExtend from './CartClassItem.extend';
import customExtend from './CartClassItem.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class CartOrderItem extends React.Component {

    constructor() {
        super();
        this.remove = (e) => {
            e.stopPropagation();
            this.props.remove(this.props.courseClass.id);
        };
    }

    render() {
        return extend.render.apply({
            props: {
                courseClass: this.props.courseClass
            },
            methods: {
                remove: this.remove
            }
        });
    }
}

CartOrderItem.propTypes = {
    courseClass: React.PropTypes.object,
    remove: React.PropTypes.func
};

export default CartOrderItem;