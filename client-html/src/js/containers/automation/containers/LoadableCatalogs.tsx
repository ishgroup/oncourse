import Loadable from "@react-loadable/revised";
import React from "react";
import Loading from "../../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "integrations" */  "./Catalogs").then(({ IntegrationsCatalog }) => IntegrationsCatalog),
  loading: Loading
});

export function LoadableIntegrations(props) {
  return <LoadableComponent {...props} />;
}
