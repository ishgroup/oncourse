/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  AvetmissExportOutcome,
  AvetmissExportOutcomeCategory,
  AvetmissExportOutcomeStatus,
  AvetmissExportRequest,
  AvetmissExportSettings,
  AvetmissExportType
} from '@api/model';
import Launch from '@mui/icons-material/Launch';
import LoadingButton from '@mui/lab/LoadingButton';
import { ButtonBase, Divider, Typography } from '@mui/material';
import $t from '@t';
import clsx from 'clsx';
import { openInternalLink } from 'ish-ui';
import React from 'react';
import { change } from 'redux-form';
import FormField from '../../../common/components/form/formFields/FormField';
import SpeechCard from '../../../common/components/layout/SpeechCard';
import { saveCategoryAQLLink } from '../../../common/utils/links';

interface ExtendedValues extends AvetmissExportSettings {
  defaultStatus: boolean;
}

interface Props {
  outcomes: AvetmissExportOutcome[];
  classes: any;
  values: ExtendedValues;
  dispatch: any;
  onExport: (settings: AvetmissExportRequest) => void;
  pending: boolean;
}

const types = Object.keys(AvetmissExportType);

const statuses = Object.keys(AvetmissExportOutcomeStatus);

const categories = Object.keys(AvetmissExportOutcomeCategory);

const tree = {};

types.forEach(t => {
  tree[t] = { number: 0 };

  categories.forEach(c => {
    tree[t][c] = { number: 0 };

    statuses.forEach(s => {
      tree[t][c][s] = { ids: [] };
    });
  });
});

class AvetmissExportResults extends React.Component<Props, any> {
  private outcomeIds: number[] = [];

  private enrolmentIds: number[] = [];

  private isOpeningFindRelated: boolean = false;

  constructor(props) {
    super(props);

    const state = JSON.parse(JSON.stringify(tree));

    props.outcomes.forEach((i: AvetmissExportOutcome) => {
      if (i.type === "outcome") {
        this.outcomeIds = [...this.outcomeIds, ...i.ids];
      }
      if (i.type === "enrolment") {
        this.enrolmentIds = [...this.enrolmentIds, ...i.ids];
      }

      state[i.type][i.category].number++;

      state[i.type][i.category][i.status] = i;
    });

    this.state = state;

    this.props.dispatch(change("AvetmissExportForm", "defaultStatus", false));
  }

  openLink = item => {
    if (!this.isOpeningFindRelated) {
      this.isOpeningFindRelated = true;
      const aql = `id in (${item.ids})`;
      let url = `/${item.type}?search=${aql}`;
      if (url.length >= 2048) {
        const id = `f${(+new Date).toString(16)}`;
        saveCategoryAQLLink({ AQL: aql, id, action: "add" });
        url = `/${item.type}?customSearch=${id}`;
      }

      setTimeout(() => {
        openInternalLink(url);
        this.isOpeningFindRelated = false;
      }, 400);
    }
  };

  export = outcome => {
    const { values } = this.props;
    const exportObj: AvetmissExportRequest = {};

    exportObj.ids = [];

    Object.keys(outcome).forEach(i => {
      if (outcome[i].number) {
        Object.keys(outcome[i]).forEach(j => {
          if (j !== "number" && outcome[i][j].ids.length) {
            outcome[i][j].ids.forEach(id => exportObj.ids.push(id));
          }
        });
      }
    });
    exportObj.defaultStatus = values.defaultStatus;

    const settings = { ...values };
    delete settings.defaultStatus;
    delete settings.noAssessment;
    exportObj.settings = settings;
    exportObj.settings.noAssessment = outcome["Started (not assessed)"].number ? values.noAssessment : true;

    this.props.onExport(exportObj);
  };

