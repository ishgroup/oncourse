import React from "react";
import Loadable from "@react-loadable/revised";
import Loading from "../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "automation" */ "./Automation"),
  loading: Loading
});

export default function LoadableAutomation(props) {
  return <LoadableComponent {...props} />;
}
