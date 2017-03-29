import * as React from "react";
import {connect} from "react-redux";
import withRouter from "react-router/lib/withRouter";
import {ReactRouter} from "../types";
import {LogMessage, Logger, Level} from "../services/Logger";
import {IshActions} from "../constants/IshActions";

class CoursePageComponent extends React.Component<CoursePageProps, CoursePageState> {
  constructor() {
    super();
  }

  componentDidMount() {
    const {location, requestStudentByUniqueId} = this.props;

    const studentUniqueIdentifier = location.query.studentUniqueIdentifier;

    if (studentUniqueIdentifier != undefined) {
      Logger.log(new LogMessage(
        Level.DEBUG,
        "Page with studentUniqueIdentifier requested.",
        [{studentUniqueIdentifier}])
      );
      requestStudentByUniqueId(studentUniqueIdentifier);
    }
  }

  /**
   * We doesn't render anything here
   */
  render() {
    return null;
  }
}

const mapDispatchToProps = (dispatch) => ({
  requestStudentByUniqueId: (id: string) => {
    dispatch({
      type: IshActions.REQUEST_CONTACT,
      id
    });
  }
});


const mapStateToProps = (state) => ({});

export const CoursePage = withRouter(connect(mapStateToProps, mapDispatchToProps)(CoursePageComponent));

interface CoursePageParams {
  course: string;
}

interface CoursePageQueryParams {
  studentUniqueIdentifier: string;
}


interface CoursePageProps {
  requestStudentByUniqueId: (id: string) => void;
  params?: CoursePageParams;
  location?: {
    query: CoursePageQueryParams
    pathname: string;
  };
  router?: ReactRouter;
}

interface CoursePageState {
}
