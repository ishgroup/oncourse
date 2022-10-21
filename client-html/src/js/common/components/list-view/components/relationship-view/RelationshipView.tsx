/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {useEffect, useRef, useState} from "react";
import * as d3 from "d3";
import clsx from "clsx";
import { alpha } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import { TransitionProps } from "@mui/material/transitions";
import Slide from "@mui/material/Slide";
import Button from "@mui/material/Button";
import AppBar from "@mui/material/AppBar";
import { makeAppStyles } from "../../../../styles/makeStyles";
import {getColor, useWindowSize} from "../../../../styles/hooks";
import LoadingIndicator from "../../../progress/LoadingIndicator";
import { NoArgFunction } from "../../../../../model/common/CommonFunctions";
import {State} from "../../../../../reducers/state";
import {connect} from "react-redux";
import {EntityRelationType} from "@api/model";

const Transition = React.forwardRef<unknown, TransitionProps>((props, ref) => (
  <Slide direction="up" ref={ref} {...props as any} />
));

function flatten(nodes, relationTypes) {
  const result = [];
  let i = 0;
  function recurse(node) {
    if (node.children) node.children.forEach(recurse);
    if (!node.id) node.id = ++i;
    if (node.relationId) {
      node.linkColor = relationTypes.find(type => type.id === node.relationId)?.color;
    }
    result.push(node);
  }
  nodes.forEach(n => {
    recurse(n);
  });
  return result;
}

function update(svg, force, rows, container, relationTypes) {
  let node = svg.selectAll(".node");
  const nodes = flatten(rows, relationTypes);

  const links = d3.layout.tree().links(nodes);

  let link = svg.selectAll(".link");

  function tick() {
    // Node radius with text offset
    const radius = 24;

    const { width, height } = container.getBoundingClientRect();

    // Keep nodes within given area
    node.attr("transform", d => "translate("
      + Math.max(radius, Math.min(width - radius, d.x)) + ","
      + Math.max(radius, Math.min(height - radius, d.y)) + ")");

    link
      .attr("x1", d => d.source.x)
      .attr("y1", d => d.source.y)
      .attr("x2", d => d.target.x)
      .attr("y2", d => d.target.y);
  }

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
    .attr("class", "link")
    .attr("style", d => `stroke: ${d.target?.linkColor}`);

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
    update(svg, force, rows, container, relationTypes);
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
    marginTop: theme.spacing(8),
    height: `calc(100vh - ${theme.spacing(8)})`,
    overflow: 'hidden'
  },
  graphRoot: {
    width: "100%",
    height: "100%",
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
      textAnchor: "middle",
      fill: theme.palette.text.primary
    },
    "& line.link": {
      fill: "none",
      stroke: "#9ecae1",
      strokeWidth: "1.5px"
    }
  },
  fullEditViewBackground: {
    background: theme.appBar.header.background,
  },
  header: {
    height: "64px",
    display: "flex",
    justifyContent: "space-between",
    flexDirection: "row",
    alignItems: "center",
    padding: theme.spacing(0, 3),
    background: theme.appBar.header.background,
    color: theme.appBar.header.color,
    "& $submitButtonAlternate": {
      background: `${theme.appBar.headerAlternate.color}`,
      color: `${theme.appBar.headerAlternate.background}`,
    },
    "& $closeButtonAlternate": {
      color: `${theme.appBar.headerAlternate.color}`,
    }
  }
}));

interface Props {
  open: boolean;
  rows: any[];
  closeView: NoArgFunction;
  entityRelationTypes?: EntityRelationType[];
}

const RelationshipView: React.FC<Props> = props => {
  const {
    open, rows, closeView, entityRelationTypes
  } = props;

  const [relationTypes, setRelationTypes] = useState<any[]>([]);
  // const [relationshipItems, setRelationshipItems] = useState<any[]>([]);

  const ref = useRef<any>();
  const forceRef = useRef<any>();

  const classes = useStyles();

  useEffect(() => {
    const types = [];
    const totalTypes = entityRelationTypes?.length;
    let colorIndex = 0;
    let colorCode = 200;
    for(let i=0; i<totalTypes; i++) {
      if (((i + 1) % 16) === 0) {
        colorIndex = 0;
        colorCode += 100;
      } else {
        colorIndex++;
      }

      types.push({...entityRelationTypes[i], color: getColor(colorIndex, colorCode)});
    }
    setRelationTypes(types);
  }, [ref.current, open, entityRelationTypes]);

  useEffect(() => {
    if (open) {
      setTimeout(() => {
        const { width, height } =  ref.current.getBoundingClientRect();

        forceRef.current = d3.layout.force()
          .linkDistance(80)
          .charge(-130)
          .gravity(0.05)
          .size([width, height]);

        d3.select(ref.current).select("svg").remove();

        const svg = d3.select(ref.current).append("svg")
          .attr("width", width)
          .attr("height", height);

        const courses = rows.map(row => {
          return {
            id: row.id,
            name: row.values[2],
            code: row.values[3],
            children: JSON.parse(row.values[9]),
          };
        });

        update(svg, forceRef.current, courses, ref.current, relationTypes);
      }, 1000);
    }
  }, [rows, open, ref.current, relationTypes]);

  const { width, height } = useWindowSize();

  useEffect(() => {
    if (open) {
      setTimeout(() => {
        const { width, height } =  ref.current?.getBoundingClientRect();

        d3.select(ref.current).select("svg")
          .attr("width", width)
          .attr("height", height);

        forceRef.current?.size([width, height]);
        forceRef.current?.start();
      }, 1000);
    }
  }, [width, height, open]);

  return (
    <Dialog
      fullScreen
      open={Boolean(open)}
      TransitionComponent={Transition}
      classes={{
        paper: classes.fullEditViewBackground
      }}
      disableEnforceFocus
    >
      <LoadingIndicator position="fixed" />
      <AppBar
        elevation={0}
        className={classes.header}
      >
        <div className="flex-fill">
          Course Relationship
        </div>
        <div>
          <Button
            onClick={closeView}
            className="closeAppBarButton"
          >
            Close
          </Button>
        </div>
      </AppBar>
      <div className={classes.root}>
        <div className={clsx("dotsBackgroundImage", classes.graphRoot)} ref={ref} />
      </div>
    </Dialog>
  );
};

const mapStateToProps = (state: State) => ({
  entityRelationTypes: state.preferences.entityRelationTypes
});

export default connect<any, any, Props>(mapStateToProps, null)(RelationshipView);
