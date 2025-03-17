import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React from 'react';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';

const PriorLearningsHeaderBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row } = props;

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <div>
        <Typography variant="subtitle2" noWrap>
          {row.title}
        </Typography>
      </div>
    </div>
  );
});

export const PriorLearningsHeaderLine = PriorLearningsHeaderBase;

export const PriorLearningsContentLine: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row } = props;

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={6}>
        <Uneditable value={row.externalRef} label={$t('external_reference')} />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.qualName} label={$t('qual_name')} />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.qualNationalCode} label={$t('qual_national_code')} />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.qualLevel} label={$t('qual_level')} />
      </Grid>
    </Grid>
  );
});
