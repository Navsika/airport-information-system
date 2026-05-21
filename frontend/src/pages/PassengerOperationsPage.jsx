import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { checkIn } from '../services/checkIn';
import { flight } from '../services/flight';
import { passenger } from '../services/passenger';
import { ticket } from '../services/ticket';
import { STATUS_LABELS, formatScheduleDate, formatScheduleTime, translateTicketClass } from './flightMeta';
import { ErrorNote, StatusBadge } from './flightUi';

const cardClass = 'rounded-lg border border-slate-200 bg-white p-4 shadow-sm';
const inputClass = 'h-10 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-500 focus:ring-4 focus:ring-sky-100 disabled:bg-slate-100';
const buttonPrimary = 'rounded-md bg-sky-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-sky-700 disabled:cursor-not-allowed disabled:opacity-45';
const buttonGhost = 'rounded-md border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50';

const emptyTicket = {
  passengerId: '',
  flightId: '',
  ticketClass: 'Economy',
  seatNumber: '',
};

const emptyPassenger = {
  lastName: '',
  firstName: '',
  middleName: '',
  passportNumber: '',
  passportExpiryDate: '',
};

const emptyCheckIn = {
  terminal: 'A',
  counterNumber: '',
  baggageCount: '0',
  totalBaggageWeight: '0',
};

export const PassengerOperationsPage = () => {
  const [tab, setTab] = useState('tickets');

  return (
    <div className="min-h-[calc(100vh-56px)] bg-[#eef6fb]">
      <section className="border-b border-sky-100 bg-white">
        <div className="mx-auto grid max-w-7xl gap-4 px-6 py-6">
          <h1 className="text-3xl font-semibold tracking-tight text-slate-950">Пассажирские операции</h1>
          <div className="inline-flex w-fit rounded-lg border border-sky-100 bg-sky-50 p-1">
            <button className={tabButton(tab === 'tickets')} type="button" onClick={() => setTab('tickets')}>Продажа билета</button>
            <button className={tabButton(tab === 'checkIn')} type="button" onClick={() => setTab('checkIn')}>Регистрация</button>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 py-6">
        {tab === 'tickets' ? <TicketSaleWorkspace /> : <CheckInWorkspace />}
      </section>
    </div>
  );
};

