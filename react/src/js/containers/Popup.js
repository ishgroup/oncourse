import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { updatePopup, hidePopup } from '../actions/popup';

class Popup extends React.Component {

    componentWillMount() {
        this.props.update(this.props.children);
    }

    componentWillReceiveProps() {
        this.props.update(this.props.children);
    }

    componentWillUnmount() {
        this.props.hide();
    }

    render() {
        return null;
    }
}

export default connect(null, (dispatch) => {
    return bindActionCreators({
        updatePopup, hidePopup
    }, dispatch);
})(Popup);