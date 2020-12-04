/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";
import EnrolItemListRenderer from "./components/EnrolItemListRenderer";

const EnrolItemListView = React.memo<any>(props => {
  const {
    courses,
    products,
    vouchers,
    membershipProducts,
    onChangeHandler,
    disabledHandler,
    selectedItems,
    searchString
  } = props;

  return (
    <>
      <EnrolItemListRenderer
        type="course"
        title={`${courses.length > 1 ? "Courses" : "Course"}`}
        items={courses}
        onChangeHandler={onChangeHandler}
        disabledHandler={disabledHandler}
        primaryText={item => (searchString ? getHighlightedPartLabel(item.code, searchString) : item.code)}
        secondaryText={item => (
          <Typography component="span" variant="caption" color="textSecondary" className="ml-0-5 text-truncate">
            {searchString ? getHighlightedPartLabel(item.name, searchString) : item.name}
          </Typography>
        )}
        searchString={searchString}
        selectedItems={selectedItems}
        showFirst={8}
      />
      <EnrolItemListRenderer
        type="membership"
        title={`${membershipProducts.length > 1 ? "Memberships" : "Membership"}`}
        items={membershipProducts}
        onChangeHandler={onChangeHandler}
        disabledHandler={disabledHandler}
        primaryText={item => (searchString ? getHighlightedPartLabel(item.code, searchString) : item.code)}
        secondaryText={item => (
          <Typography component="span" variant="caption" color="textSecondary" className="ml-0-5 text-truncate">
            {searchString ? getHighlightedPartLabel(item.name, searchString) : item.name}
          </Typography>
        )}
        searchString={searchString}
        selectedItems={selectedItems}
        showFirst={8}
      />
      <EnrolItemListRenderer
        type="voucher"
        title={`${vouchers.length > 1 ? "Vouchers" : "Voucher"}`}
        items={vouchers}
        onChangeHandler={onChangeHandler}
        disabledHandler={disabledHandler}
        primaryText={item => (searchString ? getHighlightedPartLabel(item.code, searchString) : item.code)}
        secondaryText={item => (
          <Typography component="span" variant="caption" color="textSecondary" className="ml-0-5 text-truncate">
            {searchString ? getHighlightedPartLabel(item.name, searchString) : item.name}
          </Typography>
        )}
        searchString={searchString}
        selectedItems={selectedItems}
        showFirst={8}
      />
      <EnrolItemListRenderer
        type="product"
        title={`${products.length > 1 ? "Products" : "Product"}`}
        items={products}
        onChangeHandler={onChangeHandler}
        disabledHandler={disabledHandler}
        primaryText={item => (searchString ? getHighlightedPartLabel(item.code, searchString) : item.code)}
        secondaryText={item => (
          <Typography component="span" variant="caption" color="textSecondary" className="ml-0-5 text-truncate">
            {searchString ? getHighlightedPartLabel(item.name, searchString) : item.name}
          </Typography>
        )}
        searchString={searchString}
        selectedItems={selectedItems}
        showFirst={8}
      />
    </>
  );
});

export default EnrolItemListView;
