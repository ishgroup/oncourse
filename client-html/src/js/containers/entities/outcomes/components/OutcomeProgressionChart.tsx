/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import {
 PieChart, Pie, Legend, Cell
} from "recharts";
import { useTheme } from '@material-ui/core/styles';

const data1 = [
  { name: "not marked (6 hrs)", value: 600 },
  { name: "absent (2 hrs)", value: 200 },
  { name: "attended (3 hrs)", value: 300 },
];

const data2 = [
  { name: "not released (3)", value: 300 },
  { name: "submitted (2)", value: 200 },
  { name: "marked (2)", value: 200 },
];

export function AttendanceChart() {
  const { palette: { divider } } = useTheme();

  const COLORS = [divider, "red", "green"];

  return (
    <PieChart width={200} height={270}>
      <Pie
        data={data1}
        cx={95}
        cy={90}
        innerRadius={60}
        outerRadius={80}
        fill="#8884d8"
        paddingAngle={0}
        dataKey="value"
      >
        {data1.map((entry, index) => (
          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
        ))}
      </Pie>
      <text x={100} y={85} dy={8} textAnchor="middle">
        <tspan fontSize="30">
          {Math.round(data1[2].value/(data1.reduce((p, c) => p + c.value, 0)/100))}
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
  );
}

export function AssessmentChart() {
  const { palette: { divider } } = useTheme();

  const COLORS = [divider, "#0088FE", "#FF8042"];

  return (
    <PieChart width={200} height={270}>
      <Pie
        data={data2}
        cx={95}
        cy={90}
        innerRadius={60}
        outerRadius={80}
        fill="#8884d8"
        paddingAngle={0}
        dataKey="value"
      >
        {data2.map((entry, index) => (
          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
        ))}
      </Pie>
      <text x={100} y={85} dy={8} textAnchor="middle">
        <tspan fontSize="30">
          {Math.round(data2[2].value/(data2.reduce((p, c) => p + c.value, 0)/100))}
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
  );
}
