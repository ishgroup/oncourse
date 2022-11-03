/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useRef, useState } from "react";
import { connect } from "react-redux";
import * as d3 from "d3";
import clsx from "clsx";
import { Course, EntityRelationType } from "@api/model";
import { alpha } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import { TransitionProps } from "@mui/material/transitions";
import Slide from "@mui/material/Slide";
import { makeAppStyles } from "../../../../common/styles/makeStyles";
import { getColor, useWindowSize } from "../../../../common/styles/hooks";
import LoadingIndicator from "../../../../common/components/progress/LoadingIndicator";
import { NoArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";
import { State } from "../../../../reducers/state";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";
import CourseService from "../services/CourseService";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { useAppDispatch } from "../../../../common/utils/hooks";
import Typography from "@mui/material/Typography";

const useStyles = makeAppStyles(theme => ({
  graphRoot: {
    width: "100%",
    height: "100%",
    borderRadius: theme.spacing(1),
    border: "2px solid",
    borderColor: alpha(theme.palette.text.disabled, 0.1),
    "& .node circle": {
      cursor: "pointer",
      strokeWidth: "1.5px",
      stroke: theme.palette.background.default
    },
    "& .node text": {
      font: "12px sans-serif",
      fontWeight: 600,
      pointerEvents: "none",
      textAnchor: "middle",
      fill: theme.palette.text.primary
    },

    "& .node text.textClone": {
      stroke: theme.palette.background.default
    },
    "& line.link": {
      fill: "none",
      stroke: "#9ecae1",
      strokeWidth: "2px"
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

  const nodeEnter = node.enter().append("g")
    .attr("class", "node")
    .call(force.drag);

  nodeEnter.append("circle")
    .attr("r", 6 );

  node.append("text")
    .attr("dy", ".35em")
    .attr("y", 16)
    .text(d => d.type ? `${d.name} (${d.type.toLowerCase()})` : d.name)
    .attr("fill", "none")
    .attr("class", "textClone")
    .attr("stroke-width", 3);

  nodeEnter.append("text")
    .attr("dy", ".35em")
    .attr("y", 16)
    .text(d => d.type ? `${d.name} (${d.type.toLowerCase()})` : d.name);

  node.select("circle")
    .style("fill", color);
}

function color(d) {
  return d._children ? "#3182bd" // collapsed package
    : d.children ? "#c6dbef" // expanded package
      : "#fd8d3c"; // leaf node
}

interface Props {
  open: boolean;
  selection: string[];
  closeMenu: NoArgFunction;
  setDialogOpened: StringArgFunction;
  entityRelationTypes?: EntityRelationType[];
}

const RelationshipView: React.FC<Props> = props => {
  const {
    open, closeMenu, setDialogOpened, entityRelationTypes, selection
  } = props;

  const [relationTypes, setRelationTypes] = useState<any[]>([]);
  const [coursesData, setCoursesData] = useState<Course[]>([]);
  const [coursesLoading, setCoursesLoading] = useState<boolean>(false);

  const dispatch = useAppDispatch();

  const ref = useRef<any>();
  const forceRef = useRef<any>();

  const classes = useStyles();

  const onClose = useCallback(() => {
    setDialogOpened(null);
    setCoursesData([]);
    closeMenu();
  }, []);

  const syncCourses = async () => {
    
    setCoursesLoading(true);
    const dataDraft = [];
    
    try {
      for (const courseId of selection) {
        const course = await CourseService.getCourse(Number(courseId));
        if (course.relatedSellables.length) {
          dataDraft.push(course);
        }
      }
    } catch (e) {
      instantFetchErrorHandler(dispatch, e);
    }

    setCoursesData(dataDraft);
    setCoursesLoading(false);
  };

  useEffect(() => {
    if (selection.length && open) {
      syncCourses();
    }
  }, [selection, open]);

  useEffect(() => {
    const types = [];
    const totalTypes = entityRelationTypes?.length;
    let colorIndex = 0;
    let colorCode = 400;
    for (let i = 0; i < totalTypes; i++) {
      if (((i + 1) % 16) === 0) {
        colorIndex = 0;
        colorCode += 200;
      } else {
        colorIndex++;
      }

      types.push({ ...entityRelationTypes[i], color: getColor(colorIndex, colorCode) });
    }
    setRelationTypes(types);
  }, [entityRelationTypes]);

  useEffect(() => {
    if (open && coursesData.length) {
        const { width, height } =  ref.current.getBoundingClientRect();

        forceRef.current = d3.layout.force()
          .linkDistance(120)
          .charge(-130)
          .gravity(0.05)
          .size([width, height]);

        d3.select(ref.current).select("svg").remove();

        const svg = d3.select(ref.current).append("svg")
          .attr("width", width)
          .attr("height", height);

        const courses = coursesData.map(c => {
          return {
            id: c.id,
            name: c.name,
            code: c.code,
            children: c.relatedSellables,
          };
        });

        // Add legend
        let baseOffset = 0;

        relationTypes.filter(t => coursesData
          .some(c => c.relatedSellables
            .some(r => r.relationId === t.id)))
          .forEach(t => {
          svg.append("circle")
            .attr("cx", 24)
            .attr("cy", 24 + baseOffset)
            .attr("r", 6)
            .style("fill", t.color);
          svg.append("text").attr("x", 44).attr("y", 24 + baseOffset).text(t.name)
            .style("font-size", "15px")
            .style("font-weight", "400")
            .attr("alignment-baseline", "middle");
          baseOffset += 30;
        });

        update(svg, forceRef.current, courses, ref.current, relationTypes);
    }
  }, [coursesData, open, ref.current, relationTypes]);

  const { width, height } = useWindowSize();

  useEffect(() => {
    if (open && ref.current) {
      const { width, height } =  ref.current?.getBoundingClientRect();

      d3.select(ref.current).select("svg")
        .attr("width", width)
        .attr("height", height);

      forceRef.current?.size([width, height]);
      forceRef.current?.start();
    }
  }, [width, height, open, ref.current]);

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
      <LoadingIndicator position="fixed" customLoading={coursesLoading} />
      <AppBarContainer
        title="Relationships"
        onCloseClick={onClose}
        disableInteraction
        hideHelpMenu
        hideSubmitButton
      >
        <div className={clsx(
          coursesData.length && "dotsBackgroundImage",
          coursesData.length && classes.graphRoot,
          !coursesData.length && "d-flex h-100"
        )} ref={ref} >
          {
            !coursesData.length &&  !coursesLoading && <div className="noRecordsMessage">
              <Typography variant="h6" color="inherit" align="center">
                No relations were found
              </Typography>
            </div>
          }
        </div>
      </AppBarContainer>
    </Dialog>
  );
};

const mapStateToProps = (state: State) => ({
  entityRelationTypes: state.preferences.entityRelationTypes
});

export default connect<any, any, Props>(mapStateToProps, null)(RelationshipView);