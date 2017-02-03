import classnames from 'classnames';
import ConfirmOrderDialog from './ConfirmOrderDialog';
import {stopPropagation} from '../../lib/utils';

class AddButton extends React.Component {
    constructor() {
        super();
        this.add = (e) => {
            if(this.state.pending) {
                return;
            }

            if(this.props.isAdded) {
                stopPropagation(e);
                this.setState({
                    showedPopup: true,
                    isAlreadyAdded: true
                });
            } else {
                //ToDo how to handle error?
                this.setState({
                   pending: true
                });

                this.props.add({
                    id: this.props.id,
                    name: this.props.name
                })
                    .then(() => {
                        this.setState({
                            showedPopup: true,
                            isAlreadyAdded: false
                        });
                    })
                    .always(() => {
                        this.setState({ pending: false });
                    });
            }
        };

        this.closePopup = () => {
            this.setState({
                showedPopup: false
            });
        };

        this.state = {
            showedPopup: false,
            isAlreadyAdded: false,
            pending: false
        };
    }

    getContext() {
        return {
            props: {
                id: this.props.id,
                name: this.props.name,
                isAdded: this.props.isAdded,
                isAlreadyAdded: this.state.isAlreadyAdded,
                showedPopup: this.state.showedPopup,
                paymentGatewayEnabled: this.props.paymentGatewayEnabled
            },
            methods: {
                add: this.add,
                closePopup: this.closePopup
            },
            components: { ConfirmOrderDialog },
            utils: { classnames }
        };
    }
}

AddButton.propTypes = {
    isAdded: React.PropTypes.bool,
    add: React.PropTypes.func,
    id: React.PropTypes.number,
    name: React.PropTypes.string,
    paymentGatewayEnabled: React.PropTypes.bool
};

export default AddButton;
