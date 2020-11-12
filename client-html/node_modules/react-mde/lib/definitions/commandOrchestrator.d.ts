import { Command, CommandGroup, TextRange } from "./types";
import { TextApi, TextState } from "./types/CommandOptions";
import * as React from "react";
export declare class TextAreaTextApi implements TextApi {
    textAreaRef: React.RefObject<HTMLTextAreaElement>;
    constructor(textAreaRef: React.RefObject<HTMLTextAreaElement>);
    replaceSelection(text: string): TextState;
    setSelectionRange(selection: TextRange): TextState;
    getState(): TextState;
}
export declare function getStateFromTextArea(textArea: HTMLTextAreaElement): TextState;
export declare class CommandOrchestrator {
    textAreaRef: React.RefObject<HTMLTextAreaElement>;
    textApi: TextApi;
    commands: CommandGroup[];
    keyActivatedCommands: Command[];
    /**
     * Indicates whether there is a command currently executing
     */
    isExecuting: boolean;
    constructor(commands: CommandGroup[], textArea: React.RefObject<HTMLTextAreaElement>);
    /**
     * Tries to find a command the wants to handle the keyboard event
     */
    handlePossibleKeyCommand: (e: React.KeyboardEvent<HTMLTextAreaElement>) => boolean;
    executeCommand(command: Command): Promise<void>;
}
