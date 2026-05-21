import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { flight } from '../services/flight';
import { airport } from '../services/airport';
import { aircraft } from '../services/aircraft';
import { schedule } from '../services/schedule';
import { employee } from '../services/employee';
import { qualification } from '../services/qualification';
import { aircraftModel } from '../services/aircraftModel';
import { assignmentService } from '../services/assignment';
import {
  ATTENDANT_ROLES,
  PASSENGER_STATUS_LABELS,
  PILOT_ROLES,
  ROLE_LABELS,
  STATUSES,
  STATUS_LABELS,
  STATUS_TRANSITIONS,
  formatHistoryDate,
  formatHistoryTime,
  formatScheduleDate,
  formatScheduleTime,
  fullName,
  getPassengerFlightStatus,
  isCrewAssignmentClosed,
  passengerStatusTone,
  timelineColor,
  toOffsetEnd,
  toOffsetStart,
  translateEmployeeCategory,
  translateTicketClass,
} from './flightMeta';
import { Avatar, Empty, ErrorNote, Field, InfoTile, Metric, Panel, StatusBadge } from './flightUi';

const inputClass = 'h-11 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-500 focus:ring-4 focus:ring-sky-100';
const filterInputClass = 'h-8 w-full rounded-md border-0 bg-transparent px-0 text-sm font-semibold text-slate-950 outline-none';
const filterShell = 'rounded-md border border-sky-100 bg-white/90 px-3 py-2 shadow-sm transition focus-within:border-sky-400 focus-within:ring-2 focus-within:ring-sky-100';
const buttonPrimary = 'rounded-md bg-sky-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-sky-700 disabled:cursor-not-allowed disabled:opacity-45';
const buttonGhost = 'rounded-md border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50';

