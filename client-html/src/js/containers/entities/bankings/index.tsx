import Loadable from "@react-loadable/revised";
import * as React from "react";
import Loading from "../../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "banking" */ "./BankingListView"),
  loading: Loading
});

export default class LoadableBanking extends React.Component {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
