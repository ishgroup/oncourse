/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Currency, StatisticData } from '@api/model';
import { Person } from '@mui/icons-material';
import { Grid, List, ListItem, Typography } from '@mui/material';
import Paper from '@mui/material/Paper';
import { alpha } from '@mui/material/styles';
import $t from '@t';
import clsx from 'clsx';
import { AnyArgFunction, formatCurrency, openInternalLink } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import AutoSizer from 'react-virtualized-auto-sizer';
import { Area, AreaChart, CartesianGrid, Tooltip, XAxis } from 'recharts';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import { checkPermissions } from '../../../../../common/actions';
import { IAction } from '../../../../../common/actions/IshAction';
import { State } from '../../../../../reducers/state';
import { getDashboardStatistic } from '../../../actions';
import ScriptStatistic from './ScriptStatistic';

const styles = theme => ({
    root: {
      padding: theme.spacing(3),
      alignContent: "flex-start"
    },
    totalText: {
      color: alpha(theme.palette.text.primary, 0.5),
      display: "flex",
      alignItems: "baseline",
      "& span": {
        color: theme.palette.text.primary,
        fontWeight: "bold",
        "&:first-of-type": {
          marginRight: "8px"
        }
      },
      "& svg": {
        fontSize: "18px",
        alignSelf: "center",
        marginBottom: "1px"
      },
      "& strong": {
        fontSize: "16px",
        margin: "0 0.2em"
      }
    },
    smallTextGroup: {
      display: "flex",
      justifyContent: "space-between",
      padding: "0",
      alignItems: "flex-start"
    },
    statisticGroup: {
      padding: "8px 8px 0"
    },
    containerStatisticGroup: {
      width: "calc(100% + 16px)",
      margin: "-8px"
    },
    smallText: {
      fontSize: "12px"
    },
    grayText: {
      color: alpha(theme.palette.text.primary, 0.4)
    },
    coloredHeaderText: {
      color: theme.statistics.coloredHeaderText.color
    },
    pastWeeksCaption: {
      display: "flex",
      alignItems: "center"
    },
    enrolmentsColor: {
      color: theme.statistics.enrolmentText.color
    },
    revenueColor: {
      color: theme.statistics.revenueText.color
    },
    leftColumn: {
      flex: "1 1 auto",
      marginRight: theme.spacing(3)
    },
    rightColumn: {
      flex: "0 0 auto",
      color: theme.statistics.rightColumn.color
    },
    displayBlock: {
      display: "block",
    },
    doneIcon: {
      color: "#018759",
    },
    failedIcon: {
      color: "#DE340C",
    },
    smallScriptGroup: {
      display: "flex",
      padding: "0",
    },
    smallScriptText: {
      fontSize: "12px",
      marginRight: "12px",
    },
    headingMargin: {
      margin: "10px 0",
    }
  });

const TotalStatisticInfo = props => {
  const {
    totalStudents, totalEnrolments, classes, currency
  } = props;

  return (
    <div style={{ display: "flex", justifyContent: "center" }}>
      <Typography className={clsx(classes.totalText)}>
        <Person className={classes.enrolmentsColor} />
        <span>{totalStudents}</span>
        <strong className={classes.revenueColor}>{(currency && totalEnrolments !== null) && currency.shortCurrencySymbol}</strong>
        {totalEnrolments && (<span className="money">{formatCurrency(totalEnrolments, "")}</span>)}
      </Typography>
    </div>
  );
};

const ChartTooltip = args => {
  const { payload, active } = args;

  return active && payload ? (
    <Paper className="p-1">
      {payload.map((i, n) => (
        <Typography key={n} noWrap>
          <span style={{ color: i.color }} className="mr-1">
            {i.name}
            :
          </span>
          <span className={n === 0 ? "money" : undefined}>{i.payload[`${i.name}Value`]}</span>
        </Typography>
      ))}
    </Paper>
  ) : null;
};

