import * as React from "react";
import Loadable from "@react-loadable/revised";
import Loading from "../../../common/components/layout/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "assessment" */ "./Assessments"),
  loading: Loading
});

export default class LoadableAssessments extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
