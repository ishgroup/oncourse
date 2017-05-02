import {EnrolButton, EnrolButtonProps} from "../components/enrolButton/EnrolButton";
import {connect} from "react-redux";
import {CourseClassCartState, IshState} from "../../services/IshState";
import {IshActions} from "../../constants/IshActions";
import {CourseClass} from "../../model/web/CourseClass";

export default connect(mapStateToProps, mapDispatchToProps)(EnrolButton as any);

function isAdded(items: CourseClassCartState, id) {
  let item = items.result.find(itemId => itemId === id);
  return !!item;
}

function mapStateToProps(state: IshState, ownProps: EnrolButtonProps) {
  return {
    courseClass: state.courses.entities[ownProps.id] || {},
    isAdded: isAdded(state.cart.courses, ownProps.id)
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addCourseClass: (courseClass: CourseClass) => {
      dispatch({
        type: IshActions.ADD_CLASS_TO_CART,
        payload: courseClass
      });
    },
    requestCourseClassById: (id: string) => {
      dispatch({
        type: IshActions.REQUEST_COURSE_CLASS,
        payload: id
      });
    }
  };
}
