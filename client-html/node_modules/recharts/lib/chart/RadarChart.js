"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _generateCategoricalChart = _interopRequireDefault(require("./generateCategoricalChart"));

var _Radar = _interopRequireDefault(require("../polar/Radar"));

var _PolarAngleAxis = _interopRequireDefault(require("../polar/PolarAngleAxis"));

var _PolarRadiusAxis = _interopRequireDefault(require("../polar/PolarRadiusAxis"));

var _PolarUtils = require("../util/PolarUtils");

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

/**
 * @fileOverview Radar Chart
 */
var _default = (0, _generateCategoricalChart["default"])({
  chartName: 'RadarChart',
  GraphicalChild: _Radar["default"],
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
    startAngle: 90,
    endAngle: -270,
    cx: '50%',
    cy: '50%',
    innerRadius: 0,
    outerRadius: '80%'
  }
});

exports["default"] = _default;