export const FlightsDashboard = () => {
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState({
    status: '',
    dateFrom: '',
    dateTo: '',
    departureAirportId: '',
    arrivalAirportId: '',
    aircraftRegNumber: '',
    page: 0,
    size: 10,
  });
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [isCreateOpen, setCreateOpen] = useState(false);

  const apiFilters = useMemo(() => ({
    status: filters.status || undefined,
    dateFrom: toOffsetStart(filters.dateFrom),
    dateTo: toOffsetEnd(filters.dateTo),
    departureAirportId: filters.departureAirportId || undefined,
    arrivalAirportId: filters.arrivalAirportId || undefined,
    aircraftRegNumber: filters.aircraftRegNumber || undefined,
    page: filters.page,
    size: filters.size,
  }), [filters]);

  const flightsQuery = useQuery({
    queryKey: ['flights', apiFilters],
    queryFn: () => flight.getAll(apiFilters),
    placeholderData: (previous) => previous,
  });

  const airportsQuery = useQuery({
    queryKey: ['airports', 'list'],
    queryFn: airport.getList,
    staleTime: 300000,
  });

  const flights = flightsQuery.data?.content || [];
  const totalPages = flightsQuery.data?.totalPages || 1;

  const setFilter = (key, value) => {
    setFilters((current) => ({ ...current, [key]: value, page: key === 'page' ? value : 0 }));
  };

  return (
    <div className="min-h-[calc(100vh-56px)] bg-[#eef6fb]">
      <section className="border-b border-sky-100 bg-white">
        <div className="mx-auto grid max-w-7xl gap-5 px-6 py-6">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <h1 className="text-3xl font-semibold tracking-tight text-slate-950">Панель диспетчера</h1>
            <button className={buttonPrimary} type="button" onClick={() => setCreateOpen(true)}>Создать рейс</button>
          </div>

          <div className="rounded-lg bg-sky-50/80 p-3">
            <div className="grid gap-3 md:grid-cols-6">
              <FilterBox label="Статус">
                <select className={filterInputClass} value={filters.status} onChange={(event) => setFilter('status', event.target.value)}>
                  <option value="">Все</option>
                  {STATUSES.map((status) => <option key={status} value={status}>{STATUS_LABELS[status]}</option>)}
                </select>
              </FilterBox>
              <FilterBox label="Самолет">
                <ClearableControl value={filters.aircraftRegNumber} onClear={() => setFilter('aircraftRegNumber', '')}>
                  <input className={filterInputClass} value={filters.aircraftRegNumber} onChange={(event) => setFilter('aircraftRegNumber', event.target.value.toUpperCase())} placeholder="RA-89001" />
                </ClearableControl>
              </FilterBox>
              <DateFilter label="Дата с" value={filters.dateFrom} onChange={(value) => setFilter('dateFrom', value)} />
              <DateFilter label="Дата по" value={filters.dateTo} onChange={(value) => setFilter('dateTo', value)} />
              <FilterBox label="Вылет">
                <ClearableControl value={filters.departureAirportId} onClear={() => setFilter('departureAirportId', '')}>
                  <AirportSelect value={filters.departureAirportId} airports={airportsQuery.data || []} onChange={(value) => setFilter('departureAirportId', value)} />
                </ClearableControl>
              </FilterBox>
              <FilterBox label="Прилет">
                <ClearableControl value={filters.arrivalAirportId} onClear={() => setFilter('arrivalAirportId', '')}>
                  <AirportSelect value={filters.arrivalAirportId} airports={airportsQuery.data || []} onChange={(value) => setFilter('arrivalAirportId', value)} />
                </ClearableControl>
              </FilterBox>
            </div>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 py-6">
        <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-sky-100 text-left text-xs font-semibold text-sky-950">
              <tr>
                <th className="px-5 py-4">Рейс</th>
                <th className="px-5 py-4">Маршрут</th>
                <th className="px-5 py-4">Расписание</th>
                <th className="px-5 py-4">Статус</th>
                <th className="px-5 py-4">Самолет</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {flights.map((item) => (
                <tr key={item.flightId} className={`${item.status === 'Delayed' ? 'bg-amber-50' : 'bg-white'} cursor-pointer transition hover:bg-sky-50/70`} onClick={() => setSelectedFlight(item)}>
                  <td className="px-5 py-4">
                    <div className="text-lg font-semibold text-slate-950">{item.flightNumber}</div>
                    <div className="text-xs font-medium text-slate-500">{item.airlineName}</div>
                  </td>
                  <td className="px-5 py-4">
                    <div className="inline-grid max-w-full grid-cols-[minmax(0,1fr)_2.25rem_minmax(0,1fr)] items-center gap-2 text-sm font-semibold text-slate-800">
                      <RoutePoint airport={item.departureAirport} city={item.departureCity} />
                      <span className="text-center text-base font-semibold text-sky-600">⟶</span>
                      <RoutePoint airport={item.arrivalAirport} city={item.arrivalCity} />
                    </div>
                  </td>
                  <td className="px-5 py-4">
                    <ScheduleRange departure={item.scheduledDeparture} arrival={item.scheduledArrival} />
                  </td>
                  <td className="px-5 py-4"><StatusBadge status={item.status} /></td>
                  <td className="px-5 py-4 text-sm font-medium text-slate-700">{item.aircraftRegNumber || 'Не назначен'}</td>
                </tr>
              ))}
              {!flights.length && (
                <tr>
                  <td className="px-5 py-12 text-center text-sm text-slate-500" colSpan="5">
                    {flightsQuery.isLoading ? 'Загрузка рейсов...' : 'Рейсы не найдены'}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="mt-4 flex items-center justify-end gap-3 text-sm text-slate-600">
          <button className="grid h-10 w-10 place-items-center rounded-full border border-slate-200 bg-white text-lg font-semibold text-slate-700 shadow-sm transition hover:bg-sky-50 disabled:opacity-40" disabled={filters.page === 0} onClick={() => setFilter('page', filters.page - 1)}>‹</button>
          <span className="text-xs font-semibold text-slate-500">{filters.page + 1} / {totalPages}</span>
          <button className="grid h-10 w-10 place-items-center rounded-full border border-slate-200 bg-white text-lg font-semibold text-slate-700 shadow-sm transition hover:bg-sky-50 disabled:opacity-40" disabled={filters.page + 1 >= totalPages} onClick={() => setFilter('page', filters.page + 1)}>›</button>
        </div>
      </section>

      {selectedFlight && (
        <FlightModal
          flightRow={selectedFlight}
          onClose={() => setSelectedFlight(null)}
          onChanged={() => queryClient.invalidateQueries({ queryKey: ['flights'] })}
        />
      )}
      {isCreateOpen && (
        <CreateFlightModal
          onClose={() => setCreateOpen(false)}
          onCreated={() => {
            setCreateOpen(false);
            queryClient.invalidateQueries({ queryKey: ['flights'] });
          }}
        />
      )}
    </div>
  );
};

const AirportSelect = ({ value, airports, onChange }) => (
  <select className={filterInputClass} value={value} onChange={(event) => onChange(event.target.value)}>
    <option value="">Все аэропорты</option>
    {airports.map((item) => <option key={item.airportId} value={item.airportId}>{item.iataCode} · {item.city}</option>)}
  </select>
);

const FilterBox = ({ label, children }) => (
  <div className={filterShell}>
    <Field label={label}>{children}</Field>
  </div>
);

const DateFilter = ({ label, value, onChange }) => (
  <div className={filterShell}>
    <Field label={label}>
      <div className="flex h-8 items-center gap-2">
        <input className={`${filterInputClass} date-filter-input ${value ? 'has-value' : ''} min-w-0 flex-1`} type="date" value={value} onChange={(event) => onChange(event.target.value)} />
        {value && (
          <button className="grid h-6 w-6 place-items-center rounded-full text-slate-400 transition hover:bg-slate-100 hover:text-slate-700" type="button" onClick={() => onChange('')}>
            ×
          </button>
        )}
      </div>
    </Field>
  </div>
);

const ClearableControl = ({ value, onClear, children }) => (
  <div className="flex h-8 items-center gap-2">
    <div className="min-w-0 flex-1">{children}</div>
    {value && (
      <button className="grid h-6 w-6 place-items-center rounded-full text-slate-400 transition hover:bg-slate-100 hover:text-slate-700" type="button" onClick={onClear}>
        ×
      </button>
    )}
  </div>
);

const CreateFlightModal = ({ onClose, onCreated }) => {
  const [draft, setDraft] = useState({
    scheduleId: '',
    flightDate: new Date().toISOString().slice(0, 10),
    aircraftId: '',
    gate: '',
  });
  const [scheduleSearch, setScheduleSearch] = useState('');

  const schedulesQuery = useQuery({ queryKey: ['schedules', 'selection'], queryFn: schedule.getAll, staleTime: 300000 });
  const aircraftQuery = useQuery({ queryKey: ['aircrafts', 'list'], queryFn: aircraft.getList, staleTime: 300000 });
  const createMutation = useMutation({
    mutationFn: () => flight.create({
      scheduleId: Number(draft.scheduleId),
      flightDate: draft.flightDate,
      aircraftId: draft.aircraftId ? Number(draft.aircraftId) : null,
      gate: draft.gate.trim() || null,
    }),
    onSuccess: onCreated,
  });

  const schedules = schedulesQuery.data || [];
  const aircrafts = aircraftQuery.data || [];
  const selectedSchedule = schedules.find((item) => String(item.scheduleId) === String(draft.scheduleId));
  const filteredSchedules = useMemo(() => {
    const value = scheduleSearch.trim().toLowerCase();
    if (!value) return schedules;
    return schedules.filter((item) => [
      item.flightNumber,
      item.airlineName,
      item.departureCity,
      item.arrivalCity,
    ].filter(Boolean).join(' ').toLowerCase().includes(value));
  }, [scheduleSearch, schedules]);
  const preview = selectedSchedule && draft.flightDate ? buildFlightPreview(selectedSchedule, draft.flightDate) : null;
  const canCreate = draft.scheduleId && draft.flightDate && !createMutation.isPending;

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/55 p-4">
      <div className="max-h-[92vh] w-full max-w-4xl overflow-y-auto rounded-lg bg-white shadow-2xl">
        <div className="flex items-start justify-between gap-4 border-b border-slate-200 px-6 py-5">
          <div>
            <h2 className="text-2xl font-semibold text-slate-950">Новый рейс</h2>
            <p className="mt-1 text-sm text-slate-500">Выберите шаблон расписания и дату. Время вылета и прилета рассчитается автоматически.</p>
          </div>
          <button className={buttonGhost} type="button" onClick={onClose}>Закрыть</button>
        </div>

        <div className="grid gap-5 px-6 py-5">
          <Panel title="Шаблон расписания">
            <Field label="Поиск шаблона">
              <input className={inputClass} value={scheduleSearch} onChange={(event) => setScheduleSearch(event.target.value)} placeholder="Номер рейса, город или авиакомпания" />
            </Field>
            <div className="grid max-h-72 gap-2 overflow-y-auto pr-1 md:grid-cols-2">
              {filteredSchedules.map((item) => (
                <button
                  key={item.scheduleId}
                  className={`rounded-lg border px-4 py-3 text-left transition ${String(draft.scheduleId) === String(item.scheduleId) ? 'border-sky-400 bg-sky-50 ring-2 ring-sky-100' : 'border-slate-200 bg-white hover:border-sky-200 hover:bg-sky-50/50'}`}
                  type="button"
                  onClick={() => setDraft((current) => ({ ...current, scheduleId: String(item.scheduleId) }))}
                >
                  <div className="text-lg font-semibold text-slate-950">{item.flightNumber}</div>
                  <div className="text-xs font-medium text-slate-500">{item.airlineName}</div>
                  <div className="mt-2 text-sm font-semibold text-slate-700">{item.departureCity} → {item.arrivalCity}</div>
                  <div className="mt-1 text-xs text-slate-500">{formatScheduleTemplateTime(item)}</div>
                </button>
              ))}
              {!filteredSchedules.length && <Empty text={schedulesQuery.isLoading ? 'Загрузка расписаний...' : 'Шаблоны не найдены'} />}
            </div>
          </Panel>

          <Panel title="Параметры выполнения">
            <div className="grid gap-3 md:grid-cols-3">
              <Field label="Дата рейса">
                <input className={inputClass} type="date" value={draft.flightDate} onChange={(event) => setDraft((current) => ({ ...current, flightDate: event.target.value }))} />
              </Field>
              <Field label="Самолет">
                <select className={inputClass} value={draft.aircraftId} onChange={(event) => setDraft((current) => ({ ...current, aircraftId: event.target.value }))}>
                  <option value="">Не назначать</option>
                  {aircrafts.map((item) => <option key={item.aircraftId} value={item.aircraftId}>{item.registrationNumber}</option>)}
                </select>
              </Field>
              <Field label="Гейт">
                <input className={inputClass} value={draft.gate} onChange={(event) => setDraft((current) => ({ ...current, gate: event.target.value.toUpperCase() }))} placeholder="Например, C12" maxLength={5} />
              </Field>
            </div>
            {preview && (
              <div className="grid gap-3 rounded-lg border border-sky-100 bg-sky-50/70 p-4 md:grid-cols-3">
                <InfoTile label="Рейс" value={`${selectedSchedule.flightNumber} · ${selectedSchedule.airlineName}`} />
                <InfoTile label="Вылет" value={`${preview.departureDate} ${preview.departureTime}`} />
                <InfoTile label="Прилет" value={`${preview.arrivalDate} ${preview.arrivalTime}`} />
              </div>
            )}
          </Panel>

          <ErrorNote message={createMutation.error?.message} />
          <div className="flex justify-end gap-3">
            <button className={buttonGhost} type="button" onClick={onClose}>Отмена</button>
            <button className={buttonPrimary} type="button" disabled={!canCreate} onClick={() => createMutation.mutate()}>Создать рейс</button>
          </div>
        </div>
      </div>
    </div>
  );
};

const ScheduleRange = ({ departure, arrival }) => (
  <div className="inline-grid grid-cols-[auto_2rem_auto] items-center gap-2">
    <SchedulePoint value={departure} />
    <span className="text-center text-base font-semibold text-sky-500">⟶</span>
    <SchedulePoint value={arrival} />
  </div>
);

const SchedulePoint = ({ value }) => (
  <div className="grid gap-0.5">
    <span className="text-[11px] font-medium leading-none text-slate-400">{formatScheduleDate(value)}</span>
    <span className="text-sm font-semibold text-slate-800">{formatScheduleTime(value)}</span>
  </div>
);

const RoutePoint = ({ airport, city }) => (
  <span className="grid min-w-0 gap-0.5">
    <span className="min-w-0 truncate leading-tight">{airport}</span>
    {city && <span className="min-w-0 truncate text-xs font-medium text-slate-400">{city}</span>}
  </span>
);

const formatRouteLabel = (item) => {
  const departure = item.departureCity ? `${item.departureAirport}, ${item.departureCity}` : item.departureAirport;
  const arrival = item.arrivalCity ? `${item.arrivalAirport}, ${item.arrivalCity}` : item.arrivalAirport;
  return `${departure} → ${arrival}`;
};

const formatScheduleTemplateTime = (item) => {
  const suffix = item.arrivalDayOffset ? ` +${item.arrivalDayOffset} дн.` : '';
  return `${String(item.departureTime || '').slice(0, 5)} → ${String(item.arrivalTime || '').slice(0, 5)}${suffix}`;
};

const buildFlightPreview = (scheduleItem, flightDate) => {
  const departure = new Date(`${flightDate}T${scheduleItem.departureTime || '00:00'}`);
  const arrival = new Date(`${flightDate}T${scheduleItem.arrivalTime || '00:00'}`);
  arrival.setDate(arrival.getDate() + Number(scheduleItem.arrivalDayOffset || 0));
  return {
    departureDate: formatScheduleDate(departure),
    departureTime: formatScheduleTime(departure),
    arrivalDate: formatScheduleDate(arrival),
    arrivalTime: formatScheduleTime(arrival),
  };
};

const FlightModal = ({ flightRow, onClose, onChanged }) => {
  const queryClient = useQueryClient();
  const [statusDraft, setStatusDraft] = useState(flightRow.status);
  const [reason, setReason] = useState('');
  const [crewForm, setCrewForm] = useState({ employeeId: '', employeeRole: '' });
  const [activeTab, setActiveTab] = useState('overview');
  const [profileEmployee, setProfileEmployee] = useState(null);

  const id = flightRow.flightId;
  const flightQuery = useQuery({ queryKey: ['flight', id], queryFn: () => flight.getById(id) });
  const statsQuery = useQuery({ queryKey: ['flight-stats', id], queryFn: () => flight.getStats(id) });
  const passengersQuery = useQuery({ queryKey: ['flight-passengers', id], queryFn: () => flight.getPassengers(id) });
  const crewQuery = useQuery({ queryKey: ['flight-crew', id], queryFn: () => assignmentService.getByFlightId(id) });
  const historyQuery = useQuery({ queryKey: ['flight-history', id], queryFn: () => flight.getStatusHistory(id) });
  const employeesQuery = useQuery({ queryKey: ['employees', 'list'], queryFn: employee.getList, staleTime: 300000 });

  const currentFlight = flightQuery.data || flightRow;
  const passengers = passengersQuery.data || [];
  const crew = crewQuery.data || [];
  const employees = employeesQuery.data || [];
  const stats = statsQuery.data;
  const employeesById = useMemo(() => new Map(employees.map((item) => [item.employeeId, item])), [employees]);
  const assignedSingleRoles = useMemo(() => new Set(crew.filter((item) => item.employeeRole === 'Commander' || item.employeeRole === 'Co-pilot' || item.employeeRole === 'Senior Flight Attendant').map((item) => item.employeeRole)), [crew]);
  const selectedCrewEmployee = employeesById.get(Number(crewForm.employeeId));
  const availableCrewRoles = selectedCrewEmployee?.category === 'Pilot' ? PILOT_ROLES : ATTENDANT_ROLES;
  const selectableCrewRoles = availableCrewRoles.filter((role) => !assignedSingleRoles.has(role));
  const allowedStatuses = STATUS_TRANSITIONS[currentFlight.status] || [];
  const history = historyQuery.data || [];
  const crewAssignmentClosed = isCrewAssignmentClosed(currentFlight);
  const passengerStats = useMemo(() => {
    const total = passengers.length;
    const checkedIn = passengers.filter((item) => getPassengerFlightStatus(item, currentFlight.status) === 'CHECKED_IN').length;
    const noShow = passengers.filter((item) => getPassengerFlightStatus(item, currentFlight.status) === 'NO_SHOW').length;
    const business = passengers.filter((item) => item.ticketClass === 'Business').length;
    const economy = passengers.filter((item) => item.ticketClass === 'Economy').length;
    return { total, checkedIn, noShow, business, economy };
  }, [currentFlight.status, passengers]);
  const capacity = stats?.capacity ?? flightRow.passengerCapacity;
  const soldTickets = stats?.soldTickets ?? 0;
  const checkedInPassengers = stats?.checkedInPassengers ?? 0;
  const showUpPercent = stats?.showUpPercent ?? 0;
  const baggageWeight = stats?.baggageWeight ?? 0;
  const baggageLimit = stats?.baggageLimit;
  const baggagePercent = stats?.baggagePercent ?? (baggageLimit ? Math.round((baggageWeight * 100) / baggageLimit) : 0);
  const statsReady = statsQuery.isSuccess && stats;
  const statsHint = statsQuery.isLoading ? 'загрузка сводки' : statsQuery.isError ? 'сводка недоступна' : undefined;

  useEffect(() => {
    setStatusDraft(currentFlight.status);
  }, [currentFlight.status]);

  const invalidateModal = () => {
    ['flight', 'flight-stats', 'flight-passengers', 'flight-crew', 'flight-history'].forEach((key) => {
      queryClient.invalidateQueries({ queryKey: [key, id] });
    });
    onChanged();
  };

  const statusMutation = useMutation({
    mutationFn: () => flight.updateStatus(id, statusDraft, reason),
    onSuccess: (updated) => {
      queryClient.setQueryData(['flight', id], updated);
      setStatusDraft(updated.status);
      invalidateModal();
      setReason('');
    },
  });

  const crewMutation = useMutation({
    mutationFn: () => assignmentService.assign(id, { employeeId: Number(crewForm.employeeId), employeeRole: crewForm.employeeRole }),
    onSuccess: () => {
      setCrewForm({ employeeId: '', employeeRole: '' });
      invalidateModal();
    },
  });

  const handleCrewEmployeeChange = (employeeId) => {
    const person = employeesById.get(Number(employeeId));
    const roles = person?.category === 'Pilot' ? PILOT_ROLES : ATTENDANT_ROLES;
    const firstAvailableRole = roles.find((role) => !assignedSingleRoles.has(role)) || '';
    setCrewForm({
      employeeId,
      employeeRole: firstAvailableRole,
    });
  };

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/55 p-4">
      <div className="max-h-[92vh] w-full max-w-6xl overflow-y-auto rounded-lg bg-white shadow-2xl">
        <div className="sticky top-0 z-10 border-b border-slate-200 bg-white">
          <div className="flex items-start justify-between gap-4 px-6 py-5">
            <div>
              <div className="flex flex-wrap items-center gap-3">
                <h2 className="text-3xl font-semibold text-slate-950">{flightRow.flightNumber}</h2>
                <StatusBadge status={currentFlight.status} />
              </div>
              <p className="mt-1 text-sm text-slate-500">
                {flightRow.airlineName} · {formatRouteLabel(flightRow)}
              </p>
            </div>
            <button className={buttonGhost} onClick={onClose}>Закрыть</button>
          </div>
        </div>

        <div className="grid gap-5 p-6">
          <StatusTimeline history={history} currentStatus={currentFlight.status} />

          <div className="grid gap-3 md:grid-cols-3">
            <Metric label="Продано билетов" value={statsReady ? `${soldTickets}/${capacity ?? '-'}` : '-'} tone="sky" hint={statsHint} />
            <Metric label="Явка пассажиров" value={statsReady ? `${showUpPercent}%` : '-'} progress={statsReady ? showUpPercent : undefined} tone={statsReady ? (showUpPercent >= 85 ? 'green' : showUpPercent >= 50 ? 'amber' : 'rose') : 'sky'} hint={statsReady ? `${checkedInPassengers} зарегистрировано` : statsHint} />
            <Metric label="Багаж" value={statsReady ? `${Number(baggageWeight).toFixed(1)} кг` : '-'} progress={statsReady && baggageLimit ? baggagePercent : undefined} tone={statsReady ? (baggagePercent >= 85 ? 'rose' : baggagePercent >= 60 ? 'amber' : 'green') : 'sky'} hint={statsReady ? (baggageLimit ? `лимит ${baggageLimit} кг` : 'лимит не указан') : statsHint} />
          </div>

          <div className="flex flex-wrap gap-2 rounded-lg border border-slate-200 bg-slate-50 p-2">
            {[
              ['overview', 'Сводка'],
              ['status', 'Статус'],
              ['crew', 'Экипаж'],
              ['passengers', 'Пассажиры'],
            ].map(([key, label]) => (
              <button key={key} className={`rounded-md px-4 py-2 text-sm font-semibold ${activeTab === key ? 'bg-sky-600 text-white' : 'bg-white text-slate-700 hover:bg-sky-50'}`} onClick={() => setActiveTab(key)}>
                {label}
              </button>
            ))}
          </div>

          {activeTab === 'overview' && (
            <Panel title="Сводка рейса">
              <div className="grid gap-3 lg:grid-cols-[1.2fr_1fr]">
                <div className="grid place-items-center rounded-lg border border-sky-100 bg-sky-50/70 p-6">
                  <div className="mb-4 text-xs font-bold uppercase tracking-wide text-sky-700">Расписание</div>
                  <LargeScheduleRange departure={currentFlight.scheduledDeparture} arrival={currentFlight.scheduledArrival} />
                </div>
                <div className="grid gap-3 sm:grid-cols-2">
                  <InfoTile label="Гейт" value={currentFlight.gate || 'Не назначен'} />
                  <InfoTile label="Самолет" value={flightRow.aircraftRegNumber || 'Не назначен'} />
                  <InfoTile label="Статус" value={STATUS_LABELS[currentFlight.status] || currentFlight.status} />
                  <InfoTile label="Вместимость" value={capacity ? `${capacity} мест` : 'Не определена'} />
                </div>
              </div>
              <StatusHistoryTable history={history} />
            </Panel>
          )}

          {activeTab === 'status' && (
            <Panel title="Статус рейса">
              <div className="grid gap-3 md:grid-cols-[1fr_1fr_auto]">
                <Field label="Новый статус">
                  <select className={inputClass} value={statusDraft} onChange={(event) => setStatusDraft(event.target.value)}>
                    <option value={currentFlight.status}>{STATUS_LABELS[currentFlight.status]}</option>
                    {allowedStatuses.map((status) => <option key={status} value={status}>{STATUS_LABELS[status]}</option>)}
                  </select>
                </Field>
                <Field label="Причина">
                  <input className={inputClass} value={reason} onChange={(event) => setReason(event.target.value)} placeholder="Например, готовность борта" />
                </Field>
                <button className={`${buttonPrimary} self-end`} disabled={statusDraft === currentFlight.status || statusMutation.isPending} onClick={() => statusMutation.mutate()}>
                  Обновить
                </button>
              </div>
              <ErrorNote message={statusMutation.error?.message} />
            </Panel>
          )}

          {activeTab === 'crew' && (
            <Panel title="Экипаж">
              <div className={`grid gap-3 ${crewForm.employeeId ? 'md:grid-cols-[1fr_1fr_auto]' : 'md:grid-cols-[1fr_auto]'}`}>
                <Field label="Сотрудник">
                  <select className={inputClass} value={crewForm.employeeId} disabled={crewAssignmentClosed} onChange={(event) => handleCrewEmployeeChange(event.target.value)}>
                    <option value="">Выберите</option>
                    {employees.map((item) => <option key={item.employeeId} value={item.employeeId}>{fullName(item)} · {item.category}</option>)}
                  </select>
                </Field>
                {crewForm.employeeId && (
                  <Field label="Роль">
                    <select className={inputClass} value={crewForm.employeeRole} disabled={crewAssignmentClosed} onChange={(event) => setCrewForm({ ...crewForm, employeeRole: event.target.value })}>
                      {availableCrewRoles.map((role) => (
                        <option key={role} value={role} disabled={assignedSingleRoles.has(role)}>
                          {ROLE_LABELS[role]}{assignedSingleRoles.has(role) ? ' · уже назначен' : ''}
                        </option>
                      ))}
                    </select>
                  </Field>
                )}
                <button className={`${buttonPrimary} self-end`} disabled={crewAssignmentClosed || !crewForm.employeeId || !crewForm.employeeRole || crewMutation.isPending} onClick={() => crewMutation.mutate()}>
                  Назначить
                </button>
              </div>
              {crewAssignmentClosed && (
                <div className="rounded-md border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-600">
                  Назначение экипажа закрыто: рейс уже вылетел, прибыл или отменен.
                </div>
              )}
              {crewForm.employeeId && selectableCrewRoles.length === 0 && (
                <div className="rounded-md border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-800">
                  Для выбранного сотрудника все допустимые роли на этом рейсе уже заняты.
                </div>
              )}
              <ErrorNote message={crewMutation.error?.message} />
              <div className="grid gap-3 md:grid-cols-2">
                {crew.length ? crew.map((item) => {
                  const person = employeesById.get(item.employeeId);
                  return (
                    <div key={item.assignmentId} className="flex items-center gap-3 rounded-lg border border-slate-200 bg-white p-3 shadow-sm">
                      <Avatar name={person ? fullName(person) : `Сотрудник ${item.employeeId}`} />
                      <div className="min-w-0 flex-1">
                        <div className="truncate text-sm font-semibold text-slate-900">{person ? fullName(person) : `Сотрудник ${item.employeeId}`}</div>
                        <div className="text-xs font-medium text-sky-700">{ROLE_LABELS[item.employeeRole] || item.employeeRole}</div>
                        {person && <div className="mt-1 text-xs text-slate-500">{translateEmployeeCategory(person.category)} · принят {formatScheduleDate(person.hireDate)}</div>}
                      </div>
                      {person && (
                        <button className="rounded-md border border-slate-200 px-2 py-1 text-xs font-semibold text-slate-600 hover:bg-sky-50" onClick={() => setProfileEmployee(person)}>
                          Карточка
                        </button>
                      )}
                    </div>
                  );
                }) : <Empty text="Экипаж ещё не назначен" />}
              </div>
            </Panel>
          )}

          {activeTab === 'passengers' && (
            <Panel title="Пассажиры рейса">
              <div className="grid gap-3 sm:grid-cols-4">
                <InfoTile label="В списке" value={passengerStats.total} />
                <InfoTile label="Зарегистрированы" value={passengerStats.checkedIn} />
                <InfoTile label="Не явились" value={passengerStats.noShow} />
                <InfoTile label="Бизнес / Эконом" value={`${passengerStats.business}/${passengerStats.economy}`} />
              </div>
              <div className="grid max-h-80 gap-3 overflow-y-auto md:grid-cols-2">
                {passengers.length ? passengers.map((item) => {
                  const passengerStatus = getPassengerFlightStatus(item, currentFlight.status);
                  return (
                  <div key={`${item.passportNumber}-${item.seatNumber}`} className="flex items-center gap-3 rounded-lg border border-slate-200 bg-white p-3 shadow-sm">
                    <Avatar name={`${item.lastName} ${item.firstName}`} />
                    <div className="min-w-0 flex-1">
                      <div className="truncate text-sm font-semibold text-slate-900">{item.lastName} {item.firstName}</div>
                      <div className="text-xs text-slate-500">Паспорт {item.passportNumber}</div>
                      <div className="mt-1 flex flex-wrap gap-1 text-xs">
                        <span className="rounded-full bg-sky-50 px-2 py-1 font-semibold text-sky-700">Место {item.seatNumber}</span>
                        <span className="rounded-full bg-slate-100 px-2 py-1 font-semibold text-slate-600">{translateTicketClass(item.ticketClass)}</span>
                      </div>
                    </div>
                    <span className={`rounded-full px-2 py-1 text-xs font-semibold ${passengerStatusTone[passengerStatus] || passengerStatusTone.PENDING}`}>
                      {PASSENGER_STATUS_LABELS[passengerStatus] || PASSENGER_STATUS_LABELS.PENDING}
                    </span>
                  </div>
                  );
                }) : <Empty text="Пассажиров по рейсу пока нет" />}
              </div>
            </Panel>
          )}

        </div>
      </div>
      {profileEmployee && <EmployeeProfileModal employee={profileEmployee} onClose={() => setProfileEmployee(null)} />}
    </div>
  );
};

