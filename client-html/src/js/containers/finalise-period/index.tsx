import * as React from "react";
import Loadable from "react-loadable";
import Loading from "../../common/components/layout/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "finalise-period" */ "./Finalise"),
  loading: Loading
});

export default props => <LoadableComponent {...props} />;
