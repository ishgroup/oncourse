import { Command, CommandContext, CommandMap, PasteOptions, Selection } from "../types";
import { L18n, TextApi, TextState } from "..";
import * as React from "react";
export declare class TextAreaTextApi implements TextApi {
    textAreaRef: React.RefObject<HTMLTextAreaElement>;
    constructor(textAreaRef: React.RefObject<HTMLTextAreaElement>);
    replaceSelection(text: string): TextState;
    setSelectionRange(selection: Selection): TextState;
    getState(): TextState;
}
export declare function getStateFromTextArea(textArea: HTMLTextAreaElement): TextState;
export declare class CommandOrchestrator {
    private readonly textAreaRef;
    private readonly textApi;
    private readonly commandMap;
    private readonly l18n;
    /**
     * Names of commands that can be activated by the keyboard
     */
    keyActivatedCommands: string[];
    /**
     * Indicates whether there is a command currently executing
     */
    isExecuting: boolean;
    private readonly pasteOptions?;
    constructor(customCommands: CommandMap, textArea: React.RefObject<HTMLTextAreaElement>, l18n?: L18n, pasteOptions?: PasteOptions);
    getCommand: (name: string) => Command;
    /**
     * Tries to find a command the wants to handle the keyboard event.
     * If a command is found, it is executed and the function returns
     */
    handlePossibleKeyCommand: (e: React.KeyboardEvent<HTMLTextAreaElement>) => boolean;
    executeCommand(commandName: string, context?: CommandContext): Promise<void>;
    /**
     * Executes the paste command
     */
    executePasteCommand(event: React.ClipboardEvent): Promise<void>;
    /**
     * Executes the drop command
     */
    executeDropCommand(event: React.DragEvent): Promise<void>;
    /**
     * Executes the "select image" command
     */
    executeSelectImageCommand(event: React.ChangeEvent): Promise<void>;
    /**
     * Returns a command by name
     * @param name
     */
    getCommandByName(name: string): Command;
}
