/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useRef } from "react";
import { alpha } from "@mui/material/styles";
import * as d3 from "d3";
import { makeAppStyles } from "../../common/styles/makeStyles";

const root = [
  {
  "name": "Class",
  "children": [
    {
      "name": "Room",
      "children": [
        {
          "name": "Site",
          "relation": "Holidays"
        }
      ]
    },
    {
      "name": "Waiting list",
    },
    {
      "name": "Timetable",
    },
    {
      "name": "Tutor",
      "children": [
        {
          "name": "Holidays",
        }
      ]
    }
    ]
  },
  {
    "name": "Tutor pay rates",
    "children": [
      {
        "name": "Tutor pay",
      }
    ]
  }
];

// Returns a list of all nodes under the root.
function flatten(nodes) {
  const result = [];
  let i = 0;
  function recurse(node) {
    if (node.children) node.children.forEach(recurse);
    if (!node.id) node.id = ++i;
    result.push(node);
  }
  nodes.forEach(n => {
    recurse(n);
  });
  return result;
}

function update(svg, force) {
  let node = svg.selectAll(".node");
  const nodes = flatten(root);

  const links = d3.layout.tree().links(nodes);

  // links.push({
  //   source: nodes.find(n => n.name === "Site"),
  //   target: nodes.find(n => n.name === "Holidays")
  // });

  // Restart the force layout.
  force
    .nodes(nodes)
    .links(links)
    .on("tick", tick)
    .start();

  let link = svg.selectAll(".link");

  function tick() {
    link.attr("x1", d => d.source.x)
      .attr("y1", d => d.source.y)
      .attr("x2", d => d.target.x)
      .attr("y2", d => d.target.y);

    node.attr("transform", d => "translate(" + d.x + "," + d.y + ")");
  }

  // Update links.
  link = link.data(links, d => d.target.id);

  link.exit().remove();

  link.enter().insert("line", ".node")
    .attr("class", "link");

  // Update nodes.
  node = node.data(nodes, d => d.id);

  node.exit().remove();

  function click(d) {
    if (d3.event.defaultPrevented) return; // ignore drag
    if (d.children) {
      d._children = d.children;
      d.children = null;
    } else {
      d.children = d._children;
      d._children = null;
    }
    update(svg, force);
  }

  const nodeEnter = node.enter().append("g")
    .attr("class", "node")
    .on("click", click)
    .call(force.drag);

  nodeEnter.append("circle")
    .attr("r", 10 );

  nodeEnter.append("text")
    .attr("dy", ".35em")
    .attr("y", 16)
    .text(d => d.name);

  node.select("circle")
    .style("fill", color);
}

function color(d) {
  return d._children ? "#3182bd" // collapsed package
    : d.children ? "#c6dbef" // expanded package
      : "#fd8d3c"; // leaf node
}

const useStyles = makeAppStyles(theme => ({
  root: {
    width: 600,
    height: 300,
    marginTop: theme.spacing(4),
    backgroundImage: `radial-gradient(${alpha(theme.palette.primary.main, 0.5)} 1.5px, transparent 1.5px),radial-gradient(${alpha(theme.palette.primary.main, 0.5)} 1.5px, transparent 1.5px)`,
    backgroundPosition: "5px 4px, 20px 18px",
    backgroundSize: "30px 30px",
    borderRadius: theme.spacing(1),
    border: "2px solid",
    borderColor: alpha(theme.palette.text.disabled, 0.1),
      "& .node circle": {
        cursor: "pointer",
        stroke: "#3182bd",
        strokeWidth: "1.5px"
      },
      "& .node text": {
        font: "12px sans-serif",
        fontWeight: 600,
        pointerEvents: "none",
        textAnchor: "middle"
      },
      "& line.link": {
        fill: "none",
        stroke: "#9ecae1",
        strokeWidth: "1.5px"
      }
  }
}));

const StructureGraph = () => {
  const ref = useRef();

  const classes = useStyles();

  useEffect(() => {
    const width = 600;
    const height = 300;

    const force = d3.layout.force()
      .linkDistance(80)
      .charge(-120)
      .gravity(0.05)
      .size([width, height]);

    const svg = d3.select(ref.current).append("svg")
      .attr("width", width)
      .attr("height", height);

    update(svg, force);
  }, []);
  
  return <div className={classes.root} ref={ref} />;
};

export default StructureGraph;