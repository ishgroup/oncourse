import { NormalizedSchema, schema } from 'normalizr';
import { Session } from '@api/model';
import { Normalized } from './Global';

export type SessionState = NormalizedSchema<Normalized<'session', Session>, string[]>;

export const sessionSchema = [new schema.Entity('session')];
