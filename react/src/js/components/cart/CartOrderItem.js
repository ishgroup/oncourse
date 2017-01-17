import nativeExtend from './CartOrderItem.extend';
import customExtend from './CartOrderItem.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class CartOrderItem extends React.Component {

    constructor() {
        super();

        this.remove = (e) => {
            e.stopPropagation();
            this.props.onRemove(this.props.course.id);
        };
    }

    render() {
        return extend.render.apply({
            props: {
                course: this.props.course
            },
            methods: {
                remove: this.remove
            }
        });
    }
}

CartOrderItem.propTypes = {
    course: React.PropTypes.object,
    onRemove: React.PropTypes.func
};

export default CartOrderItem;