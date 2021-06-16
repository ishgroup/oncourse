/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export type AnyArgFunction<A = any, R = void> = (arg: A) => R;

export type StringArgFunction<R = any> = AnyArgFunction<string, R>;

export type BooleanArgFunction<R = any> = AnyArgFunction<boolean, R>;

export type NumberArgFunction<R = any> = AnyArgFunction<number, R>;

export type DateArgFunction<R = any> = AnyArgFunction<Date, R>;

export type NoArgFunction = () => void;
