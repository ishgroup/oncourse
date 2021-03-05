/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Grid from "@material-ui/core/Grid";
import Collapse from "@material-ui/core/Collapse";
import {
  CourseClassAttendanceType,
  ClassFundingSource,
  DeliveryMode,
  FundingSource
} from "@api/model";
import { connect } from "react-redux";
import FormField from "../../../../../common/components/form/form-fields/FormField";
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
import { normalizeNumber, normalizeNumberToZero } from "../../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../../reducers/state";
import { formatFundingSourceId } from "../../../common/utils";
import CourseClassAttendanceTab from "../attendance/CourseClassAttendanceTab";

interface Props extends Partial<EditViewProps> {
  values?: CourseClassExtended;
  contracts?: FundingSource[];
}

const deliveryModeValues = Object.keys(DeliveryMode).map(mapSelectItems);
const attendanceTypeValues = Object.keys(CourseClassAttendanceType).map(mapSelectItems);
const fundingSourceValues = Object.keys(ClassFundingSource).map(mapSelectItems);

const CourseClassVetTab = React.memo<Props>(props => {
  const {
    contracts, tabIndex, expanded, setExpanded, twoColumn, values
  } = props;
  return (
    <div className="pl-3 pr-3">
      <ExpandableContainer index={tabIndex} expanded={expanded} setExpanded={setExpanded} header="Vet">
        <Grid container>
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
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="text"
              name="detBookingId"
              label="DET Booking Identifier (NSW)/Contracted Program of Study (WA)"
              validate={validateDETBookingIdentifier}
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              normalize={normalizeNumber}
              name="vetCourseSiteID"
              label="Course site identifier (NSW)"
              validate={validateCourseSiteIdentifier}
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : false} />

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="text"
              name="vetPurchasingContractID"
              label="Default purchasing contract identifier"
              validate={validateDefaultPurchasingContractIdentifier}
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="vetPurchasingContractScheduleID"
              label="Default purchasing contract schedule identifier"
              validate={validatePurchasingContractScheduleIdentifier}
              fullWidth
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
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              name="classroomHours"
              disabled
              label="Classroom hours"
            />
          </Grid>

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              name="studentContactHours"
              disabled
              label="Student contact hours"
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
              <Grid container className="pb-3">
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
                <Grid item xs={twoColumn ? 3 : 12}>
                  <FormField
                    type="number"
                    name="reportingPeriod"
                    label="Reporting period (Year & Semester)"
                  />
                </Grid>
              </Grid>
            </Collapse>
          </Grid>

          <CourseClassAttendanceTab {...props} showTrainingPlans />
        </Grid>
      </ExpandableContainer>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  contracts: state.export.contracts
});

export default connect<any, any, any>(mapStateToProps, null)(CourseClassVetTab);
