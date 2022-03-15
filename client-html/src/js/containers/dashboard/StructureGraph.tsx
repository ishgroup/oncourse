/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useRef } from "react";
import * as d3 from "d3";
import { makeAppStyles } from "../../common/styles/makeStyles";
import { alpha } from "@mui/material/styles";
const flextree = require('d3-flextree');

const treeData = {
  "name": "Timetables",
  "color": "#F7941D",
  "size": [100, 100],
  "children": [
    {
      "name": "Sites",
      "color": "#37CAAD",
      "size": [150, 100],
      "children": [
        {
          "name": "Rooms",
          "color": "#37CAAD",
          "size": [100, 100],
        }
      ]
    },
    {
      "name": "Contacts",
      "color": "#E27575",
      "size": [150, 100],
    },
    {
      "name": "Classes",
      "color": "#96B9CC",
      "size": [150, 100],
    }
  ]
};

const duration = 750;
let i = 0;
const root = d3.hierarchy(treeData, d => d.children);

const flexLayout = flextree.flextree();

function update(source, g) {
  // Assigns the x and y position for the nodes
  const tree = flexLayout(root);

  // Compute the new tree layout.
  const nodes = tree.descendants();
  const links = tree.descendants().slice(1);

  // ****************** Nodes section ***************************

  // Update the nodes...
  const node = g.selectAll('g.node')
    .data(nodes, d => d.id || (d.id = ++i));

  // Enter any new modes at the parent's previous position.
  const nodeEnter = node.enter().append('g')
    .attr('class', 'node')
    .attr("transform", d => "translate(" + source.x0 + "," + source.y0 + ")")
    .on('click', click);

  // Shadows
  const defs = nodeEnter.append("defs");

  // create filter with id #drop-shadow
  // height=130% so that the shadow is not clipped
  const filter = defs.append("filter")
    .attr("id", "drop-shadow")
    .attr("width", "114")
    .attr("height", "73");

  filter.append("feColorMatrix")
    .attr("in", "SourceAlpha")
    .attr("type", "matrix")
    .attr("values", "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0")
    .attr("result", "hardAlpha");  
  
  filter.append("feOffset")
    .attr("dy", 1);  
  
  filter.append("feGaussianBlur")
    .attr("stdDeviation", 1);

  filter.append("feComposite")
    .attr("in2", "hardAlpha")
    .attr("operator", "out");

  filter.append("feColorMatrix")
    .attr("type", "matrix")
    .attr("values", "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2 0");

  filter.append("feBlend")
    .attr("mode", "normal")
    .attr("in", "SourceGraphic");

  nodeEnter.attr("filter", "url(#drop-shadow)");

  nodeEnter.append("clipPath") // define a clip path
    .attr("id", "ellipse-clip") // give the clipPath an ID
    .append('rect')
    .attr('x', -110 / 2)
    .attr('y', -40 / 2)
    .attr('width', 110)
    .attr('height', 40)
    .attr('rx', 3);

  nodeEnter.append('rect')
    .attr('x', -110 / 2)
    .attr('y', -40 / 2)
    .attr('width', 110)
    .attr('height', 40)
    .attr('rx', 3)
    .attr('fill', 'white')
    .attr("clip-path", "url(#ellipse-clip)");

  nodeEnter.append('rect')
    .attr('x', -110 / 2)
    .attr('y', -40 / 2)
    .attr('width', 110)
    .attr('height', 3)
    .attr('fill', ({ data }) => data.color)
    .attr("clip-path", "url(#ellipse-clip)");

  // Add labels for the nodes
  nodeEnter.append('text')
    .attr('pointer-events', 'none')
    .attr('dy', '0.35em')
    .text(d => d.data.name)
    .attr('text-anchor', 'middle')
    .attr("fill", "rgba(0, 0, 0, 0.87)")
    .style('font', '13px sans-serif')
    .style('font-weight', 'bold');

  // UPDATE
  const nodeUpdate = nodeEnter.merge(node);

  // Transition to the proper position for the node
  nodeUpdate.transition()
    .duration(duration)
    .attr("transform", function (event, i, arr) {
      const d = d3.select(this).datum();
      return "translate(" + d.x + "," + d.y + ")";
    });

  // Update the node attributes and style
  nodeUpdate
    .style("fill", d => (d._children ? "lightsteelblue" : "#fff"))
    .attr('cursor', 'pointer');

  // Remove any exiting nodes
  const nodeExit = node.exit().transition()
    .duration(duration)
    .attr("transform", function (event, i, arr) {
      const d = d3.select(this).datum();
      return "translate(" + source.x + "," + source.y + ")";
    })
    .remove();

  // On exit reduce the node circles size to 0
  nodeExit.select('circle')
    .attr('r', 1e-6);

  // On exit reduce the opacity of text labels
  nodeExit.select('text')
    .style('fill-opacity', 1e-6);

  // ****************** links section ***************************

  // Update the links...
  const link = g.selectAll('path.link')
    .data(links, d => d.id);

  // Enter any new links at the parent's previous position.
  const linkEnter = link.enter().insert('path', "g")
    .attr("class", "link")
    .attr('d', d => {
      const o = {
        x: source.x0,
        y: source.y0
      };
      return diagonal(o, o);
    });

  // UPDATE
  const linkUpdate = linkEnter.merge(link)
    .attr("fill", "none")
    .attr("stroke", "#ccc")
    .attr("stroke-width", "2px");

  // Transition back to the parent element position
  linkUpdate.transition()
    .duration(duration)
    .attr('d', d => diagonal(d, d.parent));

  // Remove any exiting links
  const linkExit = link.exit().transition()
    .duration(duration)
    .attr('d', function (event, i, arr) {
      const d = d3.select(this).datum();
      const o = {
        x: source.x,
        y: source.y
      };
      return diagonal(o, o);
    })
    .remove();

  // Store the old positions for transition.
  nodes.forEach(d => {
    d.x0 = d.x;
    d.y0 = d.y;
  });

  // Creates a curved (diagonal) path from parent to the child nodes
  function diagonal(s, d) {
    return `M ${s.x} ${s.y}
            C ${(s.x + d.x) / 2} ${s.y},
              ${(s.x + d.x) / 2} ${d.y},
              ${d.x} ${d.y}`;
  }

  // Toggle children on click.
  function click(event, d) {
    if (d.children) {
      d._children = d.children;
      d.children = null;
    } else {
      d.children = d._children;
      d._children = null;
    }
    update(d, g);
  }
}

const useStyles = makeAppStyles((theme) => ({
  root: {
    width: 600,
    height: 400,
    backgroundImage: `radial-gradient(${theme.palette.primary.main} 1.5px, transparent 1.5px),radial-gradient(${theme.palette.primary.main} 1.5px, transparent 1.5px)`,
    backgroundPosition: "5px 4px, 20px 18px",
    backgroundSize: "30px 30px",
    borderRadius: theme.spacing(1),
    border: "2px solid",
    borderColor: alpha(theme.palette.text.disabled, 0.1),
  }
}))

const StructureGraph = () => {
  const ref = useRef();

  const classes = useStyles();

  useEffect(() => {
    const svg = d3.select(ref.current)
      .append("svg")
      .attr("width", 600)
      .attr("height", 400)
      .append("g")
      .attr('transform', `translate(${600 / 2},${100})`);
    
    update(root, svg);
  }, []);
  
  return <div className={classes.root} ref={ref} />;
};

export default StructureGraph;