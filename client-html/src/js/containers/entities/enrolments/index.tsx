import Loadable from "@react-loadable/revised";
import * as React from "react";
import Loading from "../../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "enrolments" */ "./Enrolments"),
  loading: Loading
});

export default class LoadableEnrolments extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
