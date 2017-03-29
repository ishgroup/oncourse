import * as React from "react";
import {connect} from "react-redux";
import {FeesComponent, FeesComponentProps} from "../components/fees/FeesComponent";
import {IshState} from "../services/IshState";
import {IshActions} from "../constants/IshActions";
import {debug} from "util";

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
      type: IshActions.REQUEST_COURSE_CLASS,
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
