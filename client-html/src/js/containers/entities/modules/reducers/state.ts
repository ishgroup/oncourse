import { Module } from "@api/model";

export interface ModulesState {
  items?: Module[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}