const TicketSaleWorkspace = () => {
  const queryClient = useQueryClient();
  const [form, setForm] = useState(emptyTicket);
  const [passengerSearch, setPassengerSearch] = useState('');
  const [flightSearch, setFlightSearch] = useState('');
  const [isPassengerFormOpen, setPassengerFormOpen] = useState(false);
  const [passengerDraft, setPassengerDraft] = useState(emptyPassenger);

  const passengersQuery = useQuery({ queryKey: ['passengers', 'operations'], queryFn: passenger.getList, staleTime: 300000 });
  const flightsQuery = useQuery({ queryKey: ['flights', 'operations'], queryFn: () => flight.getAll({ page: 0, size: 100 }) });
  const ticketsQuery = useQuery({
    queryKey: ['tickets', form.flightId],
    queryFn: () => ticket.getByFlightId(form.flightId),
    enabled: Boolean(form.flightId),
  });

  const passengers = passengersQuery.data || [];
  const flights = flightsQuery.data?.content || [];
  const selectedPassenger = passengers.find((item) => String(item.passengerId) === String(form.passengerId));
  const selectedFlight = flights.find((item) => String(item.flightId) === String(form.flightId));
  const soldTickets = ticketsQuery.data || [];
  const soldSeats = soldTickets.map((item) => item.seatNumber).filter(Boolean);

  const filteredPassengers = useMemo(() => filterPassengers(passengers, passengerSearch), [passengers, passengerSearch]);
  const filteredFlights = useMemo(() => filterFlights(flights, flightSearch), [flights, flightSearch]);

  const createPassengerMutation = useMutation({
    mutationFn: () => passenger.create({
      lastName: passengerDraft.lastName.trim(),
      firstName: passengerDraft.firstName.trim(),
      middleName: passengerDraft.middleName.trim() || null,
      passportNumber: passengerDraft.passportNumber.trim(),
      passportExpiryDate: passengerDraft.passportExpiryDate,
    }),
    onSuccess: (created) => {
      queryClient.invalidateQueries({ queryKey: ['passengers'] });
      setForm((current) => ({ ...current, passengerId: String(created.passengerId) }));
      setPassengerSearch(passengerLabel(created));
      setPassengerDraft(emptyPassenger);
      setPassengerFormOpen(false);
    },
  });

  const sellMutation = useMutation({
    mutationFn: () => ticket.sell(form.flightId, {
      passengerId: Number(form.passengerId),
      ticketClass: form.ticketClass,
      seatNumber: form.seatNumber.trim().toUpperCase(),
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tickets', form.flightId] });
      queryClient.invalidateQueries({ queryKey: ['flight-passengers', Number(form.flightId)] });
      queryClient.invalidateQueries({ queryKey: ['flight-stats', Number(form.flightId)] });
      setForm((current) => ({ ...emptyTicket, passengerId: current.passengerId }));
    },
  });

  const canSell = form.passengerId && form.flightId && form.ticketClass && form.seatNumber.trim();

  return (
    <div className="grid gap-5 lg:grid-cols-[1fr_22rem]">
      <div className="grid gap-5">
        <OperationPanel title="1. Пассажир">
          <SearchBox
            label="Поиск пассажира"
            value={passengerSearch}
            onChange={setPassengerSearch}
            placeholder="Фамилия, имя или паспорт"
          />
          <div className="grid gap-2 md:grid-cols-2">
            {filteredPassengers.slice(0, 6).map((item) => (
              <button
                key={item.passengerId}
                className={`rounded-lg border px-4 py-3 text-left transition ${String(form.passengerId) === String(item.passengerId) ? 'border-sky-400 bg-sky-50 ring-2 ring-sky-100' : 'border-slate-200 bg-white hover:border-sky-200 hover:bg-sky-50/50'}`}
                type="button"
                onClick={() => {
                  setForm((current) => ({ ...current, passengerId: String(item.passengerId) }));
                  setPassengerSearch(passengerLabel(item));
                }}
              >
                <div className="font-semibold text-slate-950">{passengerName(item)}</div>
                <div className="mt-1 text-xs text-slate-500">Паспорт {item.passportNumber}</div>
              </button>
            ))}
          </div>
          <button className={buttonGhost} type="button" onClick={() => setPassengerFormOpen((current) => !current)}>
            {isPassengerFormOpen ? 'Скрыть форму пассажира' : 'Оформить нового пассажира'}
          </button>
          {isPassengerFormOpen && (
            <div className="grid gap-3 rounded-lg border border-sky-100 bg-sky-50/60 p-4 md:grid-cols-2">
              <FormField label="Фамилия" value={passengerDraft.lastName} onChange={(value) => setPassengerDraft((current) => ({ ...current, lastName: value }))} />
              <FormField label="Имя" value={passengerDraft.firstName} onChange={(value) => setPassengerDraft((current) => ({ ...current, firstName: value }))} />
              <FormField label="Отчество" value={passengerDraft.middleName} onChange={(value) => setPassengerDraft((current) => ({ ...current, middleName: value }))} />
              <FormField label="Паспорт" value={passengerDraft.passportNumber} onChange={(value) => setPassengerDraft((current) => ({ ...current, passportNumber: value }))} placeholder="Например, 4012345678" />
              <FormField label="Срок действия паспорта" type="date" value={passengerDraft.passportExpiryDate} onChange={(value) => setPassengerDraft((current) => ({ ...current, passportExpiryDate: value }))} />
              <div className="flex items-end">
                <button className={buttonPrimary} type="button" disabled={createPassengerMutation.isPending || !passengerDraft.lastName || !passengerDraft.firstName || !passengerDraft.passportNumber || !passengerDraft.passportExpiryDate} onClick={() => createPassengerMutation.mutate()}>
                  Сохранить пассажира
                </button>
              </div>
              <div className="md:col-span-2"><ErrorNote message={createPassengerMutation.error?.message} /></div>
            </div>
          )}
        </OperationPanel>

        <OperationPanel title="2. Рейс">
          <SearchBox label="Поиск рейса" value={flightSearch} onChange={setFlightSearch} placeholder="Номер рейса, аэропорт или авиакомпания" />
          <div className="grid gap-2 md:grid-cols-2">
            {filteredFlights.slice(0, 8).map((item) => (
              <FlightChoiceCard key={item.flightId} flight={item} active={String(form.flightId) === String(item.flightId)} onClick={() => setForm((current) => ({ ...current, flightId: String(item.flightId) }))} />
            ))}
          </div>
        </OperationPanel>

        <OperationPanel title="3. Билет">
          <div className="grid gap-3 md:grid-cols-3">
            <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
              Класс
              <select className={inputClass} value={form.ticketClass} onChange={(event) => setForm((current) => ({ ...current, ticketClass: event.target.value }))}>
                <option value="Economy">Эконом</option>
                <option value="Business">Бизнес</option>
              </select>
            </label>
            <FormField label="Место" value={form.seatNumber} onChange={(value) => setForm((current) => ({ ...current, seatNumber: value.toUpperCase() }))} placeholder="Например, 12A" />
            <div className="flex items-end">
              <button className={buttonPrimary} type="button" disabled={!canSell || sellMutation.isPending} onClick={() => sellMutation.mutate()}>
                Оформить билет
              </button>
            </div>
          </div>
          <ErrorNote message={sellMutation.error?.message} />
        </OperationPanel>
      </div>

      <aside className="grid h-fit gap-4">
        <SummaryCard title="Выбранный пассажир" value={selectedPassenger ? passengerName(selectedPassenger) : 'Не выбран'} hint={selectedPassenger?.passportNumber ? `Паспорт ${selectedPassenger.passportNumber}` : 'Найдите пассажира или создайте нового'} />
        <SummaryCard title="Выбранный рейс" value={selectedFlight ? selectedFlight.flightNumber : 'Не выбран'} hint={selectedFlight ? `${selectedFlight.departureAirport} → ${selectedFlight.arrivalAirport}` : 'Выберите рейс из списка'} />
        <SummaryCard title="Продано мест" value={selectedFlight ? `${soldTickets.length}/${selectedFlight.capacity ?? '-'}` : '-'} hint={soldSeats.length ? `Заняты: ${soldSeats.slice(0, 8).join(', ')}${soldSeats.length > 8 ? '...' : ''}` : 'Занятых мест пока нет'} />
        {sellMutation.isSuccess && <SuccessNote text="Билет оформлен. Его можно найти во вкладке регистрации по этому рейсу." />}
      </aside>
    </div>
  );
};

const CheckInWorkspace = () => {
  const queryClient = useQueryClient();
  const [flightSearch, setFlightSearch] = useState('');
  const [ticketSearch, setTicketSearch] = useState('');
  const [selectedFlightId, setSelectedFlightId] = useState('');
  const [selectedTicketId, setSelectedTicketId] = useState('');
  const [form, setForm] = useState(emptyCheckIn);

  const flightsQuery = useQuery({ queryKey: ['flights', 'operations'], queryFn: () => flight.getAll({ page: 0, size: 100 }) });
  const passengersQuery = useQuery({ queryKey: ['passengers', 'operations'], queryFn: passenger.getList, staleTime: 300000 });
  const ticketsQuery = useQuery({
    queryKey: ['tickets', selectedFlightId],
    queryFn: () => ticket.getByFlightId(selectedFlightId),
    enabled: Boolean(selectedFlightId),
  });
  const checkInsQuery = useQuery({
    queryKey: ['check-ins', selectedFlightId],
    queryFn: () => checkIn.getByFlightId(selectedFlightId),
    enabled: Boolean(selectedFlightId),
  });

  const flights = flightsQuery.data?.content || [];
  const passengersById = useMemo(() => new Map((passengersQuery.data || []).map((item) => [item.passengerId, item])), [passengersQuery.data]);
  const selectedFlight = flights.find((item) => String(item.flightId) === String(selectedFlightId));
  const assignedTerminal = getTerminalFromGate(selectedFlight?.gate);
  const checkedTicketIds = new Set((checkInsQuery.data || []).map((item) => item.ticketId));
  const tickets = ticketsQuery.data || [];
  const filteredFlights = useMemo(() => filterFlights(flights, flightSearch), [flights, flightSearch]);
  const filteredTickets = useMemo(() => {
    const normalized = ticketSearch.trim().toLowerCase();
    return tickets.filter((item) => {
      const person = passengersById.get(item.passengerId);
      const haystack = [
        item.ticketNumber,
        item.seatNumber,
        item.ticketClass,
        person?.lastName,
        person?.firstName,
        person?.passportNumber,
      ].filter(Boolean).join(' ').toLowerCase();
      return !normalized || haystack.includes(normalized);
    });
  }, [passengersById, ticketSearch, tickets]);
  const selectedTicket = tickets.find((item) => String(item.ticketId) === String(selectedTicketId));
  const selectedPassenger = selectedTicket ? passengersById.get(selectedTicket.passengerId) : null;
  const isAlreadyCheckedIn = selectedTicket ? checkedTicketIds.has(selectedTicket.ticketId) : false;
  const isCheckInStatus = ['Check-in', 'Boarding'].includes(selectedFlight?.status);

  useEffect(() => {
    if (assignedTerminal) {
      setForm((current) => ({ ...current, terminal: assignedTerminal }));
    }
  }, [assignedTerminal]);

  const checkInMutation = useMutation({
    mutationFn: () => checkIn.create(selectedTicketId, {
      checkInTime: toLocalDateTime(new Date()),
      terminal: (assignedTerminal || form.terminal).trim().toUpperCase(),
      counterNumber: form.counterNumber.trim().toUpperCase(),
      baggageCount: Number(form.baggageCount),
      totalBaggageWeight: Number(form.totalBaggageWeight),
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['check-ins', selectedFlightId] });
      queryClient.invalidateQueries({ queryKey: ['flight-passengers', Number(selectedFlightId)] });
      queryClient.invalidateQueries({ queryKey: ['flight-stats', Number(selectedFlightId)] });
      setSelectedTicketId('');
      setForm(emptyCheckIn);
    },
  });

  const canCheckIn = selectedFlightId && selectedTicketId && isCheckInStatus && !isAlreadyCheckedIn && form.terminal && Number(form.baggageCount) >= 0 && Number(form.totalBaggageWeight) >= 0;

  return (
    <div className="grid gap-5 lg:grid-cols-[1fr_22rem]">
      <div className="grid gap-5">
        <OperationPanel title="1. Рейс для регистрации">
          <SearchBox label="Поиск рейса" value={flightSearch} onChange={setFlightSearch} placeholder="Номер рейса, аэропорт или авиакомпания" />
          <div className="grid gap-2 md:grid-cols-2">
            {filteredFlights.slice(0, 8).map((item) => (
              <FlightChoiceCard
                key={item.flightId}
                flight={item}
                active={String(selectedFlightId) === String(item.flightId)}
                onClick={() => {
                  setSelectedFlightId(String(item.flightId));
                  setSelectedTicketId('');
                }}
              />
            ))}
          </div>
        </OperationPanel>

        <OperationPanel title="2. Билет пассажира">
          <SearchBox label="Поиск билета" value={ticketSearch} onChange={setTicketSearch} placeholder="Фамилия, паспорт, место или номер билета" />
          <div className="grid gap-2 md:grid-cols-2">
            {filteredTickets.map((item) => {
              const person = passengersById.get(item.passengerId);
              const checked = checkedTicketIds.has(item.ticketId);
              return (
                <button
                  key={item.ticketId}
                  className={`rounded-lg border px-4 py-3 text-left transition ${String(selectedTicketId) === String(item.ticketId) ? 'border-sky-400 bg-sky-50 ring-2 ring-sky-100' : 'border-slate-200 bg-white hover:border-sky-200 hover:bg-sky-50/50'}`}
                  type="button"
                  onClick={() => setSelectedTicketId(String(item.ticketId))}
                >
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <div className="font-semibold text-slate-950">{person ? passengerName(person) : `Пассажир ${item.passengerId}`}</div>
                      <div className="mt-1 text-xs text-slate-500">{item.ticketNumber} · место {item.seatNumber} · {translateTicketClass(item.ticketClass)}</div>
                    </div>
                    <span className={`rounded-full px-2 py-1 text-xs font-semibold ${checked ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'}`}>
                      {checked ? 'Зарегистрирован' : 'Ожидает'}
                    </span>
                  </div>
                </button>
              );
            })}
            {selectedFlightId && !filteredTickets.length && (
              <div className="rounded-lg border border-slate-200 bg-white px-4 py-5 text-sm text-slate-500 md:col-span-2">По выбранному рейсу билеты не найдены</div>
            )}
          </div>
        </OperationPanel>

        <OperationPanel title="3. Регистрация и багаж">
          <div className="grid gap-3 md:grid-cols-4">
            <FormField label="Терминал" value={assignedTerminal || form.terminal} onChange={(value) => setForm((current) => ({ ...current, terminal: value.toUpperCase() }))} placeholder="A" disabled={Boolean(assignedTerminal)} />
            <FormField label="Стойка" value={form.counterNumber} onChange={(value) => setForm((current) => ({ ...current, counterNumber: value.toUpperCase() }))} placeholder="12" />
            <FormField label="Мест багажа" type="number" value={form.baggageCount} onChange={(value) => setForm((current) => ({ ...current, baggageCount: value }))} />
            <FormField label="Вес, кг" type="number" value={form.totalBaggageWeight} onChange={(value) => setForm((current) => ({ ...current, totalBaggageWeight: value }))} />
          </div>
          {selectedFlight && !isCheckInStatus && (
            <div className="rounded-md border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-800">
              Регистрация закрыта для статуса «{STATUS_LABELS[selectedFlight.status] || selectedFlight.status}». Сервер всё равно выполнит окончательную проверку.
            </div>
          )}
          {assignedTerminal && (
            <div className="rounded-md border border-sky-200 bg-sky-50 px-3 py-2 text-sm text-sky-800">
              Терминал взят из гейта рейса: {selectedFlight.gate}. Стойка регистрации в карточке рейса сейчас не хранится, поэтому её нужно указать вручную.
            </div>
          )}
          {isAlreadyCheckedIn && <div className="rounded-md border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-800">Этот билет уже зарегистрирован</div>}
          <div className="flex justify-end">
            <button className={buttonPrimary} type="button" disabled={!canCheckIn || checkInMutation.isPending} onClick={() => checkInMutation.mutate()}>
              Зарегистрировать пассажира
            </button>
          </div>
          <ErrorNote message={checkInMutation.error?.message} />
        </OperationPanel>
      </div>

      <aside className="grid h-fit gap-4">
        <SummaryCard title="Рейс" value={selectedFlight ? selectedFlight.flightNumber : 'Не выбран'} hint={selectedFlight ? `${selectedFlight.departureAirport} → ${selectedFlight.arrivalAirport}` : 'Выберите рейс'} />
        <SummaryCard title="Статус регистрации" value={selectedFlight ? (isCheckInStatus ? 'Доступна' : 'Недоступна') : '-'} hint={selectedFlight ? (STATUS_LABELS[selectedFlight.status] || selectedFlight.status) : 'Статус появится после выбора рейса'} />
        <SummaryCard title="Пассажир" value={selectedPassenger ? passengerName(selectedPassenger) : 'Не выбран'} hint={selectedTicket ? `Место ${selectedTicket.seatNumber} · ${translateTicketClass(selectedTicket.ticketClass)}` : 'Выберите билет'} />
        {checkInMutation.isSuccess && <SuccessNote text="Пассажир зарегистрирован. Данные по явке и багажу обновлены." />}
      </aside>
    </div>
  );
};

