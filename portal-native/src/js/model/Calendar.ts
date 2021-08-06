export interface CalendarDay {
  day: number,
  date: Date,
  status: 'previous' | 'current' | 'next',
  marks: string[]
}