const StatusTimeline = ({ history, currentStatus }) => {
  const points = history.length ? [...history].reverse() : [{ status: currentStatus, changeTime: null }];
  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white px-5 py-4 shadow-sm">
      <div className="flex min-w-max items-start py-1">
        {points.map((item, index) => {
          const active = item.status === currentStatus;
          const color = timelineColor[item.status] || timelineColor.Scheduled;
          return (
            <div key={`${item.status}-${item.changeTime || index}`} className="flex items-start">
              <div className="grid w-32 justify-items-center gap-2 text-center">
                <div className={`mt-2 h-3 w-3 rounded-full ${color} ${active ? 'ring-4 ring-sky-100' : ''}`} />
                <div className="text-xs font-semibold text-slate-900">{STATUS_LABELS[item.status] || item.status}</div>
                <div className="text-[11px] leading-tight text-slate-400">
                  {item.changeTime ? (
                    <>
                      <div>{formatHistoryDate(item.changeTime)}</div>
                      <div className="font-semibold text-slate-500">{formatHistoryTime(item.changeTime)}</div>
                    </>
                  ) : 'История пока пуста'}
                </div>
              </div>
              {index + 1 < points.length && <div className={`mt-[1.05rem] h-1 w-20 rounded-full ${color}`} />}
            </div>
          );
        })}
      </div>
    </div>
  );
};