const OperationPanel = ({ title, children }) => (
  <section className={cardClass}>
    <h2 className="mb-4 text-xs font-bold uppercase tracking-[0.18em] text-slate-500">{title}</h2>
    <div className="grid gap-4">{children}</div>
  </section>
);

const SearchBox = ({ label, value, onChange, placeholder }) => (
  <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
    {label}
    <div className="flex items-center gap-2 rounded-md border border-slate-300 bg-white px-3 transition focus-within:border-sky-500 focus-within:ring-4 focus-within:ring-sky-100">
      <input className="h-10 min-w-0 flex-1 border-0 bg-transparent p-0 text-sm text-slate-900 outline-none" value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder} />
      {!!value && <button className="text-xl leading-none text-slate-400 hover:text-slate-700" type="button" onClick={() => onChange('')}>×</button>}
    </div>
  </label>
);

const FormField = ({ label, value, onChange, type = 'text', placeholder, disabled = false }) => (
  <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
    {label}
    <input className={inputClass} type={type} value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder || ''} disabled={disabled} />
  </label>
);

const FlightChoiceCard = ({ flight: item, active, onClick }) => (
  <button
    className={`rounded-lg border px-4 py-3 text-left transition ${active ? 'border-sky-400 bg-sky-50 ring-2 ring-sky-100' : 'border-slate-200 bg-white hover:border-sky-200 hover:bg-sky-50/50'}`}
    type="button"
    onClick={onClick}
  >
    <div className="flex items-start justify-between gap-3">
      <div>
        <div className="text-lg font-semibold text-slate-950">{item.flightNumber}</div>
        <div className="mt-1 text-xs text-slate-500">{item.airlineName}</div>
      </div>
      <StatusBadge status={item.status} />
    </div>
    <div className="mt-3 text-sm font-medium text-slate-700">{item.departureAirport} → {item.arrivalAirport}</div>
    <div className="mt-1 text-xs text-slate-500">{formatScheduleDate(item.scheduledDeparture)} {formatScheduleTime(item.scheduledDeparture)}</div>
  </button>
);

