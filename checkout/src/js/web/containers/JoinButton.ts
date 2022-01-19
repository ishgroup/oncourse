import { isNil } from "lodash";
import { connect } from "react-redux";
import { IshState, WaitingCourseClassState } from "../../services/IshState";
import { Course } from "../../model";
import { addWaitingCourseToCart, requestWaitingCourse } from "../actions/Actions";
import { JoinButton } from "../components/joinButton/JoinButton";

const isAdded = (items: WaitingCourseClassState, id: string): boolean => {
  const item = items.result.find(itemId => itemId === id);
  return !isNil(item);
};

const mapStateToProps = (state: IshState, props) => {
  const course: Course = new Course();
  course.id = props.id;
  course.code = props.code;
  course.name = props.name;
  course.description = props.description;

  return {
    id: course.id,
    course: state.waitingCourses.entities[course.id] || {},
    isAdded: isAdded(state.cart.waitingCourses, props.id),
    checkoutPath: state.config.checkoutPath,
    enrollableClassesEmpty: props.enrollableClassesEmpty,
    hasMoreAvailablePlaces: props.hasMoreAvailablePlaces,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    addToCart: (course: Course) => {
      dispatch(addWaitingCourseToCart(course));
    },
    loadById: (id: string) => {
      dispatch(requestWaitingCourse(id));
    },
  };
};

export default connect<any, any, any, IshState>(mapStateToProps, mapDispatchToProps)(JoinButton as any);
