import {
  getLookupId,
  getLookupLabel,
  getSearchPlaceholder,
  getUniqueOptions,
  translateOption,
} from './referenceUtils';

export const inputClass = 'h-9 rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-sky-500 focus:ring-4 focus:ring-sky-100';
export const primaryButton = 'rounded-md bg-sky-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-sky-700 disabled:cursor-not-allowed disabled:opacity-45';
export const ghostButton = 'rounded-md border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50';

export const ReferenceField = ({ field, label, type, options, placeholder, value, lookupData, onChange }) => {
  if (type === 'select') {
    return (
      <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
        {label}
        <select className={inputClass} value={value} onChange={(event) => onChange(event.target.value)}>
          <option value="">Выберите</option>
          {options.map((option) => <option key={option} value={option}>{translateOption(option)}</option>)}
        </select>
      </label>
    );
  }

  if (type === 'lookup-select') {
    return (
      <SelectField
        label={label}
        value={value}
        options={(lookupData[options] || []).map((item) => [String(getLookupId(item, options)), getLookupLabel(item, options)])}
        onChange={onChange}
      />
    );
  }

  if (type === 'value-select') {
    return (
      <SelectField
        label={label}
        value={value}
        options={getUniqueOptions(lookupData.current, options || field)}
        onChange={onChange}
      />
    );
  }

  return (
    <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
      {label}
      <input className={inputClass} type={type} value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder || ''} />
    </label>
  );
};

export const FacetFilter = ({ facet, value, lookupData, onChange, onClear }) => {
  if (facet.type === 'range') {
    return (
      <RangeFilter
        label={facet.label}
        value={value || {}}
        onChange={(bound, nextValue) => onChange({ ...(value || {}), [bound]: nextValue })}
        onClear={onClear}
      />
    );
  }

  if (facet.type === 'date-range') {
    return (
      <RangeFilter
        label={facet.label}
        type="date"
        value={value || {}}
        onChange={(bound, nextValue) => onChange({ ...(value || {}), [bound]: nextValue })}
        onClear={onClear}
      />
    );
  }

  if (facet.type === 'choice') {
    return (
      <ChoiceFilter
        label={facet.label}
        value={value || ''}
        options={facet.options}
        onChange={onChange}
        onClear={onClear}
      />
    );
  }

  if (facet.type === 'lookup-select') {
    const options = lookupData[facet.lookup] || [];
    return (
      <SelectFilter
        label={facet.label}
        value={value || ''}
        options={options.map((item) => [String(getLookupId(item, facet.lookup)), getLookupLabel(item, facet.lookup)])}
        onChange={onChange}
        onClear={onClear}
      />
    );
  }

  if (facet.type === 'value-select') {
    return (
      <SelectFilter
        label={facet.label}
        value={value || ''}
        options={getUniqueOptions(lookupData.current, facet.sourceField || facet.field)}
        onChange={onChange}
        onClear={onClear}
      />
    );
  }

  return (
    <ClearableInput
      label={facet.label}
      type={facet.type === 'number' || facet.type === 'date' ? facet.type : 'text'}
      value={value || ''}
      onChange={onChange}
      onClear={onClear}
      placeholder={facet.placeholder || getSearchPlaceholder(facet.field)}
    />
  );
};

const SelectField = ({ label, value, options, onChange }) => (
  <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
    {label}
    <select className={inputClass} value={value} onChange={(event) => onChange(event.target.value)}>
      <option value="">Выберите</option>
      {options.map(([optionValue, optionLabel]) => <option key={optionValue} value={optionValue}>{optionLabel}</option>)}
    </select>
  </label>
);

const ClearableInput = ({ label, value, onChange, onClear, placeholder, type = 'text' }) => (
  <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
    {label}
    <div className="flex items-center gap-2 rounded-md border border-slate-300 bg-white px-3 transition focus-within:border-sky-500 focus-within:ring-4 focus-within:ring-sky-100">
      <input
        className="h-9 min-w-0 flex-1 border-0 bg-transparent p-0 text-sm text-slate-900 outline-none"
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
      />
      {!!value && (
        <button className="grid h-6 w-6 place-items-center rounded-full text-slate-400 transition hover:bg-slate-100 hover:text-slate-700" type="button" onClick={onClear}>
          ×
        </button>
      )}
    </div>
  </label>
);

const SelectFilter = ({ label, value, options, onChange, onClear }) => (
  <label className="grid gap-1.5 text-xs font-semibold uppercase tracking-wide text-slate-500">
    <span className="flex items-center justify-between gap-2">
      {label}
      {!!value && (
        <button className="text-xs font-semibold normal-case tracking-normal text-sky-700 hover:text-sky-900" type="button" onClick={onClear}>
          Сброс
        </button>
      )}
    </span>
    <div className="rounded-md border border-slate-300 bg-white px-3 transition focus-within:border-sky-500 focus-within:ring-4 focus-within:ring-sky-100">
      <select
        className="h-9 w-full border-0 bg-transparent p-0 text-sm text-slate-900 outline-none"
        value={value}
        onChange={(event) => onChange(event.target.value)}
      >
        <option value="">Любой</option>
        {options.map(([optionValue, optionLabel]) => <option key={optionValue} value={optionValue}>{optionLabel}</option>)}
      </select>
    </div>
  </label>
);

const ChoiceFilter = ({ label, value, options, onChange, onClear }) => (
  <div className="rounded-md border border-sky-100 bg-sky-50/60 p-3">
    <div className="mb-2 flex items-center justify-between gap-2">
      <div className="text-xs font-bold uppercase tracking-wide text-slate-500">{label}</div>
      {!!value && (
        <button className="text-xs font-semibold text-sky-700 hover:text-sky-900" type="button" onClick={onClear}>
          Сброс
        </button>
      )}
    </div>
    <div className="grid gap-2">
      {options.map(([optionValue, optionLabel]) => (
        <button
          key={optionValue}
          className={`flex items-center gap-2 rounded-md px-2 py-1.5 text-left text-sm font-medium transition ${value === optionValue ? 'bg-white text-sky-800 shadow-sm ring-1 ring-sky-200' : 'text-slate-700 hover:bg-white/70'}`}
          type="button"
          onClick={() => onChange(value === optionValue ? '' : optionValue)}
        >
          <span className={`h-4 w-4 rounded border ${value === optionValue ? 'border-sky-600 bg-sky-600' : 'border-slate-300 bg-white'}`} />
          {optionLabel}
        </button>
      ))}
    </div>
  </div>
);

const RangeFilter = ({ label, value, onChange, onClear, type = 'number' }) => (
  <div className="rounded-md border border-sky-100 bg-sky-50/60 p-3">
    <div className="mb-2 flex items-center justify-between gap-2">
      <div className="text-xs font-bold uppercase tracking-wide text-slate-500">{label}</div>
      {(value.min || value.max) && (
        <button className="text-xs font-semibold text-sky-700 hover:text-sky-900" type="button" onClick={onClear}>
          Сброс
        </button>
      )}
    </div>
    <div className="grid grid-cols-2 gap-2">
      <input className={inputClass} type={type} value={value.min || ''} onChange={(event) => onChange('min', event.target.value)} placeholder="от" />
      <input className={inputClass} type={type} value={value.max || ''} onChange={(event) => onChange('max', event.target.value)} placeholder="до" />
    </div>
  </div>
);
