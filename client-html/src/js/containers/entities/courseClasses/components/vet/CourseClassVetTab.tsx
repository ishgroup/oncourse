/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import Collapse from "@mui/material/Collapse";
import {
  CourseClassAttendanceType,
  ClassFundingSource,
  DeliveryMode,
  FundingSource
} from "@api/model";
import { connect } from "react-redux";
import Divider from "@mui/material/Divider";
import FormField from "../../../../../common/components/form/formFields/FormField";
import ExpandableContainer from "../../../../../common/components/layout/expandable/ExpandableContainer";
import {
  validateCourseSiteIdentifier,
  validateDefaultPurchasingContractIdentifier,
  validateDETBookingIdentifier,
  validatePurchasingContractScheduleIdentifier,
  validateVetFundingSourceState
} from "../../../../../common/utils/validation";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";
import { mapSelectItems } from "../../../../../common/utils/common";
import { normalizeNumber, normalizeNumberToZero } from "ish-ui";
import { State } from "../../../../../reducers/state";
import { formatFundingSourceId } from "../../../common/utils";
import CourseClassAttendanceTab from "../attendance/CourseClassAttendanceTab";
import { fundingSourceValues } from "../../constants";

interface Props extends Partial<EditViewProps> {
  values?: CourseClassExtended;
  contracts?: FundingSource[];
}

const deliveryModeValues = Object.keys(DeliveryMode).map(mapSelectItems);
const attendanceTypeValues = Object.keys(CourseClassAttendanceType).map(mapSelectItems);

const CourseClassVetTab = React.memo<Props>(props => {
  const {
    contracts, tabIndex, expanded, setExpanded, twoColumn, values, syncErrors
  } = props;
  return (
    <div className="pl-3 pr-3">
      <ExpandableContainer formErrors={syncErrors} index={tabIndex} expanded={expanded} setExpanded={setExpanded} header="Vet">
        <Grid container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={4}>
            <FormControlLabel
              className="switchWrapper pb-2"
              control={<FormField type="switch" name="suppressAvetmissExport" />}
              label="Do not report to AVETMISS"
              labelPlacement="start"
            />
            <FormField
              type="select"
              name="deliveryMode"
              label="Default delivery mode"
              items={deliveryModeValues}
            />
          </Grid>

          <Grid item className="pt-2" xs={12} />

          <Grid item xs={twoColumn ? 5 : 12}>
            {contracts && (
              <FormField
                type="select"
                selectValueMark="id"
                selectLabelMark="name"
                name="relatedFundingSourceId"
                label="Default funding contract"
                items={contracts}
                format={formatFundingSourceId}
              />
            )}
          </Grid>

          <Grid item xs={twoColumn ? 4 : 4}>
            <FormField
              type="select"
              name="fundingSource"
              label="Default funding source national"
              items={fundingSourceValues}
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="text"
              name="vetFundingSourceStateID"
              label="Default funding source state"
              validate={validateVetFundingSourceState}
            />
          </Grid>

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="text"
              name="detBookingId"
              label="DET Booking Identifier (NSW)/Contracted Program of Study (WA)"
              validate={validateDETBookingIdentifier}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              normalize={normalizeNumber}
              name="vetCourseSiteID"
              label="Course site identifier (NSW)"
              validate={validateCourseSiteIdentifier}
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : false} />

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="text"
              name="vetPurchasingContractID"
              label="Default purchasing contract identifier"
              validate={validateDefaultPurchasingContractIdentifier}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="vetPurchasingContractScheduleID"
              label="Default purchasing contract schedule identifier"
              validate={validatePurchasingContractScheduleIdentifier}
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : false} />

          <Grid item className="pt-3" xs={12} />

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              name="qualificationHours"
              label="Qualification hours"
              debounced={false}
              disabled
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              disabled
              name="nominalHours"
              label="Nominal hours"
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              name="classroomHours"
              disabled
              label="Classroom hours"
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              name="studentContactHours"
              disabled
              label="Student contact hours"
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumberToZero}
              name="reportableHours"
              label="Reportable hours"
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : false} />

          <Grid item className="pb-3" xs={12} />

          <Grid item className="pt-3" xs={12}>
            <Collapse in={values.feeHelpClass}>
              <Grid container columnSpacing={3} className="pb-3">
                <Grid item xs={twoColumn ? 3 : 12}>
                  <FormField
                    type="select"
                    name="attendanceType"
                    label="Type of attendance"
                    items={attendanceTypeValues}
                  />
                </Grid>
                <Grid item xs={twoColumn ? 3 : 12}>
                  <FormField type="date" name="censusDate" label="Census date" />
                </Grid>
              </Grid>
            </Collapse>
          </Grid>

          <Grid item xs={12}>
            <CourseClassAttendanceTab {...props} showTrainingPlans />
          </Grid>
        </Grid>
      </ExpandableContainer>
      <Divider className="mb-2" />
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  contracts: state.export.contracts
});

export default connect<any, any, any>(mapStateToProps, null)(CourseClassVetTab);
