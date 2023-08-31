import Loadable from "@react-loadable/revised";
import * as React from "react";
import Loading from "../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "finalise-period" */ "./Finalise"),
  loading: Loading
});

export default props => <LoadableComponent {...props} />;
