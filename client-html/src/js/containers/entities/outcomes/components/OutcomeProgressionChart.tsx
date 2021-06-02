/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo, useState } from "react";
import { Cell, Legend, Pie, PieChart } from "recharts";
import { fade, Tooltip, useTheme } from '@material-ui/core';
import { OutcomeProgression } from "@api/model";
import { normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";

interface Props {
  data: OutcomeProgression
}

export function AttendanceChart({ data }: Props) {
  if (!data) {
    data = {};
  }

  const [includeFuture, setIncludeFuture] = useState(true);

  const tooltipText = `Click to ${includeFuture ? "exclude" : "include"} future timetable`;

  const chartData = useMemo(() => {
    const result = [];
    Object.keys(data).forEach(k => {
      switch (k) {
        case "futureTimetable":
          return includeFuture
            ? result.push({ name: `future timetable (${data[k]} hrs)`, value: data[k] })
            : undefined;
        case "notMarked":
          return result.push({ name: `not marked (${data[k]} hrs)`, value: data[k] });
        case "absent":
          return result.push({ name: `absent (${data[k]} hrs)`, value: data[k] });
        case "attended":
          return result.push({ name: `attended (${data[k]} hrs)`, value: data[k] });
        default:
          return null;
      }
    });
    return result;
  }, [data, includeFuture]);

  const { palette: { text: { primary }, background: { paper }, getContrastText } } = useTheme();

  const COLORS = [
    ...includeFuture ? [fade(getContrastText(paper), 0.4)] : [],
    fade(getContrastText(paper), 0.6),
    "red",
    "green"
  ];

  const attendedIndex = chartData.findIndex(c => c.name.includes("attended"));

  return (
    <Tooltip title={tooltipText} placement="top">
      <div onClick={() => setIncludeFuture(prevState => !prevState)}>
        <PieChart width={200} height={270} style={{ cursor: "pointer" }}>
          <Pie
            data={chartData}
            cx={95}
            cy={90}
            innerRadius={60}
            outerRadius={80}
            fill="#8884d8"
            paddingAngle={0}
            dataKey="value"
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <text x={100} y={85} dy={8} textAnchor="middle" fill={primary}>
            <tspan fontSize="30">
              {normalizeNumber(chartData.length
                && chartData[attendedIndex].value
                && Math.round(chartData[attendedIndex].value / (chartData.reduce((p, c) => p + c.value, 0) / 100)))}
              %
            </tspan>
            <tspan x="100" dy="15">attended</tspan>
          </text>
          <Legend
            iconType="circle"
            iconSize={10}
            layout="vertical"
            verticalAlign="bottom"
            align="center"
          />
        </PieChart>
      </div>
    </Tooltip>

  );
}

export function AssessmentChart({ data }: Props) {
  if (!data) {
    data = {};
  }

  const [includeUnreleased, setIncludeUnreleased] = useState(true);

  const tooltipText = `Click to ${includeUnreleased ? "exclude" : "include"} unreleased assessments`;

  const chartData = useMemo(() => {
    const result = [];
    Object.keys(data).forEach(k => {
      switch (k) {
        case "released":
          return result.push({ name: `released (${data[k]})`, value: data[k] });
        case "notReleased":
          return includeUnreleased
            ? result.push({ name: `not released (${data[k]})`, value: data[k] })
            : undefined;
        case "submitted":
          return result.push({ name: `submitted (${data[k]})`, value: data[k] });
        case "marked":
          return result.push({ name: `marked (${data[k]})`, value: data[k] });
        default:
          return null;
      }
    });
    return result;
  }, [data, includeUnreleased]);

  const { palette: { text: { primary }, background: { paper }, getContrastText } } = useTheme();

  const COLORS = [
    ...includeUnreleased ? [fade(getContrastText(paper), 0.4)] : [],
    fade(getContrastText(paper), 0.6),
    "#0088FE",
    "#FF8042"
  ];

  const markedIndex = chartData.findIndex(c => c.name.includes("marked"));

  return (
    <Tooltip title={tooltipText} placement="top">
      <div onClick={() => setIncludeUnreleased(prevState => !prevState)}>
        <PieChart width={200} height={270} style={{ cursor: "pointer" }}>
          <Pie
            data={chartData}
            cx={95}
            cy={90}
            innerRadius={60}
            outerRadius={80}
            fill="#8884d8"
            paddingAngle={0}
            dataKey="value"
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <text x={100} y={85} dy={8} textAnchor="middle" fill={primary}>
            <tspan fontSize="30">
              {normalizeNumber(chartData.length
                && chartData[markedIndex]?.value
                && Math.round(chartData[markedIndex].value / ( (chartData.reduce((p, c) => p + c.value, 0) / 100))))}
              %
            </tspan>
            <tspan x="100" dy="15">assessments</tspan>
            <tspan x="100" dy="15">marked</tspan>
          </text>
          <Legend
            iconType="circle"
            iconSize={10}
            layout="vertical"
            verticalAlign="bottom"
            align="center"
          />
        </PieChart>
      </div>
    </Tooltip>
  );
}
