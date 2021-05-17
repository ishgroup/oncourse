/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import makeStyles from "@material-ui/core/styles/makeStyles";
import clsx from "clsx";
import React, {
 useCallback, useEffect, useMemo, useRef, useState
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
 ResponsiveContainer, Area, AreaChart, XAxis, ReferenceLine, Tooltip
} from "recharts";
import { differenceInCalendarWeeks } from "date-fns";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import withTheme from "@material-ui/core/styles/withTheme";
import green from "@material-ui/core/colors/green";
import Paper from "@material-ui/core/Paper";
import Edit from "@material-ui/icons/Edit";
import { CourseClassState } from "../../reducers";
import { State } from "../../../../../reducers/state";
import { getCourseClassEnrolments, setCourseClassEnrolments } from "../../actions";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import { AppTheme } from "../../../../../model/common/Theme";
import ChartViewSwitcher from "./ChartViewSwitcher";

interface Props {
  classId: number;
  classStart: string;
  minEnrolments: number;
  maxEnrolments: number;
  targetEnrolments?: number;
  getEnrolments?: NumberArgFunction;
  enrolmentsFetching?: boolean;
  enrolments?: CourseClassState["enrolments"];
  clearEnrolments?: any;
  theme?: AppTheme;
  openBudget?: any;
  setShowAllWeeks?: any;
  showAllWeeks?: boolean;
  hasBudget?: boolean;
}

const CustomizedTooltip = props => {
  const {
 active, payload, data, showAllWeeks
} = props;

  return active && payload[0] && (showAllWeeks ? true : payload[0].payload.week !== 0) ? (
    <Paper className="p-1">
      <Typography component="div" variant="body2" noWrap>
        <span className="mr-1">Total enrolments:</span>
        <span>{data[data.length - 1].value}</span>
      </Typography>
      <Typography component="div" variant="body2" color="textSecondary" noWrap>
        <span className="mr-1">This week enrolments:</span>
        <span>{payload[0].payload["enrolments"]}</span>
      </Typography>
    </Paper>
  ) : null;
};

const CustomizedLabel: React.FC<any> = ({
  label,
  visible,
  fill,
  coorginatesRef,
  offset,
  viewBox: {
 x, y, width, height
}
}) => (
  <text
    ref={coorginatesRef}
    x={width / 2 + x}
    y={(height ? height / 2 : 0) + y + offset}
    fill={fill}
    visibility={visible ? "visible" : "invisible"}
    textAnchor="middle"
  >
    {label}
  </text>
);

const LabelButton = ({ box, onClick }) => (
  <IconButton
    className="lightGrayIconButton absolute"
    style={{ top: box.y - box.height / 2, left: box.x + box.width }}
    onClick={onClick}
  >
    <Edit fontSize="inherit" />
  </IconButton>
);

type ChartWeeks = { week: string | number; enrolments: number; value: number }[];

const initialWeeks: ChartWeeks = [...Array(8).keys()].map((v, week) => ({
  week,
  enrolments: 0,
  value: 0
}));

const initialData = [];

for (let i = initialWeeks.length - 1; i >= 0; i--) {
  initialData.push(initialWeeks[i]);
}

initialData[initialData.length - 2].week = "start";

const chartMargin = {
 top: 8, right: 30, left: 20, bottom: 0
};

const tickFormatterFirstSix = tick => {
  switch (tick) {
    case 0:
      return "";
    case "start":
      return tick;
    default:
      return tick - 1;
  }
};

const tickFormatterAll = tick => {
  switch (tick) {
    case 0:
      return "start";
    default:
      return tick.toString();
  }
};

const CustomizedAxisTick = ({
 x, y, className, payload, fill, formatter
}) => (
  <g transform={`translate(${x},${y})`} className={className}>
    <text x={0} y={0} dy={10} textAnchor="middle" fill={fill}>
      {formatter(payload.value)}
    </text>
  </g>
);

const useStules = makeStyles({
  showAllWeeks: show => show ? { maxWidth: "unset" } : { maxWidth: "400px" },
  hasOverlay: {
    opacity: 0.2,
    pointerEvents: "none"
  },
  overlay: {
    position: "absolute",
    top: 0,
    left: 0,
    display: "flex",
    alignItems: "center",
    height: "100%",
    width: "100%",
    justifyContent: "center"
  }
})

