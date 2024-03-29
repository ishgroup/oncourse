import Loadable from "@react-loadable/revised";
import React from "react";
import Loading from "../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "audit" */ "./AuditsApp"),
  loading: Loading
});

export default class LoadableAudits extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
