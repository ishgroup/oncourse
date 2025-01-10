import Loadable from "@react-loadable/revised";
import * as React from "react";
import Loading from "../../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "survey" */ "./Survey"),
  loading: Loading
});

export default class LoadableScripts extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
