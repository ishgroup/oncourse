/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import DragHandleRounded from '@mui/icons-material/DragIndicatorRounded';
import Slider from '@mui/material/Slider';
import debounce from 'lodash.debounce';
import React, { useCallback, useMemo } from 'react';
import { withStyles } from 'tss-react/mui';
import { AttandanceStepItem } from '../../../../../model/entities/CourseClass';

const SliderThumbComponent = props => (
  <span {...props}>
    <DragHandleRounded />
  </span>
);

const styles = () => ({
  sliderRoot: {
    color: "#868686",
    top: 17,
    height: 16,
    "& > span:last-child": {
      marginLeft: 2
    }
  },
  sliderRail: {
    height: 16,
    background: "transparent"
  },
  sliderTrack: {
    left: "50%",
    width: "50%",
    color: "rgb(221, 221, 221)",
    top: 4.5,
    height: 24,
    borderRadius: 15,
    padding: "3px 20px",
    marginLeft: -18
  },
  sliderMark: {
    width: 4,
    height: 16
  },
  sliderMarkActive: {
    opacity: 1,
    background: "#868686"
  },
  sliderThumb: {
    width: 20,
    height: 20,
    background: "transparent",
    borderRadius: 0,
    color: "#fff",
    marginTop: -2,
    marginLeft: -18,
    "& > svg": {
      width: 16,
      height: 16
    },
    "&:focus,&:hover": {
      boxShadow: "none"
    }
  }
});

interface Props {
  stepItems: AttandanceStepItem[];
  setSelectedItems: (items: AttandanceStepItem[]) => void;
  sliderValue: number[];
  setSliderValue: any;
  classes?: any;
}

const getUpdatedValue = (newValue, prevValue, stepItemsLength) => {
  if (prevValue[0] > newValue[0]) {
    return [newValue[0], prevValue[1] - (prevValue[0] - newValue[0])];
  }
  if (prevValue[0] < newValue[0]) {
    if (newValue[0] >= stepItemsLength - 7) {
      return prevValue;
    }

    return [newValue[0], prevValue[1] + (newValue[0] - prevValue[0])];
  }
  if (prevValue[1] > newValue[1]) {
    const updated = prevValue[0] - (prevValue[1] - newValue[1]);

    if (updated < 0) {
      return prevValue;
    }

    return [updated, newValue[1]];
  }
  if (prevValue[1] < newValue[1]) {
    return [prevValue[0] + (newValue[1] - prevValue[1]), newValue[1]];
  }
  return newValue;
};

const AttendanceSlider: React.FC<Props> = ({
  stepItems, setSelectedItems, classes, setSliderValue, sliderValue
}) => {
  const debounceChange = useCallback<any>(
    debounce(val => {
      setSelectedItems(stepItems.slice(val[0], val[1] + 1));
    }, 200),
    [stepItems]
  );

  const handleSliderValueChange = useCallback(
    (event, newValue) => {
      setSliderValue(prevValue => {
        const updated = getUpdatedValue(newValue, prevValue, stepItems.length);

        debounceChange(updated);
        return updated;
      });
    },
    [stepItems.length]
  );

  const isStatic = useMemo(() => stepItems.length < 8, [stepItems.length]);

  return (
    <Slider
      value={sliderValue}
      onChange={handleSliderValueChange}
      valueLabelDisplay="off"
      aria-labelledby="session-range-slider"
      step={1}
      max={stepItems.length - 1}
      style={{ width: `${24 * (stepItems.length - 1)}px` }}
      classes={{
        root: `absolute w-100 ${classes.sliderRoot}`,
        rail: classes.sliderRail,
        track: isStatic ? "d-none" : classes.sliderTrack,
        mark: classes.sliderMark,
        thumb: classes.sliderThumb,
        markActive: classes.sliderMarkActive
      }}
      valueLabelFormat={isStatic ? undefined : SliderThumbComponent}
      disabled={isStatic}
      marks
    />
  );
};

export default withStyles(AttendanceSlider, styles);
