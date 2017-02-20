import * as React from 'react';
import {findDOMNode} from 'react-dom';
import {stopPropagation} from '../../lib/utils';
import nativeExtend from './ConfirmOrderDialog.extend';
import $ from 'jquery';

const extend = Object.assign({}, nativeExtend, require('./ConfirmOrderDialog.custom'));

export class ConfirmOrderDialog extends React.Component<ConfirmOrderDialogProps, {}> {
  private $item: JQuery;

  constructor(props) {
    super();
    this.$item = $(`.classItem[data-classid=${props.id}]`);
  }

  componentWillMount = () => {
    $(document).on('click', this.props.close);
  };

  componentDidMount = () => {
    $(findDOMNode(this))
      .show()
      .offset({top: this.$item.offset().top} as JQueryCoordinates)
      .css('right', '150px')
      .fadeIn('fast');
  };

  componentWillUnmount = () => {
    $(document).off('click', this.props.close);
  };

  render() {
    return extend.render.apply({
      props: {
        isAlreadyAdded: this.props.isAlreadyAdded,
        id: this.props.id,
        name: this.props.name,
        //ToDo ask about time
        date: this.$item.find('.class-item-info-l > .date a:first').text()
      },
      methods: {
        close: this.props.close
      },
      utils: {stopPropagation}
    });
  }
}

interface ConfirmOrderDialogProps {
  isAlreadyAdded: boolean
  id: number
  name: string
  date: string
  close: () => void
}
