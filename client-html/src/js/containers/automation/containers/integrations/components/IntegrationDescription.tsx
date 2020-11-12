import * as React from "react";
import Typography from "@material-ui/core/Typography";

const IntegrationDescription = props => {
  const { item } = props;

  return (
    <div>
      <Typography variant="h5" gutterBottom={true} component="h2">
        {item.name}
      </Typography>
      <Typography variant="body2">{item.description}</Typography>
    </div>
  );
};

export default IntegrationDescription;
