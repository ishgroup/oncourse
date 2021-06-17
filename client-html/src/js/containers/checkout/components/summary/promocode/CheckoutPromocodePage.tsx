/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import LoadingIndicator from "../../../../../common/components/layout/LoadingIndicator";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import CheckoutAppBar from "../../CheckoutAppBar";

interface Props {
  activeField?: any;
  titles?: any;
}

const CheckoutPromocodePage = React.memo<Props>(props => {
  const { activeField, titles } = props;
  return (
    <div className="appFrame flex-fill root">
      <LoadingIndicator />
      <CustomAppBar>
        <CheckoutAppBar title={activeField && titles[activeField]} />
      </CustomAppBar>

      <div className="appBarContainer w-100 p-3" />
    </div>
  );
});

export default CheckoutPromocodePage;
