import * as React from "react";
import {connect} from "react-redux";
import {FeesComponent, FeesComponentProps} from "../components/fees/FeesComponent";
import {IshState} from "../../services/IshState";
import {Actions} from "../actions/Actions";

class FeesContainer extends React.Component<FeesProps, FeesState> {
  constructor() {
    super();
  }

  componentDidMount() {
    const {id, requestCourseClassById} = this.props;

    requestCourseClassById(id);
  }

  render() {
    const {price} = this.props;

    return <FeesComponent {...price} />;
  }
}

const mapDispatchToProps = (dispatch) => ({
  requestCourseClassById: (id: string) => {
    dispatch({
      type: Actions.REQUEST_COURSE_CLASS,
      payload: id
    });
  }
});

const mapStateToProps = (state: IshState, ownProps: FeesProps) => {
  const course = state.courses.entities[ownProps.id] || {};

  return {
    price: {
      ...course.price,
      isPaymentGatewayEnabled: course.isPaymentGatewayEnabled
    }
  };
};

export const Fees = connect(mapStateToProps, mapDispatchToProps)(FeesContainer);

interface FeesProps {
  id: string;
  price: FeesComponentProps;
  requestCourseClassById: (id: string) => void;
}

interface FeesState {
}
