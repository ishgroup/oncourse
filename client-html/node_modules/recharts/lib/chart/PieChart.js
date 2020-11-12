"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _generateCategoricalChart = _interopRequireDefault(require("./generateCategoricalChart"));

var _PolarAngleAxis = _interopRequireDefault(require("../polar/PolarAngleAxis"));

var _PolarRadiusAxis = _interopRequireDefault(require("../polar/PolarRadiusAxis"));

var _PolarUtils = require("../util/PolarUtils");

var _Pie = _interopRequireDefault(require("../polar/Pie"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

/**
 * @fileOverview Pie Chart
 */
var _default = (0, _generateCategoricalChart["default"])({
  chartName: 'PieChart',
  GraphicalChild: _Pie["default"],
  eventType: 'item',
  legendContent: 'children',
  axisComponents: [{
    axisType: 'angleAxis',
    AxisComp: _PolarAngleAxis["default"]
  }, {
    axisType: 'radiusAxis',
    AxisComp: _PolarRadiusAxis["default"]
  }],
  formatAxisMap: _PolarUtils.formatAxisMap,
  defaultProps: {
    layout: 'centric',
    startAngle: 0,
    endAngle: 360,
    cx: '50%',
    cy: '50%',
    innerRadius: 0,
    outerRadius: '80%'
  }
});

exports["default"] = _default;