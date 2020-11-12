import * as React from "react";
import { Tab } from "../types/Tab";
import { L18n } from "..";
import { ClassValue } from "../util/ClassNames";
import { ButtonChildProps } from "../child-props";
export interface ToolbarButtonData {
    commandName: string;
    buttonContent: React.ReactNode;
    buttonProps: any;
    buttonComponentClass: React.ComponentClass | string;
}
export interface ToolbarProps {
    classes?: ClassValue;
    buttons: ToolbarButtonData[][];
    onCommand: (commandName: string) => void;
    onTabChange: (tab: Tab) => void;
    readOnly: boolean;
    disablePreview: boolean;
    tab: Tab;
    l18n: L18n;
    writeButtonProps: ButtonChildProps;
    previewButtonProps: ButtonChildProps;
    buttonProps: ButtonChildProps;
}
export declare class Toolbar extends React.Component<ToolbarProps> {
    handleTabChange: (tab: Tab) => void;
    render(): JSX.Element;
}