const SummaryCard = ({ title, value, hint }) => (
  <div className={cardClass}>
    <div className="text-xs font-bold uppercase tracking-wide text-sky-700">{title}</div>
    <div className="mt-2 text-xl font-semibold text-slate-950">{value}</div>
    <div className="mt-1 text-sm text-slate-500">{hint}</div>
  </div>
);

const SuccessNote = ({ text }) => (
  <div className="rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium text-emerald-800">{text}</div>
);

const tabButton = (active) => `rounded-md px-4 py-2 text-sm font-semibold transition ${active ? 'bg-white text-sky-800 shadow-sm' : 'text-slate-600 hover:bg-white/70 hover:text-sky-800'}`;

const filterPassengers = (items, query) => {
  const normalized = query.trim().toLowerCase();
  if (!normalized) {
    return items;
  }
  return items.filter((item) => [item.lastName, item.firstName, item.middleName, item.passportNumber]
    .filter(Boolean)
    .join(' ')
    .toLowerCase()
    .includes(normalized));
};

const filterFlights = (items, query) => {
  const normalized = query.trim().toLowerCase();
  if (!normalized) {
    return items;
  }
  return items.filter((item) => [item.flightNumber, item.airlineName, item.departureAirport, item.arrivalAirport, item.aircraftRegNumber]
    .filter(Boolean)
    .join(' ')
    .toLowerCase()
    .includes(normalized));
};

const passengerName = (item) => [item.lastName, item.firstName, item.middleName].filter(Boolean).join(' ');
const passengerLabel = (item) => `${passengerName(item)} · ${item.passportNumber}`;

const toLocalDateTime = (date) => {
  const pad = (value) => String(value).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

const getTerminalFromGate = (gate) => {
  const match = String(gate || '').trim().match(/^[A-Za-zА-Яа-я]/);
  return match ? match[0].toUpperCase() : '';
};