  render() {
    const { classes, pending, values } = this.props;

    const { outcome, enrolment } = this.state;

    return (
      <>
        <div className="centeredFlex">
          <div className={clsx("d-flex", classes.exportHeadersContainer)} />
        </div>

        <Divider className={classes.divider} />

        <div className="d-flex">
          <div className="flex-fill" />

          <SpeechCard
            className={clsx({
              [classes.hidden]: !outcome["Not yet started"].number
            })}
          >
            <Typography color="inherit" className={clsx("heading", classes.resultsHeader)}>
              {this.outcomeIds.length}
              {$t('outcomes')}
            </Typography>

            <Typography variant="body1">
              <strong>{$t('not_yet_started')}</strong>
            </Typography>

            {Object.keys(outcome["Not yet started"]).map(k => {
              const item = outcome["Not yet started"][k];

              if (k !== "number" && item.ids.length) {
                return (
                  <ButtonBase
                    className="coloredHover d-flex"
                    key={k + item.ids.length}
                    onClick={() => this.openLink(item)}
                  >
                    <Typography className={classes.recordContainer}>
                      {item.ids.length}
                      {' '}
                      {k}
                      {' '}
                      <Launch className={classes.recordIcon} />
                    </Typography>
                  </ButtonBase>
                );
              }
              return null;
            })}

            <div className="d-flex mt-2">
              <Typography>
                {$t('export_as_br_not_yet_started_85')}
              </Typography>
            </div>
          </SpeechCard>

          <div className={classes.stepsArrowContainer}>
            <div className={classes.stepWrapper}>
              <div className={classes.step}>
                <Typography color="inherit">{$t('enrol')}</Typography>
              </div>
            </div>
            <div className={classes.arrowLine} />
          </div>

          <SpeechCard
            leftSide
            className={clsx({
              [classes.hidden]: !enrolment["Not yet started"].number
            })}
          >
            <Typography color="inherit" className={clsx("heading", classes.resultsHeader)}>
              {this.enrolmentIds.length}
              {$t('enrolments3')}
            </Typography>

            <Typography variant="body1">
              <strong>{$t('not_yet_started')}</strong>
            </Typography>

            {Object.keys(enrolment["Not yet started"]).map(k => {
              const item = enrolment["Not yet started"][k];

              if (k !== "number" && item.ids.length) {
                return (
                  <ButtonBase
                    className="coloredHover d-flex"
                    key={k + item.ids.length}
                    onClick={() => this.openLink(item)}
                  >
                    <Typography className={classes.recordContainer}>
                      {item.ids.length}
                      {' '}
                      {k}
                      {' '}
                      <Launch className={classes.recordIcon} />
                    </Typography>
                  </ButtonBase>
                );
              }
              return null;
            })}
          </SpeechCard>

          <div className="flex-fill" />
        </div>

        <div className="d-flex">
          <div className="flex-fill" />

          <div>
            <SpeechCard
              className={clsx({
              [classes.hidden]: !outcome["Started (not assessed)"].number
            })}
            >
              <Typography variant="body1">
                <strong>{$t('started_not_assessed')}</strong>
              </Typography>

              {Object.keys(outcome["Started (not assessed)"]).map(k => {
              const item = outcome["Started (not assessed)"][k];

              if (k !== "number" && item.ids.length) {
                return (
                  <ButtonBase
                    className="coloredHover d-flex"
                    key={k + item.ids.length}
                    onClick={() => this.openLink(item)}
                  >
                    <Typography className={classes.recordContainer}>
                      {item.ids.length}
                      {' '}
                      {k}
                      {' '}
                      <Launch className={classes.recordIcon} />
                    </Typography>
                  </ButtonBase>
                );
              }
              return null;
            })}

              <div className="d-flex mt-2 mb-2">
                <Typography color={!values.noAssessment ? "textSecondary" : undefined}>
                  {$t('dont_require_assessment')}
                </Typography>

                <FormField
                  type="switch"
                  name="noAssessment"
                  color="primary"
                  inline
                />
              </div>

              <Typography>
                {values.noAssessment ? "export as continuing (70)" : "export as starting in 7 days (85)"}
              </Typography>
            </SpeechCard>

            <SpeechCard
              className={clsx({
                [classes.hidden]: !outcome["Commenced"].number
              })}
            >
              <Typography variant="body1">
                <strong>{$t('commenced2')}</strong>
              </Typography>

              {Object.keys(outcome["Commenced"]).map(k => {
                const item = outcome["Commenced"][k];

                if (k !== "number" && item.ids.length) {
                  return (
                    <ButtonBase
                      className="coloredHover d-flex"
                      key={k + item.ids.length}
                      onClick={() => this.openLink(item)}
                    >
                      <Typography className={classes.recordContainer}>
                        {item.ids.length}
                        {' '}
                        {k}
                        {' '}
                        <Launch className={classes.recordIcon} />
                      </Typography>
                    </ButtonBase>
                  );
                }
                return null;
              })}

              <Typography className="mt-1">{$t('export_as_continuing_70')}</Typography>
            </SpeechCard>
          </div>

          <div className={classes.stepsArrowContainer}>
            <div className={classes.stepWrapper}>
              <div className={classes.step}>
                <Typography color="inherit">{$t('start2')}</Typography>
              </div>
            </div>
            <div className={classes.arrowLine} />
          </div>

          <SpeechCard
            leftSide
            className={clsx({
              [classes.hidden]: !enrolment["Commenced"].number
            })}
          >
            <Typography variant="body1">
              <strong>{$t('commenced2')}</strong>
            </Typography>
            {Object.keys(enrolment["Commenced"]).map(k => {
              const item = enrolment["Commenced"][k];

              if (k !== "number" && item.ids.length) {
                return (
                  <ButtonBase
                    className="coloredHover d-flex"
                    key={k + item.ids.length}
                    onClick={() => this.openLink(item)}
                  >
                    <Typography className={classes.recordContainer}>
                      {item.ids.length}
                      {' '}
                      {k}
                      {' '}
                      <Launch className={classes.recordIcon} />
                    </Typography>
                  </ButtonBase>
                );
              }
              return null;
            })}
          </SpeechCard>

          <div className="flex-fill" />
        </div>

        <div className="d-flex">
          <div className="flex-fill" />
          <SpeechCard
            className={clsx({
              [classes.hidden]: !outcome["Delivered"].number
            })}
          >
            <Typography variant="body1">
              <strong>{$t('delivered')}</strong>
            </Typography>

            {Object.keys(outcome["Delivered"]).map(k => {
              const item = outcome["Delivered"][k];

              if (k !== "number" && item.ids.length) {
                return (
                  <ButtonBase
                    className="coloredHover d-flex"
                    key={k + item.ids.length}
                    onClick={() => this.openLink(item)}
                  >
                    <Typography className={classes.recordContainer}>
                      {item.ids.length}
                      {' '}
                      {k}
                      {' '}
                      <Launch className={classes.recordIcon} />
                    </Typography>
                  </ButtonBase>
                );
              }
              return null;
            })}

            <div className="d-flex mt-2">
              <Typography color={!values.defaultStatus ? "textSecondary" : undefined}>
                {$t('export_as_continuing_70_br_ending_7_days_br_from_n')}
              </Typography>

              <FormField
                type="switch"
                name="defaultStatus"
                color="primary"
                inline
              />
            </div>
          </SpeechCard>

          <div className={classes.stepsArrowContainer}>
            <div className={classes.stepWrapper}>
              <div className={classes.step}>
                <Typography color="inherit">{$t('end2')}</Typography>
              </div>
            </div>
            <div className={classes.arrowLine} />
          </div>

          <SpeechCard
            leftSide
            className={clsx({
              [classes.hidden]: !enrolment["Delivered"].number
            })}
          >
            <Typography variant="body1">
              <strong>{$t('delivered')}</strong>
            </Typography>

            {Object.keys(enrolment["Delivered"]).map(k => {
              const item = enrolment["Delivered"][k];

              if (k !== "number" && item.ids.length) {
                return (
                  <ButtonBase
                    className="coloredHover d-flex"
                    key={k + item.ids.length}
                    onClick={() => this.openLink(item)}
                  >
                    <Typography className={classes.recordContainer}>
                      {item.ids.length}
                      {' '}
                      {k}
                      {' '}
                      <Launch className={classes.recordIcon} />
                    </Typography>
                  </ButtonBase>
                );
              }
              return null;
            })}
          </SpeechCard>

          <div className="flex-fill" />
        </div>

        <div className="d-flex">
          <div className="flex-fill" />

          <SpeechCard
            className={clsx({
              [classes.hidden]: !outcome["Final Status"].number
            })}
          >
            <Typography variant="body1">
              <strong>{$t('final_status')}</strong>
            </Typography>

            {Object.keys(outcome["Final Status"]).map(k => {
              const item = outcome["Final Status"][k];

              if (k !== "number" && item.ids.length) {
                return (
                  <ButtonBase
                    className="coloredHover d-flex"
                    key={k + item.ids.length}
                    onClick={() => this.openLink(item)}
                  >
                    <Typography className={classes.recordContainer}>
                      {item.ids.length}
                      {' '}
                      {k}
                      {' '}
                      <Launch  className={classes.recordIcon} />
                    </Typography>
                  </ButtonBase>
                );
              }
              return null;
            })}
          </SpeechCard>

          <div className={classes.stepsArrowContainer}>
            <div className={classes.stepWrapper}>
              <div className={classes.step}>
                <Typography color="inherit">{$t('verify')}</Typography>
              </div>
            </div>
            <div className={classes.arrowLine} />
          </div>

          <div>
            <SpeechCard
              leftSide
              className={clsx({
                [classes.hidden]: !enrolment["All outcomes final"].number
              })}
            >
              <Typography variant="body1">
                <strong>{$t('all_outcomes_final')}</strong>
              </Typography>

              {Object.keys(enrolment["All outcomes final"]).map(k => {
                const item = enrolment["All outcomes final"][k];

                if (k !== "number" && item.ids.length) {
                  return (
                    <ButtonBase
                      className="coloredHover d-flex"
                      key={k + item.ids.length}
                      onClick={() => this.openLink(item)}
                    >
                      <Typography className={classes.recordContainer}>
                        {item.ids.length}
                        {' '}
                        {k}
                        {' '}
                        <Launch className={classes.recordIcon} />
                      </Typography>
                    </ButtonBase>
                  );
                }
                return null;
              })}
            </SpeechCard>

            <SpeechCard
              leftSide
              className={clsx({
                [classes.hidden]: !enrolment["Issued"].number
              })}
            >
              <Typography variant="body1">
                <strong>{$t('issued')}</strong>
              </Typography>

              {Object.keys(enrolment["Issued"]).map(k => {
                const item = enrolment["Issued"][k];

                if (k !== "number" && item.ids.length) {
                  return (
                    <ButtonBase
                      className="coloredHover d-flex"
                      key={k + item.ids.length}
                      onClick={() => this.openLink(item)}
                    >
                      <Typography className={classes.recordContainer}>
                        {item.ids.length}
                        {' '}
                        {k}
                        {' '}
                        <Launch className={classes.recordIcon} />
                      </Typography>
                    </ButtonBase>
                  );
                }
                return null;
              })}
            </SpeechCard>
          </div>

          <div className="flex-fill" />
        </div>

        <div className="d-flex">
          <div className="flex-fill" />

          <div className={classes.stepsArrowContainer}>
            <div className={classes.stepsArrowHead} />
          </div>

          <div className="flex-fill" />
        </div>

        <div className="centeredFlex">
          <div className="flex-fill" />
          <LoadingButton
            variant="contained"
            color="primary"
            onClick={() => this.export(outcome)}
            loading={pending}
          >
            {$t('export')}
          </LoadingButton>
        </div>
      </>
    );
  }
}

export default AvetmissExportResults;
