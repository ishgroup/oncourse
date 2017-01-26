class CartItem extends React.Component {

    constructor() {
        super();

        this.remove = () => {
            if(this.state.pending) {
                return;
            }

            this.setState({ pending: true });
            this.props.remove(this.props.item.id)
                .fail(() => {
                    this.setState({ pending: false });
                });
        };

        this.state = {
            pending: false
        };
    }

}

CartItem.propTypes = {
    item: React.PropTypes.object,
    remove: React.PropTypes.func
};

export default CartItem;