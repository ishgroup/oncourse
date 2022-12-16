/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { Grid } from "@mui/material";
import { FieldArray } from "redux-form";
import { GridSize } from "@mui/material/Grid";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] =>
  (twoColumn
    ? [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { md: 6 }, { lg: 4 }]
    : [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { md: 12 }, { lg: 12 }]);

class Directions extends React.Component<any, any> {
  render() {
    const {
      twoColumn, form, dispatch, showConfirm, isNew
    } = this.props;

    const layoutArray = getLayoutArray(twoColumn);

    return (
      <Grid container columnSpacing={3} rowSpacing={2} className="pt-3 pb-1 pl-3 pr-3">
        <Grid item xs={12}>
          <div className="heading">Directions</div>
        </Grid>
        <Grid item xs={layoutArray[0].xs}>
          <FormEditorField
            name="drivingDirections"
            label="Driving directions"
          />
        </Grid>
        <Grid item xs={layoutArray[1].xs}>
          <FormEditorField
            name="publicTransportDirections"
            label="Public transport directions"
          />
        </Grid>
        <Grid item xs={layoutArray[2].xs} className="mb-3">
          <FormEditorField
            name="specialInstructions"
            label="Special instructions"
          />
        </Grid>
        <FieldArray
          name="documents"
          label="Documents"
          entity="Site"
          component={DocumentsRenderer}
          isNew={isNew}
          xsGrid={layoutArray[3].xs}
          mdGrid={layoutArray[4].md}
          lgGrid={layoutArray[5].lg}
          dispatch={dispatch}
          form={form}
          showConfirm={showConfirm}
          rerenderOnEveryChange
        />
      </Grid>
    );
  }
}

export default Directions;
