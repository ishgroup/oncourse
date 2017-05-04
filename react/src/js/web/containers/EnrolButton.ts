import * as EnrolButton from "../components/EnrolButton";
import {connect} from "react-redux";
import {CourseClassCartState, IshState} from "../../services/IshState";
import {CourseClass} from "../../model/web/CourseClass";
import {Actions} from "../actions/Actions";
import * as CourseClassService from "../services/CourseClassService";

export default connect(mapStateToProps, mapDispatchToProps)(EnrolButton.EnrolButton as any);

function isAdded(items: CourseClassCartState, id) {
  let item = items.result.find(itemId => itemId === id);
  return !!item;
}

function mapStateToProps(state: IshState, props) {
  const courseClass:CourseClass = CourseClassService.htmlProps2CourseClass(props);
  return {
    courseClass: courseClass || state.courses.entities[courseClass.id] || {},
    isAdded: isAdded(state.cart.courses, props.id)
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addCourseClass: (courseClass: CourseClass) => {
      dispatch({
        type: Actions.ADD_CLASS_TO_CART,
        payload: courseClass
      });
    },
    requestCourseClassById: (id: string) => {
      dispatch({
        type: Actions.REQUEST_COURSE_CLASS,
        payload: id
      });
    }
  };
}