const LargeScheduleRange = ({ departure, arrival }) => (
  <div className="inline-grid grid-cols-[auto_3rem_auto] items-center gap-4">
    <LargeSchedulePoint value={departure} />
    <span className="text-center text-2xl font-semibold text-sky-500">⟶</span>
    <LargeSchedulePoint value={arrival} />
  </div>
);

const LargeSchedulePoint = ({ value }) => (
  <div className="grid justify-items-center gap-1">
    <span className="text-sm font-medium text-slate-400">{formatScheduleDate(value)}</span>
    <span className="text-3xl font-semibold text-slate-950">{formatScheduleTime(value)}</span>
  </div>
);

const StatusHistoryTable = ({ history }) => (
  <div className="mt-4 overflow-hidden rounded-lg border border-slate-200">
    <table className="min-w-full divide-y divide-slate-200">
      <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
        <tr>
          <th className="px-4 py-3">Дата</th>
          <th className="px-4 py-3">Время</th>
          <th className="px-4 py-3">Статус</th>
          <th className="px-4 py-3">Причина</th>
        </tr>
      </thead>
      <tbody className="divide-y divide-slate-100 bg-white">
        {history.length ? history.map((item) => (
          <tr key={item.statusHistoryId}>
            <td className="px-4 py-3 text-sm text-slate-500">{formatHistoryDate(item.changeTime)}</td>
            <td className="px-4 py-3 text-sm font-semibold text-slate-800">{formatHistoryTime(item.changeTime)}</td>
            <td className="px-4 py-3 text-sm text-slate-800">{STATUS_LABELS[item.status] || item.status}</td>
            <td className="px-4 py-3 text-sm text-slate-500">{item.reason || 'Не указана'}</td>
          </tr>
        )) : (
          <tr>
            <td className="px-4 py-6 text-center text-sm text-slate-500" colSpan="4">История пока пуста</td>
          </tr>
        )}
      </tbody>
    </table>
  </div>
);

