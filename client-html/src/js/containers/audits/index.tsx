import * as React from "react";
import Loadable from "@react-loadable/revised";
import Loading from "../../common/components/layout/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "audit" */ "./AuditsApp"),
  loading: Loading
});

export default class LoadableAudits extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
