import * as React from "react";
export declare type GenerateMarkdownPreview = (markdown: string) => Promise<React.ReactNode>;
/**
 * If the command returns true for a given KeyboardEvent,
 * the command is executed
 */
export declare type HandleKeyCommand = (e: React.KeyboardEvent<HTMLTextAreaElement>) => boolean;
