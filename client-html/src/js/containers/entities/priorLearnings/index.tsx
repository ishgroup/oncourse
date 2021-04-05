import * as React from "react";
import Loadable from "@react-loadable/revised";
import Loading from "../../../common/components/layout/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "priorLearnings" */ "./PriorLearnings"),
  loading: Loading
});

export default class LoadablePriorLearnings extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
