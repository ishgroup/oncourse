import React, { useEffect } from "react";
import CustomSelectorHeader from "./CustomSelectorHeader";
import CustomSelectorBody from "./CustomSelectorBody";

interface Props {
  className?: any;
  caption: string;
  options: CustomSelectorOption[];
  onSelect?: (index: any) => void;
  initialIndex?: number;
  disabled?: boolean;
}

export interface CustomSelectorOption {
  caption?: string;
  type?: string;
  component?: any;
  body?: string;
  validate?: any;
  normalize?: any;
  format?: any;
  parse?: any;
  min?: number;
  fieldName?: string;
  preformatDisplayValue?: any;
}

const CustomSelector = ({ className, caption, options, onSelect, initialIndex, disabled }: Props) => {
  const [selectedIndex, setSelectedIndex] = React.useState<null | number>(0);

  useEffect(
    () => {
      setSelectedIndex(initialIndex);
    },
    [initialIndex]
  );

  return (
    <div className={className}>
      <CustomSelectorHeader
        options={options}
        selectedIndex={selectedIndex}
        setSelectedIndex={setSelectedIndex}
        caption={caption}
        onSelect={onSelect}
        disabled={disabled}
      />
      <CustomSelectorBody options={options} selectedIndex={selectedIndex} />
    </div>
  );
};

export default CustomSelector;
