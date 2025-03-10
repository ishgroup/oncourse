/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassAttendanceType, DeliveryMode, FundingSource } from '@api/model';
import { Collapse, Divider, FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { mapSelectItems, normalizeNumber, normalizeNumberToZero } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import FormField from '../../../../../common/components/form/formFields/FormField';
import ExpandableContainer from '../../../../../common/components/layout/expandable/ExpandableContainer';
import {
  validateCourseSiteIdentifier,
  validateDefaultPurchasingContractIdentifier,
  validateDETBookingIdentifier,
  validatePurchasingContractScheduleIdentifier,
  validateVetFundingSourceState
} from '../../../../../common/utils/validation';
import { EditViewProps } from '../../../../../model/common/ListView';
import { CourseClassExtended } from '../../../../../model/entities/CourseClass';
import { State } from '../../../../../reducers/state';
import { formatFundingSourceId } from '../../../common/utils';
import { fundingSourceValues } from '../../constants';
import CourseClassAttendanceTab from '../attendance/CourseClassAttendanceTab';

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
              label={$t('do_not_report_to_avetmiss')}
              labelPlacement="start"
            />
            <FormField
              type="select"
              name="deliveryMode"
              label={$t('default_delivery_mode')}
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
                label={$t('default_funding_contract')}
                items={contracts}
                format={formatFundingSourceId}
              />
            )}
          </Grid>

          <Grid item xs={twoColumn ? 4 : 4}>
            <FormField
              type="select"
              name="fundingSource"
              label={$t('default_funding_source_national')}
              items={fundingSourceValues}
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="text"
              name="vetFundingSourceStateID"
              label={$t('default_funding_source_state')}
              validate={validateVetFundingSourceState}
            />
          </Grid>

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="text"
              name="detBookingId"
              label={$t('det_booking_identifier_nswcontracted_program_of_st')}
              validate={validateDETBookingIdentifier}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              normalize={normalizeNumber}
              name="vetCourseSiteID"
              label={$t('course_site_identifier_nsw')}
              validate={validateCourseSiteIdentifier}
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : false} />

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="text"
              name="vetPurchasingContractID"
              label={$t('default_purchasing_contract_identifier')}
              validate={validateDefaultPurchasingContractIdentifier}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="vetPurchasingContractScheduleID"
              label={$t('default_purchasing_contract_schedule_identifier')}
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
              label={$t('qualification_hours')}
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
              label={$t('nominal_hours')}
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              name="classroomHours"
              disabled
              label={$t('classroom_hours')}
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 5 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumber}
              name="studentContactHours"
              disabled
              label={$t('student_contact_hours')}
              debounced={false}
            />
          </Grid>

          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              normalize={normalizeNumberToZero}
              name="reportableHours"
              label={$t('reportable_hours')}
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
                    label={$t('type_of_attendance')}
                    items={attendanceTypeValues}
                  />
                </Grid>
                <Grid item xs={twoColumn ? 3 : 12}>
                  <FormField type="date" name="censusDate" label={$t('census_date')} />
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
