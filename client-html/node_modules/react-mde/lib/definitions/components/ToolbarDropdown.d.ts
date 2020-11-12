import * as React from "react";
import { Command, GetIcon } from "../types";
export interface ToolbarDropdownProps {
    getIcon: GetIcon;
    buttonContent: React.ReactNode;
    buttonProps: any;
    commands: Command[];
    onCommand: (command: Command) => void;
    readOnly: boolean;
}
export interface ToolbarDropdownState {
    open: boolean;
}
export declare class ToolbarDropdown extends React.Component<ToolbarDropdownProps, ToolbarDropdownState> {
    dropdown: any;
    dropdownOpener: any;
    constructor(props: ToolbarDropdownProps);
    componentDidMount(): void;
    componentWillUnmount(): void;
    handleGlobalClick: EventListenerOrEventListenerObject;
    openDropdown: () => void;
    closeDropdown(): void;
    clickedOutside: (e: Event) => boolean;
    handleOnClickCommand: (e: React.SyntheticEvent<any, Event>, command: Command) => void;
    handleClick: () => void;
    render(): JSX.Element;
}
