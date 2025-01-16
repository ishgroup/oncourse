import Loadable from "@react-loadable/revised";
import React from "react";
import Loading from "../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "automation" */ "./Automation"),
  loading: Loading
});

export default props => (
  <LoadableComponent {...props} />
);
