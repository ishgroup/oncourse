/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
 useCallback, useEffect, useMemo, useRef, useState
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
 ResponsiveContainer, Area, AreaChart, XAxis, ReferenceLine, Tooltip
} from "recharts";
import { differenceInCalendarWeeks } from "date-fns";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import withTheme from "@mui/styles/withTheme";
import { green, orange } from "@mui/material/colors";
import Paper from "@mui/material/Paper";
import Edit from "@mui/icons-material/Edit";
import { CourseClassState } from "../../reducers";
import { State } from "../../../../../reducers/state";
import { getCourseClassEnrolments, setCourseClassEnrolments } from "../../actions";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import { AppTheme } from "../../../../../../ish-ui/model/Theme";

interface Props {
  classId: number;
  classStart: string;
  minEnrolments: number;
  maxEnrolments: number;
  hasBudged: boolean;
  targetEnrolments?: number;
  actualEnrolmentsToProfit?: number;
  getEnrolments?: NumberArgFunction;
  enrolmentsFetching?: boolean;
  enrolments?: CourseClassState["enrolments"];
  clearEnrolments?: any;
  theme?: AppTheme;
  openBudget?: any;
}

const CustomizedTooltip = props => {
  const {
 active, payload, data
} = props;

  return active && payload[0] && (
    <Paper className="p-1">
      <Typography component="div" variant="body2" noWrap>
        <span className="mr-1">Total enrolments:</span>
        <span>{data[data.length - 1].value}</span>
      </Typography>
      <Typography component="div" variant="body2" color="textSecondary" noWrap>
        <span className="mr-1">This week enrolments:</span>
        <span>{payload[0].payload["enrolments"]}</span>
      </Typography>
      <Typography component="div" variant="body2" color="textSecondary" noWrap>
        <span className="mr-1">Average enrolments:</span>
        <span>{payload[0].payload["averageValue"]}</span>
      </Typography>
    </Paper>
  );
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

type ChartWeeks = { week: string | number; enrolments: number; value: number, averageValue: number }[];

const initialWeeks: ChartWeeks = [...Array(8).keys()].map((v, week) => ({
  week,
  enrolments: 0,
  value: 0,
  averageValue: 0.0
}));

const initialData = [];

for (let i = initialWeeks.length - 1; i >= 0; i--) {
  initialData.push(initialWeeks[i]);
}

initialData[initialData.length - 2].week = "start";

const chartMargin = {
 top: 8, right: 30, left: 20, bottom: 0
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
    hasBudged,
    actualEnrolmentsToProfit
  }) => {
    const [data, setData] = useState<ChartWeeks>(initialData);
    const [allWeeksData, setAllWeeksData] = useState<ChartWeeks>(initialData);
    const [todayWeek, setTodayWeek] = useState(null);
    const [allWeeksTodayWeek, setAllWeeksTodayWeek] = useState(null);
    const [showLabels, setShowLabels] = useState(false);
    
    const maxLabelEl = useRef<SVGAElement>();
    const minLabelEl = useRef<SVGAElement>();

    const clearData = useCallback(() => {
      setData(prev => prev.map(({ week }) => ({ week, enrolments: 0, value: 0, averageValue: 0 })));
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
            weeks[i].averageValue = (weeks[i].averageValue * (i + 1) + 1) / (i + 1);
          }
          return;
        }

        if (diff <= 0) {
          weeks[0].enrolments++;
          weeks[0].value++;
          weeks[0].averageValue = weeks[0].value;
          return;
        }

        if (weeks[diff]) {
          weeks[diff].enrolments++;
        }

        for (let i = diff; i >= 0; i--) {
          weeks[i].value++;
          weeks[i].averageValue = (weeks[i].averageValue * (i + 1) + 1) / (i + 1);
        }
      });

      weeks[1].week = "start";

      weeks = weeks.reverse();

      setData(weeks);

      weeks = [...Array(Math.abs(enrolmentsWeeks + 1) || 1).keys()].map((v, week) => ({
        week,
        enrolments: 0,
        value: 0,
        averageValue: 0.0,
      }));

      enrolments.forEach(e => {
        const enrolmentStartDate = new Date(e.createdOn);

        const diff = differenceInCalendarWeeks(lastEnrolmentDate, enrolmentStartDate);

        if (diff >= 0 && weeks[diff]) {
          weeks[diff].enrolments++;

          for (let i = diff; i >= 0; i--) {
            weeks[i].value++;
            weeks[i].averageValue = (weeks[i].value) / (i + 1);
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
            value: 0,
            averageValue: 0.0
          }))
        );

        weeks[0].week = 0;

        startWeek = 0;
      } else if (startWeek >= weeks.length) {
        const lastValue = weeks[weeks.length - 1].value as number;
        const lastAverageValue = weeks[weeks.length - 1].averageValue as number;
        const additionalLength = startWeek + 1 - weeks.length;

        for (let i = 0; i < weeks.length; i++) {
          weeks[i].averageValue = weeks[i].value / (weeks.length - i + additionalLength);
        }

        weeks.push(
          ...[...Array(additionalLength).keys()].map((v, week) => ({
            week,
            enrolments: 0,
            value: lastValue,
            averageValue: lastAverageValue / (additionalLength - v),
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
    
    if (!hasBudged || !enrolments.length) {
      return null;
    }

    return (
      <div
        onMouseEnter={onChartHover}
        onMouseLeave={onChartLeave}
        className="mt-2 relative"
      >
        <ResponsiveContainer
          width="100%"
          height={250}
        >
          <AreaChart data={allWeeksData} margin={chartMargin}>
            <XAxis
              dataKey="week"
              tickLine={false}
              tick={props => (
                <CustomizedAxisTick {...props} formatter={tickFormatterAll} />
              )}
              minTickGap={12}
            />
            <Area
              dataKey="value"
              type="stepAfter"
              fill={theme.palette.grey["200"]}
              stroke={theme.palette.grey["500"]}
            />
            <Area
              dataKey="averageValue"
              type="natural"
              fill={theme.palette.grey["200"]}
              stroke={orange["200"]}
            />
            <Tooltip
              wrapperStyle={{ zIndex: 1 }}
              content={props => <CustomizedTooltip {...props} data={data} />}
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
                  label={`Projected enrolments to profit (${targetEnrolments})`}
                  fill={theme.palette.text.primary}
                  visible={showLabels}
                />
              )}
            />
            <ReferenceLine
              y={actualEnrolmentsToProfit}
              ifOverflow="extendDomain"
              label={(
                <CustomizedLabel
                  label={`Actual enrolments to profit (${actualEnrolmentsToProfit})`}
                  fill={theme.palette.text.primary}
                  visible={showLabels}
                />
              )}
            />
            <ReferenceLine
              x={(startWeekIndex !== allWeeksData.length - 1 ? startWeekIndex : 0)}
              isFront
              stroke={green[600]}
              strokeWidth={2}
            />
            <ReferenceLine
              x={allWeeksTodayWeek}
              isFront
              stroke={theme.palette.secondary.main}
              strokeWidth={2}
              label={<CustomizedLabel label="Today" fill={theme.palette.text.primary} visible={showLabels} />}
            />
          </AreaChart>
        </ResponsiveContainer>

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
