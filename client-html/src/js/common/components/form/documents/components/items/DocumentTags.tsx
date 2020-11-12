import React from "react";
import { Grid } from "@material-ui/core";
import Typography from "@material-ui/core/Typography";

const DocumentTags: React.FC<any> = ({ tags, bold }) => (
  <Grid container>
    {tags.length ? (
      tags.map(t => (
        <Typography key={t.id} variant={bold ? "subtitle2" : "body2"} className="mr-1">
          #
          {t.name}
        </Typography>
      ))
    ) : (
      <Typography variant="body1" className="placeholderContent">
        No Tags
      </Typography>
    )}
  </Grid>
);

export default DocumentTags;
