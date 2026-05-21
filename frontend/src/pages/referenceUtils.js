export const normalizePayload = (draft, fields) => {
  const payload = {};
  fields.forEach(([field, , type]) => {
    const value = draft[field];
    if (value === undefined || value === '') {
      return;
    }
    payload[field] = type === 'number' || type === 'lookup-select' ? Number(value) : value;
  });
  return payload;
};

export const fetchReferencePage = async (active, filters, page, selectedEmployeeId) => {
  if (active.id === 'employeeId' && selectedEmployeeId) {
    const result = await active.service.getById(selectedEmployeeId);
    return [result];
  }
  const params = normalizeFilters(active.facets || [], filters);
  if (active.service.getAll) {
    return active.service.getAll({
      ...params,
      page,
      size: 20,
    });
  }
  return active.service.getList();
};

export const normalizeFilters = (facets, filters) => facets.reduce((params, facet) => {
  const value = filters[facet.field];
  if (!hasFilterValue(value)) {
    return params;
  }

  if (facet.type === 'range') {
    const min = normalizeNumber(value.min);
    const max = normalizeNumber(value.max);
    if (min !== undefined) params[`${facet.field}Min`] = min;
    if (max !== undefined) params[`${facet.field}Max`] = max;
    return params;
  }

  if (facet.type === 'date-range') {
    if (value.min) params[`${facet.field}From`] = value.min;
    if (value.max) params[`${facet.field}To`] = value.max;
    return params;
  }

  if (facet.type === 'number' || facet.type === 'lookup-select') {
    const number = normalizeNumber(value);
    if (number !== undefined) params[facet.field] = number;
    return params;
  }

  params[facet.field] = String(value).trim();
  return params;
}, {});

export const normalizeNumber = (value) => {
  if (value === undefined || value === '') {
    return undefined;
  }
  const number = Number(value);
  return Number.isNaN(number) ? undefined : number;
};

export const hasFilterValue = (value) => {
  if (value === undefined || value === null || value === '') {
    return false;
  }
  if (typeof value === 'object') {
    return Boolean(value.min || value.max);
  }
  return true;
};

export const hasActiveFilters = (filters) => Object.values(filters).some(hasFilterValue);

export const getSearchPlaceholder = (field) => ({
  iataCode: 'Например, SVO',
  airlineName: 'Например, S7 Airlines',
  modelName: 'Например, Superjet 100',
  manufacturer: 'Например, Sukhoi',
  city: 'Например, Москва',
  country: 'Например, Россия',
  modelId: 'Введите ID модели',
  airlineId: 'Введите ID авиакомпании',
  manufactureYear: 'Например, 2020',
  lastMaintenanceDate: 'ГГГГ-ММ-ДД',
  flightHours: 'Например, 1240',
  lastName: 'Например, Иванов',
  category: 'Pilot или FlightAttendant',
  hireDateFrom: 'ГГГГ-ММ-ДД',
  hireDateTo: 'ГГГГ-ММ-ДД',
  passportNumber: 'Например, 4012 345678',
}[field] || 'Введите значение');

export const getCellValue = (row, field, lookupData) => {
  if (field === 'modelId') {
    const model = lookupData.models.find((item) => item.modelId === row.modelId);
    return model ? getLookupLabel(model, 'models') : String(row[field] ?? '');
  }
  if (field === 'airlineId') {
    const carrier = lookupData.airlines.find((item) => item.airlineId === row.airlineId);
    return carrier ? getLookupLabel(carrier, 'airlines') : String(row[field] ?? '');
  }
  return String(row[field] ?? '');
};

export const getLookupId = (item, lookupKey) => lookupKey === 'models' ? item.modelId : item.airlineId;

export const getLookupLabel = (item, lookupKey) => {
  if (lookupKey === 'models') {
    return [item.modelName, item.manufacturer].filter(Boolean).join(' · ');
  }
  return [item.iataCode, item.airlineName].filter(Boolean).join(' · ');
};

export const getUniqueOptions = (items, field) => {
  const seen = new Set();
  return items
    .map((item) => String(item[field] ?? '').trim())
    .filter(Boolean)
    .sort((a, b) => a.localeCompare(b, 'ru'))
    .filter((value) => {
      const key = value.toLowerCase();
      if (seen.has(key)) {
        return false;
      }
      seen.add(key);
      return true;
    })
    .map((value) => [value, value]);
};

export const translateOption = (option) => ({
  Pilot: 'Пилот',
  FlightAttendant: 'Бортпроводник',
}[option] || option);
