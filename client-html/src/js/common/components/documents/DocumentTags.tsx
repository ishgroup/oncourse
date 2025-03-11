import { Grid, Typography } from '@mui/material';
import $t from '@t';
import React from 'react';

const DocumentTags: React.FC<any> = ({ tags, bold, classes }) => (
  <Grid container columnSpacing={3}>
    {tags.length ? (
      tags.map(t => (
        <Typography key={t.id} variant={bold ? "subtitle2" : "body2"} className="mr-1">
          #
          {t.name}
        </Typography>
      ))
    ) : (
      <Typography variant="body2" className={`placeholderContent ${classes && classes.documentNoTags}`}>
        {$t('no_tags')}
      </Typography>
    )}
  </Grid>
);

export default DocumentTags;
