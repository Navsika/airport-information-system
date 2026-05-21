export const STATUSES = ['Scheduled', 'Delayed', 'Check-in', 'Boarding', 'Departed', 'Arrived', 'Cancelled'];

export const STATUS_LABELS = {
  Scheduled: 'По расписанию',
  Delayed: 'Задержан',
  'Check-in': 'Регистрация',
  Boarding: 'Посадка',
  Departed: 'Вылетел',
  Arrived: 'Прибыл',
  Cancelled: 'Отменен',
};

export const ROLE_LABELS = {
  Commander: 'Командир',
  'Co-pilot': 'Второй пилот',
  'Senior Flight Attendant': 'Старший бортпроводник',
  'Flight Attendant': 'Бортпроводник',
};

export const PASSENGER_STATUS_LABELS = {
  CHECKED_IN: 'Зарегистрирован',
  NO_SHOW: 'Не явился',
  CANCELLED: 'Рейс отменен',
  PENDING: 'Ожидает регистрации',
};

export const passengerStatusTone = {
  CHECKED_IN: 'bg-emerald-100 text-emerald-700',
  NO_SHOW: 'bg-rose-100 text-rose-700',
  CANCELLED: 'bg-slate-200 text-slate-600',
  PENDING: 'bg-amber-100 text-amber-700',
};

export const STATUS_TRANSITIONS = {
  Scheduled: ['Check-in', 'Delayed', 'Cancelled'],
  Delayed: ['Check-in', 'Cancelled'],
  'Check-in': ['Boarding', 'Cancelled'],
  Boarding: ['Departed', 'Cancelled'],
  Departed: ['Arrived'],
  Arrived: [],
  Cancelled: [],
};

export const PILOT_ROLES = ['Commander', 'Co-pilot'];
export const ATTENDANT_ROLES = ['Senior Flight Attendant', 'Flight Attendant'];

export const statusTone = {
  Scheduled: 'bg-slate-100 text-slate-700 ring-slate-200',
  Delayed: 'bg-amber-100 text-amber-800 ring-amber-200',
  'Check-in': 'bg-sky-100 text-sky-800 ring-sky-200',
  Boarding: 'bg-blue-100 text-blue-800 ring-blue-200',
  Departed: 'bg-cyan-100 text-cyan-800 ring-cyan-200',
  Arrived: 'bg-emerald-100 text-emerald-800 ring-emerald-200',
  Cancelled: 'bg-rose-100 text-rose-800 ring-rose-200',
};

export const timelineColor = {
  Scheduled: 'bg-slate-400',
  Delayed: 'bg-amber-500',
  'Check-in': 'bg-sky-500',
  Boarding: 'bg-blue-500',
  Departed: 'bg-cyan-500',
  Arrived: 'bg-emerald-500',
  Cancelled: 'bg-rose-500',
};

export const toOffsetStart = (value) => value ? new Date(`${value}T00:00:00`).toISOString() : undefined;
export const toOffsetEnd = (value) => value ? new Date(`${value}T23:59:59`).toISOString() : undefined;
export const isCrewAssignmentClosed = (flight) => ['Departed', 'Arrived', 'Cancelled'].includes(flight?.status);

export const getPassengerFlightStatus = (passenger, flightStatus) => {
  if (passenger.passengerFlightStatus && passenger.passengerFlightStatus !== 'PENDING') {
    return passenger.passengerFlightStatus;
  }
  if (passenger.checkedIn || passenger.isCheckedIn) {
    return 'CHECKED_IN';
  }
  if (flightStatus === 'Departed' || flightStatus === 'Arrived') {
    return 'NO_SHOW';
  }
  if (flightStatus === 'Cancelled') {
    return 'CANCELLED';
  }
  return passenger.passengerFlightStatus || 'PENDING';
};

export const formatScheduleDate = (value) => value ? new Intl.DateTimeFormat('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' }).format(new Date(value)) : '-';
export const formatScheduleTime = (value) => value ? new Intl.DateTimeFormat('ru-RU', { hour: '2-digit', minute: '2-digit' }).format(new Date(value)) : '-';
export const formatHistoryDate = (value) => value ? new Intl.DateTimeFormat('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' }).format(new Date(value)) : '-';
export const formatHistoryTime = (value) => value ? new Intl.DateTimeFormat('ru-RU', { hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(new Date(value)) : '-';
export const fullName = (item) => [item?.lastName, item?.firstName, item?.middleName].filter(Boolean).join(' ');

export const translateEmployeeCategory = (category) => ({
  Pilot: 'Пилот',
  FlightAttendant: 'Бортпроводник',
}[category] || category);

export const translateTicketClass = (ticketClass) => ({
  Business: 'Бизнес',
  Economy: 'Эконом',
}[ticketClass] || ticketClass);
