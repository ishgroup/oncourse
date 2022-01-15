import * as EnrolButton from "../components/EnrolButton";
import { isNil } from "lodash";
import { connect } from "react-redux";
import { CourseClassCartState, IshState } from "../../services/IshState";
import { CourseClass } from "../../model";
import { addClassToCart, requestCourseClass } from "../actions/Actions";
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
    checkoutPath: state.config.checkoutPath,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    addToCart: (courseClass: CourseClass) => {
      dispatch(addClassToCart(courseClass));
    },
    loadById: (id: string) => {
      dispatch(requestCourseClass(id));
    },
  };
};

export default connect<any, any, any, IshState>(mapStateToProps, mapDispatchToProps)(EnrolButton.EnrolButton as any);
