/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";
import EnrolItemListRenderer from "./components/EnrolItemListRenderer";
import SaleRelations from "./components/SaleRelations";

const EnrolItemListView = React.memo<any>(props => {
  const {
    courses,
    products,
    vouchers,
    membershipProducts,
    onChangeHandler,
    disabledHandler,
    selectedItems,
    searchString,
    salesRelations
  } = props;

  const hasSalesRelations = Boolean(salesRelations.length);

  return (
    <Grid container>
      <Grid item xs={12} md={hasSalesRelations ? 6 : 12}>
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
      </Grid>
      {hasSalesRelations
        && (
        <Grid item xs={12} md={6}>
          <SaleRelations
            relations={salesRelations}
            cartItems={selectedItems}
            onSelect={onChangeHandler}
          />
        </Grid>
)}
    </Grid>
  );
});

export default EnrolItemListView;
