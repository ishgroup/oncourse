"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _generateCategoricalChart = _interopRequireDefault(require("./generateCategoricalChart"));

var _Funnel = _interopRequireDefault(require("../numberAxis/Funnel"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

/**
 * @fileOverview Funnel Chart
 */
var _default = (0, _generateCategoricalChart["default"])({
  chartName: 'FunnelChart',
  GraphicalChild: _Funnel["default"],
  eventType: 'item',
  axisComponents: [],
  defaultProps: {
    layout: 'centric'
  }
});

exports["default"] = _default;