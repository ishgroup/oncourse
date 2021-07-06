/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import withTheme from "@material-ui/core/styles/withTheme";
import green from "@material-ui/core/colors/green";
import { differenceInDays, format, parseISO } from "date-fns";
import { Paper, Typography } from "@material-ui/core";
import clsx from "clsx";
import makeStyles from "@material-ui/core/styles/makeStyles";
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, ReferenceLine
} from 'recharts';
import { END_DAY_VALUE, initDataForGraph, START_DAY_VALUE } from "../utils";
import EntityService from "../../../../common/services/EntityService";
import { III_DD_MMM_YYYY_HH_MM } from "../../../../common/utils/dates/format";

const CustomizedTooltip = (props: any) => {
  const { active, payload } = props;

  if (active && payload && payload.length) {
    const dataPayload = payload[0].payload;
    if (!dataPayload.classCreated.length && !dataPayload.classStarted.length) return null;

    const TooltipsForCreatedClass = () => {
      return dataPayload.classCreated.map(classCreated => (
        <div key={classCreated.uniqueCode} className="mb-1">
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">Class {classCreated.uniqueCode} published</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">{format(parseISO(classCreated.createdOn), III_DD_MMM_YYYY_HH_MM)}</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            <span>{classCreated.maximumPlaces} places added</span>
          </Typography>
        </div>
      ));
    };

    const TooltipsForStartedClass = () => {
      return dataPayload.classStarted.map(classStarted => (
        <div key={classStarted.uniqueCode} className="mb-1">
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">Class {classStarted.uniqueCode} started</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            <span className="fontWeight600">{format(parseISO(classStarted.startDateTime), III_DD_MMM_YYYY_HH_MM)}</span>
          </Typography>
          <Typography component="div" variant="body2" noWrap>
            <span>{classStarted.availablePlacesOnStartDate} unfilled places lost</span>
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

const useStyles = makeStyles({
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
});

const CourseAvailableClassChart = (props: any) => {
  const { courseId, isNew, theme } = props;

  const [graphData, setGraphData] = useState(initDataForGraph());
  const [hasOverlay, setHasOverlay] = useState(false);

  const classes = useStyles();

  useEffect(() => {
    if (isNew) return null;

    EntityService.getPlainRecords(
      "CourseClass",
      "createdOn,startDateTime,maximumPlaces,uniqueCode",
      `course.id is ${courseId} and startDateTime >= (now - 180 day) and isActive = true and endDateTime > (now - 180 day)`,
      65000,
      null,
      "createdOn",
      true,
    ).then(courseClasses => {
      const classIds = courseClasses.rows.map(e => e.id);

      if (!classIds.length) {
        setHasOverlay(true);
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
        const newGraphData = graphData.map(elem => {
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
        enrolments.rows.forEach(enrolment => {
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

            const availablePlacesResult = newGraphDataWithEnrolments[index - 1 - END_DAY_VALUE].availablePlaces
              - different - newPlaceToRemove >= 0 ? newGraphDataWithEnrolments[index - 1
              - END_DAY_VALUE].availablePlaces - different - newPlaceToRemove : 0;

            return {
              ...elem,
              availablePlaces: availablePlacesResult,
            };
          }
        });

        setGraphData(newGraphDataWithFuture);
      });
    });
  }, []);

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

  return (
    <div className="relative h-100">
      {hasOverlay && (
        <div className={classes.overlay}>
          <Typography>
            Create a class to see projection
          </Typography>
        </div>
      )}

      <ResponsiveContainer width="100%" height="100%" className={clsx(hasOverlay && classes.hasOverlay)}>
        <LineChart
          width={500}
          height={400}
          data={graphData}
          margin={{
            top: 5,
            right: 30,
            left: 20,
            bottom: 5,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <YAxis
            dataKey="availablePlaces"
            label={{ value: "Available places", angle: -90, position: "insideBottomLeft" }}
          />
          <XAxis
            dataKey="dayNumber"
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
    </div>
  );
};

export default withTheme(CourseAvailableClassChart);