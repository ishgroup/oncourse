export enum ClassCondition {
  'afterClassStarts',
  'beforeClassStarts',
  'afterClassEnds',
  'beforeClassEnds',
}

export enum ClassEnrolmentCondition {
  'afterClassStarts',
  'beforeClassStarts',
  'beforeClassEnds',
}

export class ClassAge {
  hideClassDays: number;
  hideClassCondition: ClassCondition;
  stopWebEnrolmentDays: number;
  stopWebEnrolmentCondition: ClassEnrolmentCondition;
}
