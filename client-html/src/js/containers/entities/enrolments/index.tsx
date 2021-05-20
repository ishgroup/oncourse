import * as React from "react";
import Loadable from "@react-loadable/revised";
import Loading from "../../../common/components/layout/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "enrolments" */ "./Enrolments"),
  loading: Loading
});

export default class LoadableEnrolments extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
