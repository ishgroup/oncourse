/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Loadable from "@react-loadable/revised";
import * as React from "react";
import Loading from "../../common/components/progress/Loading";

const LoadableComponent = Loadable({
  loader: () => import(/* webpackChunkName: "notfound" */ "./NotFound"),
  loading: Loading
});

export default class LoadableNotFound extends React.Component<any, any> {
  render() {
    return <LoadableComponent {...this.props} />;
  }
}