const EmployeeProfileModal = ({ employee, onClose }) => {
  const qualificationsQuery = useQuery({
    queryKey: ['employee-qualifications', employee.employeeId],
    queryFn: () => qualification.getByPilotId(employee.employeeId),
    enabled: employee.category === 'Pilot',
  });
  const modelsQuery = useQuery({
    queryKey: ['models', 'list'],
    queryFn: aircraftModel.getList,
    staleTime: 300000,
    enabled: employee.category === 'Pilot',
  });
  const modelsById = useMemo(() => new Map((modelsQuery.data || []).map((item) => [item.modelId, item])), [modelsQuery.data]);
  const qualifications = qualificationsQuery.data || [];

  return (
    <div className="fixed inset-0 z-60 grid place-items-center bg-slate-950/60 p-4">
      <div className="w-full max-w-2xl rounded-lg bg-white shadow-2xl">
        <div className="flex items-start justify-between gap-4 border-b border-slate-200 px-6 py-5">
          <div className="flex items-center gap-4">
            <Avatar name={fullName(employee)} />
            <div>
              <h3 className="text-2xl font-semibold text-slate-950">{fullName(employee)}</h3>
              <p className="text-sm text-slate-500">{translateEmployeeCategory(employee.category)}</p>
            </div>
          </div>
          <button className={buttonGhost} onClick={onClose}>Закрыть</button>
        </div>

        <div className="grid gap-5 px-6 py-5">
          <div className="grid gap-3 sm:grid-cols-3">
            <InfoTile label="Фамилия" value={employee.lastName} />
            <InfoTile label="Имя" value={employee.firstName} />
            <InfoTile label="Отчество" value={employee.middleName || '-'} />
            <InfoTile label="Категория" value={translateEmployeeCategory(employee.category)} />
            <InfoTile label="Дата найма" value={formatScheduleDate(employee.hireDate)} />
          </div>

          {employee.category === 'Pilot' ? (
            <section className="rounded-lg border border-slate-200 p-4">
              <h4 className="mb-3 text-xs font-bold uppercase tracking-wide text-slate-500">Лицензии и допуски</h4>
              <div className="grid gap-2">
                {qualificationsQuery.isLoading ? (
                  <Empty text="Загрузка допусков..." />
                ) : qualifications.length ? qualifications.map((item) => {
                  const model = modelsById.get(item.modelId);
                  return (
                    <div key={item.qualificationId} className="grid gap-2 rounded-md bg-slate-50 px-3 py-3 sm:grid-cols-[1fr_auto] sm:items-center">
                      <div>
                        <div className="text-sm font-semibold text-slate-900">{model ? `${model.modelName} · ${model.manufacturer}` : `Модель #${item.modelId}`}</div>
                        <div className="text-xs text-slate-500">Получена {formatScheduleDate(item.qualificationDate)}</div>
                      </div>
                      <div className="text-xs font-semibold text-slate-600">
                        {item.validUntil ? `до ${formatScheduleDate(item.validUntil)}` : 'без срока'}
                      </div>
                    </div>
                  );
                }) : (
                  <Empty text="Допуски не указаны" />
                )}
              </div>
            </section>
          ) : (
            <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-500">
              В базе для бортпроводников сейчас хранится ФИО, категория и дата найма. Полей образования или лицензий нет.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