const Chart = props => (
  <AutoSizer disableHeight>
    {({ width }) => (
      <AreaChart
        width={width}
        height={200}
        data={props.data}
        margin={{
         top: 8, right: 0, left: 0, bottom: 8
        }}
      >
        <XAxis hide interval={0} />
        <CartesianGrid strokeDasharray="3 3" />
        <Tooltip content={ChartTooltip} wrapperStyle={{ outline: "none" }} />
        <Area type="monotone" dataKey="Revenue" stackId="1" stroke="#ffd876" fill="#ffd876" />
        <Area type="monotone" dataKey="Enrolments" stackId="1" stroke="#73cba7" fill="#73cba7" />
      </AreaChart>
      )}
  </AutoSizer>
  );

const preformatChartNumbers = (values: number[], reduceTo?: number) => {
  const max = Math.max(...values);

  let formatted = values;

  if (max) {
    formatted = values.map(i => Math.round((i / max) * reduceTo * 1000) / 1000);
  }

  return formatted;
};

interface Props {
  classes?: any;
  getStatistic?: AnyArgFunction;
  statisticData?: StatisticData;
  getCurrency?: AnyArgFunction;
  currency?: Currency;
  isUpdating?: boolean;
  hideChart?: boolean;
  hasAuditPermissions?: boolean;
  dispatch?: Dispatch<IAction>;
  getAuditPermissions?: () => void;
}

class Statistics extends React.Component<Props, any> {
  private interval;

  constructor(props) {
    super(props);

    this.state = {
      chartData: []
    };
  }

  componentDidMount() {
    const { getStatistic, getAuditPermissions } = this.props;
    getStatistic();

    // Statistic update interval
    this.interval = setInterval(getStatistic, 120000);
    getAuditPermissions();
  }

  componentDidUpdate(prevProps) {
    const { statisticData } = this.props;

    if (!prevProps.statisticData && statisticData) {
      this.setState({
        chartData: this.preformatChartValues(statisticData.enrolmentsChartLine, statisticData.revenueChartLine)
      });
    }

    if (prevProps.isUpdating && !this.props.isUpdating) {
      this.setState({
        chartData: this.preformatChartValues(statisticData.enrolmentsChartLine, statisticData.revenueChartLine)
      });
    }
  }

  componentWillUnmount() {
    clearInterval(this.interval);
  }

  preformatChartValues = (enrolments, revenue) => {
    const formattedEnrolmentsValues = preformatChartNumbers(enrolments, 0.9);

    const formattedRevenueValues = preformatChartNumbers(revenue, 2.8);

    return formattedEnrolmentsValues.map((e, i) => ({
      Revenue: formattedRevenueValues[i],
      Enrolments: e,
      RevenueValue: formatCurrency(revenue[i], this.props.currency ? this.props.currency.shortCurrencySymbol : ""),
      EnrolmentsValue: enrolments[i]
    }));
  };

