import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useParams, useSearchParams } from 'react-router-dom';
import { airline } from '../services/airline';
import { aircraftModel } from '../services/aircraftModel';
import { REFERENCES } from './referencesConfig';
import { FacetFilter, ReferenceField, ghostButton, primaryButton } from './referenceControls';
import { fetchReferencePage, getCellValue, hasActiveFilters, normalizePayload} from './referenceUtils';

export const ReferencesPage = () => {
  const { referenceKey = 'models' } = useParams();
  const [searchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const active = REFERENCES[referenceKey] || REFERENCES.models;
  const selectedEmployeeId = referenceKey === 'employees' ? searchParams.get('employeeId') : null;
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [isCreateOpen, setCreateOpen] = useState(false);
  const [draft, setDraft] = useState({});

  useEffect(() => {
    setFilters({});
    setPage(0);
  }, [active]);

  const modelsQuery = useQuery({
    queryKey: ['reference-lookup', 'models'],
    queryFn: aircraftModel.getList,
    staleTime: 300000,
    enabled: active.lookups?.includes('models'),
  });

  const airlinesQuery = useQuery({
    queryKey: ['reference-lookup', 'airlines'],
    queryFn: airline.getList,
    staleTime: 300000,
    enabled: active.lookups?.includes('airlines'),
  });

  const referenceOptionsQuery = useQuery({
    queryKey: ['reference-options', referenceKey],
    queryFn: active.service.getList,
    staleTime: 300000,
    enabled: (
      (active.facets || []).some((facet) => facet.type === 'value-select') ||
      (active.fields || []).some(([, , type]) => type === 'value-select')
    ) && Boolean(active.service.getList),
  });

  const query = useQuery({
    queryKey: ['reference', referenceKey, filters, page, selectedEmployeeId],
    queryFn: () => fetchReferencePage(active, filters, page, selectedEmployeeId),
  });

  const createMutation = useMutation({
    mutationFn: () => active.service.create(normalizePayload(draft, active.fields)),
    onSuccess: () => {
      setDraft({});
      setCreateOpen(false);
      queryClient.invalidateQueries({ queryKey: ['reference', referenceKey] });
    },
  });

  const lookupData = useMemo(() => ({
    models: modelsQuery.data || [],
    airlines: airlinesQuery.data || [],
    current: referenceOptionsQuery.data || [],
  }), [airlinesQuery.data, modelsQuery.data, referenceOptionsQuery.data]);

  const rows = useMemo(() => {
    if (Array.isArray(query.data)) {
      return query.data;
    }
    return query.data?.content || [];
  }, [query.data]);
  const totalPages = Array.isArray(query.data) ? 1 : query.data?.totalPages || 1;

  const setDraftField = (field, value) => setDraft((current) => ({ ...current, [field]: value }));
  const setFilter = (field, value) => {
    setFilters((current) => ({ ...current, [field]: value }));
    setPage(0);
  };
  const clearFilter = (field) => setFilters((current) => {
    const next = { ...current };
    delete next[field];
    setPage(0);
    return next;
  });
  const clearFilters = () => {
    setFilters({});
    setPage(0);
  };

  return (
    <div className="min-h-[calc(100vh-56px)] bg-[#eef6fb]">
      <section className="border-b border-sky-100 bg-white">
        <div className="mx-auto flex max-w-7xl flex-wrap items-end justify-between gap-4 px-6 py-7">
          <h1 className="text-4xl font-semibold tracking-tight text-slate-950">{active.title}</h1>
          <button className={primaryButton} onClick={() => setCreateOpen((value) => !value)}>
            {isCreateOpen ? 'Свернуть форму' : 'Пополнить справочник'}
          </button>
        </div>
      </section>

      <section className="mx-auto grid max-w-7xl gap-5 px-6 py-6">
        {isCreateOpen && (
          <ReferenceCreateModal
            active={active}
            draft={draft}
            lookupData={lookupData}
            error={createMutation.error}
            isPending={createMutation.isPending}
            onChange={setDraftField}
            onClose={() => { setDraft({}); setCreateOpen(false); }}
            onSubmit={() => createMutation.mutate()}
          />
        )}

        <div className="grid gap-5 lg:grid-cols-[17rem_minmax(0,1fr)]">
          <ReferenceFilters
            active={active}
            filters={filters}
            lookupData={lookupData}
            onChange={setFilter}
            onClear={clearFilter}
            onClearAll={clearFilters}
          />
          <ReferenceTable active={active} rows={rows} lookupData={lookupData} isLoading={query.isLoading} />
        </div>

        <div className="flex items-center justify-between text-sm text-slate-600">
          <span>Страница {page + 1} из {totalPages}</span>
          <div className="flex gap-2">
            <button className={ghostButton} disabled={page === 0} onClick={() => setPage((value) => Math.max(value - 1, 0))}>Назад</button>
            <button className={ghostButton} disabled={page + 1 >= totalPages} onClick={() => setPage((value) => value + 1)}>Вперед</button>
          </div>
        </div>
      </section>
    </div>
  );
};

const ReferenceCreateModal = ({ active, draft, lookupData, error, isPending, onChange, onClose, onSubmit }) => (
  <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/55 p-4">
    <div className="w-full max-w-3xl rounded-lg bg-white shadow-2xl">
      <div className="flex items-start justify-between gap-4 border-b border-slate-200 px-6 py-5">
        <div>
          <h2 className="text-2xl font-semibold text-slate-950">Новая запись</h2>
          <p className="mt-1 text-sm text-slate-500">{active.title}</p>
        </div>
        <button className={ghostButton} onClick={onClose}>Закрыть</button>
      </div>
      <div className="grid gap-5 px-6 py-5">
        <div className="grid gap-3 md:grid-cols-2">
          {active.fields.map(([field, label, type, options, placeholder]) => (
            <ReferenceField
              key={field}
              field={field}
              label={label}
              type={type}
              options={options}
              placeholder={placeholder}
              value={draft[field] || ''}
              lookupData={lookupData}
              onChange={(value) => onChange(field, value)}
            />
          ))}
        </div>
        {error && <div className="rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{error.message}</div>}
        <div className="flex justify-end gap-2">
          <button className={ghostButton} onClick={onClose}>Отмена</button>
          <button className={primaryButton} disabled={isPending} onClick={onSubmit}>
            Сохранить запись
          </button>
        </div>
      </div>
    </div>
  </div>
);

const ReferenceFilters = ({ active, filters, lookupData, onChange, onClear, onClearAll }) => (
  <aside className="h-fit rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
    <div className="mb-3 flex items-center justify-between gap-3">
      <div className="text-xs font-bold uppercase tracking-wide text-slate-500">Фильтры</div>
      {hasActiveFilters(filters) && (
        <button className="text-xs font-semibold text-sky-700 hover:text-sky-900" onClick={onClearAll}>
          Сбросить
        </button>
      )}
    </div>
    <div className="grid gap-3">
      {(active.facets || []).map((facet) => (
        <FacetFilter
          key={facet.field}
          facet={facet}
          value={filters[facet.field]}
          lookupData={lookupData}
          onChange={(value) => onChange(facet.field, value)}
          onClear={() => onClear(facet.field)}
        />
      ))}
    </div>
  </aside>
);

const ReferenceTable = ({ active, rows, lookupData, isLoading }) => (
  <div className="h-fit self-start overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
    <table className="min-w-full divide-y divide-slate-200">
      <thead className="bg-sky-100 text-left text-xs uppercase tracking-wide text-sky-950">
        <tr>
          {active.columns.map(([, title]) => <th key={title} className="px-5 py-4">{title}</th>)}
        </tr>
      </thead>
      <tbody className="divide-y divide-slate-100">
        {rows.map((row, index) => (
          <tr key={row[active.id] || index} className="hover:bg-sky-50/70">
            {active.columns.map(([field]) => <td key={field} className="px-5 py-3 text-sm text-slate-700">{getCellValue(row, field, lookupData) || '-'}</td>)}
          </tr>
        ))}
        {!rows.length && (
          <tr>
            <td className="px-5 py-12 text-center text-sm text-slate-500" colSpan={active.columns.length}>
              {isLoading ? 'Загрузка справочника...' : 'Записи не найдены'}
            </td>
          </tr>
        )}
      </tbody>
    </table>
  </div>
);
