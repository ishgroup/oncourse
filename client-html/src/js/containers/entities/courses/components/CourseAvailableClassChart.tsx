/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Grid, Paper, Typography } from '@mui/material';
import { green } from '@mui/material/colors';
import $t from '@t';
import { differenceInDays, format, parseISO } from 'date-fns';
import { III_DD_MMM_YYYY_HH_MM, makeAppStyles, useAppTheme } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import { CartesianGrid, Line, LineChart, ReferenceLine, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import EntityService from '../../../../common/services/EntityService';
import { END_DAY_VALUE, initDataForGraph, START_DAY_VALUE } from '../utils';

const useStyles = makeAppStyles()(() => ({
  chartWrapper: {
    height: "250px",
  },
}));

const CustomizedTooltip = (props: any) => {
  const { active, payload } = props;

  if (active && payload && payload.length) {
    const dataPayload = payload[0].payload;
    if (!dataPayload.classCreated.length && !dataPayload.classStarted.length) return null;

    const TooltipsForCreatedClass = () => {
      return dataPayload.classCreated.map(classCreated => (
        <div key={classCreated.uniqueCode} className="mb-1">
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">{$t('class_published', classCreated.uniqueCode)}</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">{format(parseISO(classCreated.createdOn), III_DD_MMM_YYYY_HH_MM)}</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            <span>{classCreated.maximumPlaces} {$t('places_added')}</span>
          </Typography>
        </div>
      ));
    };

    const TooltipsForStartedClass = () => {
      return dataPayload.classStarted.map(classStarted => (
        <div key={classStarted.uniqueCode} className="mb-1">
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">{$t('class_started', classStarted.uniqueCode)}</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">{format(parseISO(classStarted.startDateTime), III_DD_MMM_YYYY_HH_MM)}</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            {classStarted.availablePlacesOnStartDate === 0 ? (<span>{$t('no_unfilled_places')}</span>) : (
              <span>
                {`${classStarted.availablePlacesOnStartDate} unfilled 
                ${classStarted.availablePlacesOnStartDate === 1 ? "place" : "places"} lost`}
              </span>
            )}
          </Typography>
        </div>
      ));
    };

    return (
      <Paper className="p-1">
        {dataPayload.classCreated.length ? TooltipsForCreatedClass() : null}
        {dataPayload.classStarted.length ? TooltipsForStartedClass() : null}
      </Paper>
    );
  }

  return null;
};

const CustomizedAxisTick: React.FC<any> = (props: any) => {
  const {
    x, y, fill, payload
  } = props;

  return (
    <g transform={`translate(${x},${y})`}>
      <text
        x={0}
        y={0}
        dy={10}
        dx={payload.value !== "Today" ? -9 : -20}
        fill={fill}
      >
        {payload.value < 0 ? -payload.value : payload.value}
      </text>
    </g>
  );
};

const CourseAvailableClassChart = (props: any) => {
  const theme = useAppTheme();

  const { courseId, isNew } = props;

  const [graphData, setGraphData] = useState(null);

  const { classes } = useStyles();

  useEffect(() => {
    if (isNew) return;

    EntityService.getPlainRecords(
      "CourseClass",
      "createdOn,startDateTime,maximumPlaces,uniqueCode",
      `course.id is ${courseId} and startDateTime >= (now - 180 day) and isActive = true and isCancelled = false and endDateTime > (now - 180 day)`,
      65000,
      null,
      "createdOn",
      true,
    ).then(courseClasses => {
      const classIds = courseClasses.rows.map(e => e.id);

      if (!classIds.length) {
        return null;
      }

      EntityService.getPlainRecords(
        "Enrolment",
        "createdOn,courseClass.id,courseClass.createdOn,courseClass.startDateTime",
        `courseClass.id ${classIds.length > 1 ? "in" : "is"} ${classIds} and createdOn <= now and status = SUCCESS`,
        65000,
        null,
        "createdOn",
        true,
      ).then(enrolments => {
        const filteredEnrolments = enrolments.rows.filter(e => (
          e.values[0] >= e.values[2] && e.values[0] <= e.values[3]
        ));

        const courseClassesWithFormatedDate = courseClasses.rows.map(courseClass => ({
          id: courseClass.id,
          uniqueCode: courseClass.values[3],
          createdOn: courseClass.values[0],
          startDateTime: courseClass.values[1],
          maximumPlaces: courseClass.values[2],
          availablePlacesOnStartDate: +courseClass.values[2] - filteredEnrolments.filter(e => e.values[1] === courseClass.id).length,
          createdOnFormated: format(parseISO(courseClass.values[0]), "yyyy-MM-dd"),
          startDateTimeFormated: format(parseISO(courseClass.values[1]), "yyyy-MM-dd"),
        }));

        let availablePlacesOnStartDate = 0;
        const oldNotStartedClasses = courseClassesWithFormatedDate.filter(courseClass => (
          differenceInDays(new Date(), parseISO(courseClass.createdOn)) > START_DAY_VALUE
        ));

        oldNotStartedClasses.forEach(courseClass => {
          availablePlacesOnStartDate += +courseClass.maximumPlaces;
        });

        const oldEnrolments = filteredEnrolments.filter(e => differenceInDays(new Date(), parseISO(e.values[0])) > START_DAY_VALUE );
        if (oldEnrolments.length) availablePlacesOnStartDate -= oldEnrolments.length;

        let newAvailablePlaces = 0;
        const newGraphData = initDataForGraph().map(elem => {
          const newClasses = courseClassesWithFormatedDate.filter(courseClass => courseClass.createdOnFormated === elem.dayDate);
          const newStartedClasses = courseClassesWithFormatedDate.filter(courseClass => courseClass.startDateTimeFormated === elem.dayDate);

          if (elem.dayNumber === "Today" || elem.dayNumber < 0) {
            newClasses.forEach(courseClass => {
              if (courseClass.maximumPlaces) newAvailablePlaces += +courseClass.maximumPlaces;
            });
            newStartedClasses.forEach(courseClass => {
              if (courseClass.availablePlacesOnStartDate) newAvailablePlaces -= courseClass.availablePlacesOnStartDate;
            });
          }

          elem.classCreated = newClasses;
          elem.classStarted = newStartedClasses;

          return { ...elem, availablePlaces: availablePlacesOnStartDate + newAvailablePlaces };
        });

        const enrlomentsResult = {};
        filteredEnrolments.forEach(enrolment => {
          const key = format(parseISO(enrolment.values[0]), "yyyy-MM-dd");
          enrlomentsResult[key] = enrlomentsResult[key] ? enrlomentsResult[key] + 1 : 1;
        });

        let numberOfEnrolments = 0;
          const newGraphDataWithEnrolments = newGraphData.map(elem => {
          if (enrlomentsResult[elem.dayDate]) numberOfEnrolments += enrlomentsResult[elem.dayDate];

          return { ...elem, availablePlaces: elem.availablePlaces - numberOfEnrolments };
        });

        let different = 0;
        let newPlaceToRemove = 0;
        const newGraphDataWithFuture = newGraphDataWithEnrolments.map((elem, index) => {
          if (elem.dayNumber === "Today" || elem.dayNumber < 0) return { ...elem };

          if (elem.dayNumber !== "Today" || elem.dayNumber > 0) {
            if (elem.dayNumber === 1) {
              different = newGraphDataWithEnrolments[index - 1 - END_DAY_VALUE].availablePlaces
                - newGraphDataWithEnrolments[index - 1].availablePlaces;
            }

            if (newGraphDataWithEnrolments[index - 1 - END_DAY_VALUE].classCreated.length) {
              newGraphDataWithEnrolments[index - 1 - END_DAY_VALUE].classCreated.forEach(courseClass => {
                newPlaceToRemove += +courseClass.maximumPlaces;
              });
            }

            if (newGraphDataWithEnrolments[index - 1 - END_DAY_VALUE].classStarted.length) {
              newGraphDataWithEnrolments[index - 1 - END_DAY_VALUE].classStarted.forEach(courseClass => {
                newPlaceToRemove -= courseClass.availablePlacesOnStartDate;
              });
            }

            const valueToDecrease = newGraphDataWithEnrolments[index - 1 - END_DAY_VALUE].availablePlaces
              - different - newPlaceToRemove;

            index > 0 && newGraphDataWithEnrolments[index].classStarted.forEach(courseClass => {
              newPlaceToRemove += courseClass.availablePlacesOnStartDate;
            });

            const availablePlacesResult = valueToDecrease >= 0 ? valueToDecrease : 0;

            return {
              ...elem,
              availablePlaces: availablePlacesResult,
            };
          }
        });

        setGraphData(newGraphDataWithFuture);
      });
    });
  }, [courseId]);

  const renderDot = dotProps => {
    const {
      cx, cy, payload
    } = dotProps;

    if (payload.classCreated.length || payload.classStarted.length) {
      return (
        <svg x={cx - 15} y={cy - 15} width={60} height={60} fill={payload.classCreated.length ? green[600] : "red"} viewBox="0 0 40 40">
          <path d="M7.8 10a2.2 2.2 0 0 0 4.4 0 2.2 2.2 0 0 0-4.4 0z" />
        </svg>
      );
    }

    return null;
  };

  if (!graphData) {
    return null;
  }

  return (
    <Grid item xs={12} className={classes.chartWrapper}>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          width={500}
          height={400}
          data={graphData}
          margin={{
            top: 5,
            right: 30,
            left: 20,
            bottom: 20,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <YAxis
            allowDecimals={false}
            dataKey="availablePlaces"
            label={{ value: "Available places", angle: -90, position: "insideBottomLeft" }}
          />
          <XAxis
            dataKey="dayNumber"
            height={50}
            interval={9}
            label={{ value: "Days", position: "insideBottom" }}
            tick={<CustomizedAxisTick />}
          />
          <Tooltip
            content={tooltipProps => (<CustomizedTooltip {...tooltipProps} />)}
          />
          <Line
            activeDot={false}
            dataKey="availablePlaces"
            dot={renderDot}
            stroke={theme.palette.grey["500"]}
            strokeWidth={2}
            type="monotone"
          />
          <ReferenceLine
            x="Today"
            isFront
            stroke={green[600]}
            strokeWidth={2}
          />
        </LineChart>
      </ResponsiveContainer>
    </Grid>
  );
};

export default CourseAvailableClassChart;