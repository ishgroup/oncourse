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
import { useWindowSize } from "../../common/styles/hooks";

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

function update(svg, force, root, container) {
  let node = svg.selectAll(".node");
  const nodes = flatten(root);

  const links = d3.layout.tree().links(nodes);

  let link = svg.selectAll(".link");

  // Remove labels
  svg.selectAll("text").remove();

  // Restart the force layout.
  force
    .nodes(nodes)
    .on("tick", tick)
    .links(links)
    .start();

  // Update links.
  link = link.data(links, d => d.target.id);

  link.exit().remove();

  link.enter().insert("line", ".node")
    .attr("class", "link");

  // Update nodes.
  node = node.data(nodes, d => d.id);

  node.exit().remove();

  function click(d) {
    if ((d3.event as any).defaultPrevented) return; // ignore drag
    if (d.children) {
      d._children = d.children;
      d.children = null;
    } else {
      d.children = d._children;
      d._children = null;
    }
    update(svg, force, root, container);
  }

  const nodeEnter = node.enter()
    .append("circle")
    .attr("r", 10 )
    .attr("class", "node")
    .on("click", click)
    .call(force.drag);

  const labelsBackground = svg.selectAll(null)
    .data(nodes)
    .enter()
    .append("text")
    .attr("dy", ".35em")
    .attr("y", 16)
    .text(d => d.name)
    .attr("fill", "none")
    .attr("class", "textClone")
    .attr("stroke-width", 3);

  const labels = svg.selectAll(null)
    .data(nodes)
    .enter()
    .append("text")
    .attr("dy", ".35em")
    .attr("y", 16)
    .text(d => d.name);

  nodeEnter.style("fill", color);

  function tick() {
    // Node radius with text offset
    const radius = 24;

    const { width, height } = container.getBoundingClientRect();

    // Keep nodes within given area
    node.attr("cx", d => d.x = Math.max(radius, Math.min(width - radius, d.x)))
      .attr("cy", d => d.y = Math.max(radius, Math.min(height - radius, d.y)));

    link
      .attr("x1", d => d.source.x)
      .attr("y1", d => d.source.y)
      .attr("x2", d => d.target.x)
      .attr("y2", d => d.target.y);

    labels
      .attr("x", d => d.x )
      .attr("y", d => d.y + 16 );

    labelsBackground
      .attr("x", d => d.x )
      .attr("y", d => d.y + 16 );
  }

}

function color(d) {
  return d._children ? "#3182bd" // collapsed package
    : d.children ? "#c6dbef" // expanded package
      : "#fd8d3c"; // leaf node
}

const useStyles = makeAppStyles(theme => ({
  root: {
    width: "100%",
    height: 300,
    backgroundImage: `radial-gradient(${alpha(theme.palette.primary.main, 0.5)} 1.5px, transparent 1.5px),radial-gradient(${alpha(theme.palette.primary.main, 0.5)} 1.5px, transparent 1.5px)`,
    backgroundPosition: "5px 4px, 20px 18px",
    backgroundSize: "30px 30px",
    borderRadius: theme.spacing(1),
    border: "2px solid",
    borderColor: alpha(theme.palette.text.disabled, 0.1),
      "& .node": {
        cursor: "pointer",
        strokeWidth: "1.5px",
        stroke: theme.palette.background.default
      },
      "& text": {
        font: "12px sans-serif",
        fontWeight: 600,
        pointerEvents: "none",
        textAnchor: "middle",
        fill: theme.palette.text.primary
      },
      "& text.textClone": {
        stroke: theme.palette.background.default
      },
      "& line.link": {
        fill: "none",
        stroke: "#9ecae1",
        strokeWidth: "1.5px"
      }
  }
}));

const StructureGraph = ({ root }) => {
  const ref = useRef<any>();
  const forceRef = useRef<any>();

  // Stop on unmount
  useEffect(() => () => {
    forceRef.current?.stop();
  }, []);

  const classes = useStyles();

  useEffect(() => {
    const { width, height } =  ref.current.getBoundingClientRect();

    // Stop on root change
    forceRef.current?.stop();

    forceRef.current = d3.layout.force()
      .linkDistance(80)
      .charge(-120)
      .gravity(0.05)
      .size([width, height]);

    d3.select(ref.current).select("svg").remove();

    const svg = d3.select(ref.current).append("svg")
      .attr("width", width)
      .attr("height", height);

    update(svg, forceRef.current, root, ref.current);
  }, [root]);

  const { width, height } = useWindowSize();

  useEffect(() => {
    const { width, height } =  ref.current?.getBoundingClientRect();

    d3.select(ref.current).select("svg")
      .attr("width", width)
      .attr("height", height);

    forceRef.current?.size([width, height]);
    forceRef.current?.start();

  }, [width, height]);

  return <div className={classes.root} ref={ref} />;
};

export default StructureGraph;