  render() {
    const {
      classes, hasAuditPermissions, statisticData, currency, hideChart, dispatch
    } = this.props;

    const { chartData } = this.state;

    return (
      <>
        {statisticData && !hideChart ? (
          <Grid container className={classes.root}>
            <Grid item className="w-100 d-flex">
              <Typography className="heading flex-fill">{statisticData.moneyCount !== null ? 'Enrolments & Revenue' : 'Enrolments'}</Typography>
              <Typography variant="caption">{$t('past_4_weeks')}</Typography>
            </Grid>
            <Grid item xs={12}>
              <Chart data={chartData} />
            </Grid>
            <Grid item xs={12}>
              <TotalStatisticInfo
                totalStudents={statisticData.studentsCount}
                totalEnrolments={statisticData.moneyCount}
                classes={classes}
                currency={currency}
              />
            </Grid>
            {Boolean(statisticData.latestEnrolments?.length) && <Grid item xs={12} className="mt-2">
              <Typography className={clsx(classes.coloredHeaderText, classes.marginBottom, classes.smallText)}>
                {$t('last_enrolments')}
              </Typography>
              <List dense disablePadding>
                {statisticData.latestEnrolments.map((e, index) => (
                <ListItem key={index} dense disableGutters className={classes.smallTextGroup}>
                  <Typography
                    onClick={() => openInternalLink(e.link)}
                    className={clsx(classes.smallText, "linkDecoration", classes.leftColumn)}
                  >
                    {e.title}
                  </Typography>

                  <Typography className={clsx(classes.smallText, classes.grayText, classes.rightColumn)}>
                    {e.info}
                  </Typography>
                </ListItem>
              ))}
              </List>
            </Grid>}
            {Boolean(statisticData.latestWaitingLists?.length)
              && <Grid item xs={12} className="mt-2">
                <Typography className={clsx(classes.coloredHeaderText, classes.marginBottom, classes.smallText)}>
                  {$t('largest_waiting_lists')}
                </Typography>
                <List dense disablePadding>
                  {statisticData.latestWaitingLists.map((e, index) => (
                    <ListItem key={index} dense disableGutters className={classes.smallTextGroup}>
                      <Typography
                        onClick={() => openInternalLink(e.link)}
                        className={clsx(classes.smallText, "linkDecoration", classes.leftColumn)}
                      >
                        {e.title}
                      </Typography>
                      <Typography className={clsx(classes.smallText, classes.grayText, classes.rightColumn)}>
                        {e.info}
                      </Typography>
                    </ListItem>
                  ))}
                </List>
              </Grid>}
            <Grid item xs={12} className="mt-2">
              <Typography className={clsx(classes.coloredHeaderText, classes.marginBottom, classes.smallText)}>
                {statisticData.openedClasses}
                {$t('classes_open_for_enrolment')}
              </Typography>
              <List dense disablePadding>
                <Grid container className={classes.containerStatisticGroup}>
                  <Grid item className={classes.statisticGroup} xs={6}>
                    <ListItem dense disableGutters className={classes.smallTextGroup}>
                      <Typography className={classes.smallText}>
                        {statisticData.inDevelopmentClasses}
                        {$t('preparing')}
                      </Typography>
                    </ListItem>
                  </Grid>
                  <Grid item className={classes.statisticGroup} xs={6}>
                    <ListItem dense disableGutters className={classes.smallTextGroup}>
                      <Typography className={classes.smallText}>
                        {statisticData.cancelledClasses}
                        {$t('cancelled')}
                      </Typography>
                    </ListItem>
                  </Grid>
                </Grid>
                <Grid container className={classes.containerStatisticGroup}>
                  <Grid item className={classes.statisticGroup} xs={6}>
                    <ListItem dense disableGutters className={classes.smallTextGroup}>
                      <Typography className={classes.smallText}>
                        {statisticData.completedClasses}
                        {$t('completed')}
                      </Typography>
                    </ListItem>
                  </Grid>
                  <Grid item className={classes.statisticGroup} xs={6}>
                    <ListItem dense disableGutters className={classes.smallTextGroup}>
                      <Typography className={classes.smallText}>
                        {statisticData.commencedClasses}
                        {$t('commenced')}
                      </Typography>
                    </ListItem>
                  </Grid>
                </Grid>
              </List>
            </Grid>
          </Grid>
      ) : null}
      {hasAuditPermissions && (
        <Grid item xs={12} className="mt-2 p-3">
          <Typography className={clsx("heading", classes.headingMargin)}>
            {$t('automation_status')}
          </Typography>
          <ScriptStatistic dispatch={dispatch} />
        </Grid>
      )}
    </>
);
  }
}

const mapStateToProps = (state: State) => ({
  statisticData: state.dashboard.statistics.data,
  isUpdating: state.dashboard.statistics.updating,
  hasAuditPermissions: state.access["/a/v1/list/plain?entity=Audit"] && state.access["/a/v1/list/plain?entity=Audit"]["GET"],
  currency: state.location.currency
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  getStatistic: () => dispatch(getDashboardStatistic()),
  getAuditPermissions: () => dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Audit", method: "GET" })),
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(Statistics, styles));
