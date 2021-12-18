/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import HelpOutlineOutlinedIcon from "@mui/icons-material/HelpOutlineOutlined";

const styles = createStyles(() => ({
  cardRoot: {
    background: "#f7f7f7",
  }
}));

const OfferMessage: React.FC<any> = props => {
  const { classes } = props;
  return (
    <div className="p-2 mb-1">
      <Card className={classes.cardRoot}>
        <CardContent>
          <div className="d-flex align-items-center mb-1">
            <HelpOutlineOutlinedIcon fontSize="medium" color="primary" className="mr-0-5" />
            <Typography variant="body2">
              <strong>Did you know...</strong>
            </Typography>
          </div>
          <Typography variant="body2" component="div" className="mb-1">
            Send automated emails or SMS messages using our
            {" "}
            <span className="primaryColor cursor-pointer text-decoration-underline">Script Engine</span>
          </Typography>
          <Typography variant="body2" component="div" className="mb-1">
            <span className="primaryColor cursor-pointer text-decoration-underline">Track Leads</span>
            {" "}
            at every touch point
          </Typography>
          <Typography variant="body2" component="div">
            Offer
            {" "}
            <span className="primaryColor cursor-pointer text-decoration-underline">Discounts</span>
            {" "}
            of virtually any kind
          </Typography>
        </CardContent>
      </Card>
    </div>
  );
};

export default withStyles(styles)(OfferMessage);
