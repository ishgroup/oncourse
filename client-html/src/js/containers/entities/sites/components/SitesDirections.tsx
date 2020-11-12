/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { Grid } from "@material-ui/core";
import { FieldArray } from "redux-form";
import { GridSize } from "@material-ui/core/Grid";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] =>
  (twoColumn
    ? [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { md: 6 }, { lg: 4 }]
    : [{ xs: 12 }, { xs: 12 }, { xs: 12 }, { xs: 12 }, { md: 12 }, { lg: 12 }]);

class Directions extends React.Component<any, any> {
  render() {
    const {
      twoColumn, form, dispatch, showConfirm
    } = this.props;

    const layoutArray = getLayoutArray(twoColumn);
    return (
      <>
        <div className="heading p-3 pt-2 pb-0">Directions</div>
        <Grid container className="h-100 justify-content-center p-3" alignItems="flex-start" alignContent="flex-start">
          <Grid item xs={layoutArray[0].xs} className="mb-2">
            <FormEditorField
              name="drivingDirections"
              label="Driving directions"
            />
          </Grid>
          <Grid item xs={layoutArray[1].xs} className="mb-2">
            <FormEditorField
              name="publicTransportDirections"
              label="Public transport directions"
            />
          </Grid>
          <Grid item xs={layoutArray[2].xs} className="mb-2">
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
            xsGrid={layoutArray[3].xs}
            mdGrid={layoutArray[4].md}
            lgGrid={layoutArray[5].lg}
            dispatch={dispatch}
            form={form}
            showConfirm={showConfirm}
            rerenderOnEveryChange
          />
        </Grid>
      </>
    );
  }
}

export default Directions;
