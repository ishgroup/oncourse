import {EnrolButton, EnrolButtonCommonProps} from '../components/enrolButton/EnrolButton';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { addClass as add } from '../actions/cart';

function isAdded(items, id) {
    let item = items.find((item) => {
        return item.id === id;
    });

    return !!item;
}

export default connect((state, ownProps: EnrolButtonCommonProps) => {
    return {
        isAdded: isAdded(state.cart.courses, ownProps.id)
    };
}, (dispatch) => {
    return bindActionCreators({ add }, dispatch);
})(EnrolButton as any); //todo
