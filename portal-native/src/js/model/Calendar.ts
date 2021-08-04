export interface Session {
  id: number;
  name: string;
  collegeName: string;
  siteName: string;
  roomName: string;
  start: string;
  end: string;
  color: string;
}

export interface CalendarDay {
  day: number,
  date: Date,
  status: 'previous' | 'current' | 'next'
}
