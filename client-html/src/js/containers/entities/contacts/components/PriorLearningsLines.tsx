import React from "react";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";

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
        <Uneditable value={row.externalRef} label="External reference" />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.qualName} label="Qual name" />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.qualNationalCode} label="Qual national code" />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.qualLevel} label="Qual level" />
      </Grid>
    </Grid>
  );
});