const CourseClassEnrolmentsChart = React.memo<Props>(
  ({
    classId,
    getEnrolments,
    clearEnrolments,
    minEnrolments,
    maxEnrolments,
    targetEnrolments,
    enrolments,
    enrolmentsFetching,
    classStart,
    theme,
    openBudget,
    setShowAllWeeks,
    showAllWeeks,
    hasBudget
  }) => {
    const [data, setData] = useState<ChartWeeks>(initialData);
    const [allWeeksData, setAllWeeksData] = useState<ChartWeeks>(initialData);
    const [todayWeek, setTodayWeek] = useState(null);
    const [allWeeksTodayWeek, setAllWeeksTodayWeek] = useState(null);
    const [showLabels, setShowLabels] = useState(false);

    const hasEnrolments = Boolean(enrolments.length);

    const maxLabelEl = useRef<SVGAElement>();
    const minLabelEl = useRef<SVGAElement>();

    const classes = useStules(showAllWeeks);

    const clearData = useCallback(() => {
      setData(prev => prev.map(({ week }) => ({ week, enrolments: 0, value: 0 })));
    }, []);

    const onChartHover = useCallback(() => setShowLabels(true), []);
    const onChartLeave = useCallback(() => setShowLabels(false), []);

    useEffect(() => {
      clearData();
      clearEnrolments();
      if (todayWeek) {
        setTodayWeek(null);
      }
      if (classId) {
        getEnrolments(classId);
      }
      return () => {
        clearEnrolments();
      };
    }, [classId]);

    useEffect(() => {
      if (!enrolments.length || enrolmentsFetching) {
        return;
      }
      const classStartDate = new Date(classStart);
      const todayDate = new Date();
      todayDate.setHours(0, 0, 0, 0);

      if (todayWeek === null) {
        const weeksBeforeToday = differenceInCalendarWeeks(classStartDate, todayDate) + 1;
        if (weeksBeforeToday > 0) {
          setTodayWeek(weeksBeforeToday);
        }
      }

      const lastEnrolmentDate = new Date(enrolments[enrolments.length - 1].createdOn);
      const firstEnrolmentDate = new Date(enrolments[0].createdOn);

      const enrolmentsWeeks = differenceInCalendarWeeks(lastEnrolmentDate, firstEnrolmentDate);

      let weeks = initialWeeks.map(w => ({ ...w }));

      enrolments.forEach(e => {
        const enrolmentStartDate = new Date(e.createdOn);

        const diff = differenceInCalendarWeeks(classStartDate, enrolmentStartDate) + 1;

        if (diff >= weeks.length) {
          for (let i = 0; i < weeks.length; i++) {
            weeks[i].value++;
          }
          return;
        }

        if (diff <= 0) {
          weeks[0].enrolments++;
          weeks[0].value++;
          return;
        }

        weeks[diff].enrolments++;

        for (let i = diff; i >= 0; i--) {
          weeks[i].value++;
        }
      });

      weeks[1].week = "start";

      weeks = weeks.reverse();

      setData(weeks);

      weeks = [...Array(Math.abs(enrolmentsWeeks + 1) || 1).keys()].map((v, week) => ({
        week,
        enrolments: 0,
        value: 0
      }));

      enrolments.forEach(e => {
        const enrolmentStartDate = new Date(e.createdOn);

        const diff = differenceInCalendarWeeks(lastEnrolmentDate, enrolmentStartDate);

        if (diff >= 0 && weeks[diff]) {
          weeks[diff].enrolments++;

          for (let i = diff; i >= 0; i--) {
            weeks[i].value++;
          }
        }
      });

      weeks = weeks.reverse();

      let startWeek = differenceInCalendarWeeks(classStartDate, firstEnrolmentDate);

      if (startWeek < 0) {
        weeks.unshift(
          ...[...Array(Math.abs(startWeek)).keys()].map((v, week) => ({
            week,
            enrolments: 0,
            value: 0
          }))
        );

        weeks[0].week = 0;

        startWeek = 0;
      } else if (startWeek >= weeks.length) {
        const lastValue = weeks[weeks.length - 1].value;

        weeks.push(
          ...[...Array(startWeek + 1 - weeks.length).keys()].map((v, week) => ({
            week,
            enrolments: 0,
            value: lastValue
          }))
        );

        weeks[weeks.length - 1].week = 0;

        startWeek = weeks.length - 1;
      } else if (startWeek >= 0) {
        weeks[startWeek].week = 0;
      }

      for (let i = 0; i <= startWeek - 1; i++) {
        weeks[i].week = startWeek - i;
      }

      for (let i = 0; i < weeks.length - startWeek - 1; i++) {
        weeks[startWeek + 1 + i].week = i + 1;
      }

      setAllWeeksData(weeks);

      if (allWeeksTodayWeek === null) {
        const weeksBeforeToday = differenceInCalendarWeeks(classStartDate, todayDate);

        const targetWeek = weeks[weeks.findIndex(w => w.week === 0) - weeksBeforeToday];

        if (targetWeek) {
          setAllWeeksTodayWeek(targetWeek.week);
        }
      }
    }, [enrolments]);

    const maxLabelButton = useMemo(() => {
      if (showLabels && maxLabelEl.current) {
        return <LabelButton box={maxLabelEl.current.getBBox()} onClick={openBudget} />;
      }
      return null;
    }, [showLabels]);

    const minLabelButton = useMemo(() => {
      if (showLabels && minLabelEl.current) {
        return <LabelButton box={minLabelEl.current.getBBox()} onClick={openBudget} />;
      }
      return null;
    }, [showLabels]);

    const startWeekIndex = useMemo(() => allWeeksData.findIndex(d => d.week === 0), [allWeeksData]);

    const hasOverlay = !hasBudget || !hasEnrolments;

    return (
      <div
        onMouseEnter={onChartHover}
        onMouseLeave={onChartLeave}
        className={clsx("mt-2 relative", classes.showAllWeeks)}
      >
        {hasOverlay &&
          <div className={classes.overlay}>
            {!hasBudget && !hasEnrolments  &&
              <Typography>
                Add a budget to activate
              </Typography>
            }
            {hasBudget && !hasEnrolments &&
              <Typography>
                Waiting for enrolments
              </Typography>
            }
          </div>
        }
        <ResponsiveContainer
          width="100%"
          height={250}
          className={clsx(hasOverlay && classes.hasOverlay)}
        >
          <AreaChart data={showAllWeeks ? allWeeksData : data} margin={chartMargin}>
            <XAxis
              dataKey="week"
              tickLine={false}
              tick={props => (
                <CustomizedAxisTick {...props} formatter={showAllWeeks ? tickFormatterAll : tickFormatterFirstSix} />
              )}
              minTickGap={12}
            />
            <Area
              dataKey="value"
              type="stepAfter"
              fill={theme.palette.grey["200"]}
              stroke={theme.palette.grey["500"]}
            />
            <Tooltip
              wrapperStyle={{ zIndex: 1 }}
              content={props => <CustomizedTooltip {...props} data={data} showAllWeeks={showAllWeeks} />}
            />
            <ReferenceLine
              y={minEnrolments}
              ifOverflow="extendDomain"
              strokeDasharray="3 3"
              label={(
                <CustomizedLabel
                  label={`Min enrolments (${minEnrolments})`}
                  fill={theme.palette.text.primary}
                  visible={showLabels}
                  coorginatesRef={minLabelEl}
                />
              )}
            />
            <ReferenceLine
              y={maxEnrolments}
              ifOverflow="extendDomain"
              strokeDasharray="3 3"
              label={(
                <CustomizedLabel
                  label={`Max enrolments (${maxEnrolments})`}
                  fill={theme.palette.text.primary}
                  visible={showLabels}
                  coorginatesRef={maxLabelEl}
                />
              )}
            />
            <ReferenceLine
              y={targetEnrolments}
              ifOverflow="extendDomain"
              label={(
                <CustomizedLabel
                  label={`Enrolments to profit (${targetEnrolments})`}
                  fill={theme.palette.text.primary}
                  visible={showLabels}
                />
              )}
            />
            <ReferenceLine
              x={showAllWeeks ? (startWeekIndex !== allWeeksData.length - 1 ? startWeekIndex : 0) : "start"}
              isFront
              stroke={green[600]}
              strokeWidth={2}
            />
            <ReferenceLine
              x={showAllWeeks ? allWeeksTodayWeek : todayWeek}
              isFront
              stroke={theme.palette.secondary.main}
              strokeWidth={2}
              label={<CustomizedLabel label="Today" fill={theme.palette.text.primary} visible={showLabels} />}
            />
          </AreaChart>
        </ResponsiveContainer>

        {Boolean(enrolments.length) && (
          <ChartViewSwitcher showAllWeeks={showAllWeeks} setShowAllWeeks={setShowAllWeeks} theme={theme} />
        )}

        {maxLabelButton}
        {minLabelButton}
      </div>
    );
  }
);

const mapStateToProps = (state: State) => ({
  enrolments: state.courseClass.enrolments,
  enrolmentsFetching: state.courseClass.enrolmentsFetching
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getEnrolments: id => dispatch(getCourseClassEnrolments(id)),
    clearEnrolments: () => dispatch(setCourseClassEnrolments([]))
  });

export default connect<any, any, Props>(mapStateToProps, mapDispatchToProps)(withTheme(CourseClassEnrolmentsChart));
