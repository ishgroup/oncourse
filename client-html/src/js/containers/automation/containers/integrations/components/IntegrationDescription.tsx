import * as React from "react";
import Typography from "@mui/material/Typography";

const IntegrationDescription = ({ item }) => (
  <div>
    <Typography variant="h5" gutterBottom component="h2">
      {item.name}
    </Typography>
    <Typography variant="body2">{item.description}</Typography>
  </div>
);

export default IntegrationDescription;
