import classnames from 'classnames';
import { plural, noop } from 'js/lib/utils';
import ConfirmOrderDialog from './ConfirmOrderDialog';
import nativeExtend from './EnrolButton.extend';
import customExtend from './EnrolButton.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class EnrolButton extends React.Component {

    constructor() {
        super();
        this.add = () => {
            if(this.props.isAdded) {
                this.setState({
                    showedPopup: true,
                    isAlreadyAdded: true
                });
            } else {
                //ToDo how to handle error?
                this.props.onEnrol(this.props.classId)
                    .then(() => {
                        this.setState({
                            showedPopup: true,
                            isAlreadyAdded: false
                        });
                    }, noop);
            }
        };

        this.closePopup = () => {
            this.setState({
                showedPopup: false
            });
        };

        this.state = {
            showedPopup: false,
            isAlreadyAdded: false
        };
    }

    render() {
        return extend.render.apply({
            props: {
                classId: this.props.classId,
                className: this.props.className,
                isAdded: this.props.isAdded,
                isCanceled: this.props.isCanceled,
                isFinished: this.props.isFinished,
                hasAvailableEnrolmentPlaces: this.props.hasAvailableEnrolmentPlaces,
                paymentGatewayEnabled: this.props.paymentGatewayEnabled,
                allowByApplication: this.props.allowByApplication,
                freePlaces: this.props.freePlaces,
                showedPopup: this.state.showedPopup,
                isAlreadyAdded: this.state.isAlreadyAdded
            },
            methods: {
                add: this.add,
                closePopup: this.closePopup
            },
            components: { ConfirmOrderDialog },
            utils: { classnames, plural }
        });
    }
}

EnrolButton.propTypes = {
    isAdded: React.PropTypes.bool,
    onEnrol: React.PropTypes.func,

    // props from container
    classId: React.PropTypes.number,
    className: React.PropTypes.string,
    isCanceled: React.PropTypes.bool,
    isFinished: React.PropTypes.bool,
    hasAvailableEnrolmentPlaces: React.PropTypes.bool,
    paymentGatewayEnabled: React.PropTypes.bool,
    allowByApplication: React.PropTypes.bool,
    freePlaces: React.PropTypes.number
};

export default EnrolButton;