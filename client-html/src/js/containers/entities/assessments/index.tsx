import Loadable from "@react-loadable/revised";
import * as React from "react";
import Loading from "../../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "assessment" */ "./Assessments"),
  loading: Loading
});

export default class LoadableAssessments extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
