import * as EnrolButton from "../components/EnrolButton";
import {isNil} from "lodash";
import {connect} from "react-redux";
import {CourseClassCartState, IshState} from "../../services/IshState";
import {CourseClass} from "../../model/web/CourseClass";
import {Actions} from "../actions/Actions";
import * as CourseClassService from "../services/CourseClassService";

const isAdded = (items: CourseClassCartState, id: string): boolean => {
    const item = items.result.find(itemId => itemId === id);
    return !isNil(item);
};

const mapStateToProps = (state: IshState, props) => {
    const courseClass: CourseClass = CourseClassService.htmlProps2CourseClass(props);
    return {
        id: courseClass.id,
        courseClass: state.courses.entities[courseClass.id] || {},
        isAdded: isAdded(state.cart.courses, props.id),
        checkoutPath: state.checkoutPath
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        addToCart: (courseClass: CourseClass) => {
            dispatch({
                type: Actions.ADD_CLASS_TO_CART,
                payload: courseClass
            });
        },
        loadById: (id: string) => {
            dispatch({
                type: Actions.REQUEST_COURSE_CLASS,
                payload: id
            });
        }
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(EnrolButton.EnrolButton as any);
