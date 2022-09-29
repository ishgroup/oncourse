/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

// Temporary implementation
// TODO: Remove component when native DynamicSizeList will be included in react-window v.2
import React from "react";
import { VariableSizeList } from "react-window";
import debounce from "lodash.debounce";

class ItemMeasurer extends React.Component<any, any> {
  _node = null;

  _resizeObserver = null;

  componentDidMount() {
    this._measureItem();

    // @ts-ignore
    if (typeof window.ResizeObserver !== "undefined") {
      // @ts-ignore
      this._resizeObserver = new window.ResizeObserver(this._onResize);

      if (this._node !== null) {
        this._resizeObserver.observe(this._node);
      }
    } else {
      //
    }
  }

  componentWillUnmount() {
    if (this._resizeObserver !== null) {
      this._resizeObserver.disconnect();
      this._resizeObserver = null;
    }
  }

  render() {
    return React.cloneElement(this.props.item, {
      ref: this._refSetter
    });
  }

  _measureItem = () => {
    const { handleNewMeasurements, index } = this.props;

    const node = this._node;

    if (node) {
      const { height, width } = node.getBoundingClientRect();
      const isVisible = height > 0 || width > 0;

      if (isVisible) {
        const newSize = Math.ceil(height);

        handleNewMeasurements(index, newSize);
      }
    }
  };

  _refSetter = ref => {
    if (this._resizeObserver !== null && this._node !== null) {
      this._resizeObserver.unobserve(this._node);
    }

    if (ref) {
      this._node = ref;
    } else {
      this._node = null;
    }

    if (this._resizeObserver !== null && this._node !== null) {
      this._resizeObserver.observe(this._node);
    }
  };

  _onResize = entries => {
    const _this = this;
    window.requestAnimationFrame(() => {
      if (!Array.isArray(entries) || !entries.length) {
        return;
      }
      _this._measureItem();
    });
  };
}

class DynamicSizeList extends React.Component<any> {
  listRef = React.createRef<any>();

  rowSizes = [];

  componentDidMount() {
    const ref = this.listRef.current;
    if (ref) {
      ref.resetAfterIndex(0, true);
    }
  }

  attachRefs = node => {
    const { listRef } = this.props;

    if (node) {
      if (!this.listRef.current) {
        // @ts-ignore
        this.listRef.current = node;
      }

      if (typeof listRef === "function") {
        listRef(node);
      }

      if (typeof listRef === "object") {
        listRef.current = node;
      }
    }
  };

  render() {
    const { children, ...rest } = this.props;

    return (
      <VariableSizeList
        {...rest}
        ref={this.attachRefs}
        itemSize={this.getItemSize}
      >
        {this.renderItem}
      </VariableSizeList>
    );
  }

  scrollToItem(index, align) {
    const ref = this.listRef.current;

    if (ref) {
      ref.scrollToItem(index, align);
      //
      window.requestAnimationFrame(() => {
        ref.scrollToItem(index, align);
      });
    }
  }

  renderItem = props => {
    // clear height
    if (props.style.height !== undefined) {
      props.style.height = undefined;
    }

    return React.createElement<any>(ItemMeasurer, {
      index: props.index,
      // @ts-ignore
      item: React.createElement(this.props.children, props),
      handleNewMeasurements: this.handleNewMeasurements
    });
  };

  handleNewMeasurements = (index, newSize) => {
    const itemSizeDelta = newSize - this.getItemSize(index);

    if (itemSizeDelta !== 0) {
      this.rowSizes[index] = newSize;

      const ref = this.listRef.current;

      if (ref) {
        const { isScrolling, scrollDirection } = ref.state;

        if (isScrolling && scrollDirection === "backward") {
          ref._outerRef.scrollBy({
            top: itemSizeDelta,
            left: 0,
            behavior: "auto"
          });
        }
        ref.resetAfterIndex(index, true);
      }
    }
  };

  getItemSize = index => {
    // https://react-window.now.sh/#/api/VariableSizeList
    const defaultEstimatedItemSize = 50;

    return (
      this.rowSizes[index]
      || this.props.estimatedItemSize
      || defaultEstimatedItemSize
    );
  };
}

export default DynamicSizeList;
