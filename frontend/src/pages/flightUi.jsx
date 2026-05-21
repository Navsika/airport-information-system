import { STATUS_LABELS, statusTone } from './flightMeta';

export const StatusBadge = ({ status }) => (
  <span className={`inline-flex min-w-24 justify-center rounded-full px-3 py-1 text-xs font-semibold ring-1 ${statusTone[status] || statusTone.Scheduled}`}>
    {STATUS_LABELS[status] || status}
  </span>
);

export const Field = ({ label, children }) => (
  <label className="grid gap-1 text-xs font-semibold text-slate-500">
    {label}
    {children}
  </label>
);

export const ErrorNote = ({ message }) => message ? (
  <div className="rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{message}</div>
) : null;

export const Avatar = ({ name }) => {
  const initials = String(name || '')
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase();

  return (
    <div className="grid h-11 w-11 shrink-0 place-items-center rounded-full bg-sky-100 text-sm font-bold text-sky-800">
      {initials || 'С'}
    </div>
  );
};

const metricTone = {
  sky: ['border-sky-100', 'from-white to-sky-50', 'text-sky-700', 'bg-sky-500'],
  green: ['border-emerald-100', 'from-white to-emerald-50', 'text-emerald-700', 'bg-emerald-500'],
  amber: ['border-amber-100', 'from-white to-amber-50', 'text-amber-700', 'bg-amber-500'],
  rose: ['border-rose-100', 'from-white to-rose-50', 'text-rose-700', 'bg-rose-500'],
};

export const Metric = ({ label, value, progress, tone = 'sky', hint }) => {
  const [border, gradient, text, bar] = metricTone[tone] || metricTone.sky;

  return (
    <div className={`rounded-lg border ${border} bg-linear-to-br ${gradient} p-4 shadow-sm`}>
      <div className={`text-xs font-bold uppercase tracking-wide ${text}`}>{label}</div>
      <div className="mt-2 text-3xl font-semibold text-slate-950">{value}</div>
      {hint && <div className="mt-1 text-xs text-slate-500">{hint}</div>}
      {typeof progress === 'number' && (
        <div className="mt-3 h-2 rounded-full bg-slate-200">
          <div className={`h-2 rounded-full ${bar}`} style={{ width: `${Math.min(progress, 100)}%` }} />
        </div>
      )}
    </div>
  );
};

export const InfoTile = ({ label, value }) => (
  <div className="rounded-md bg-slate-50 px-3 py-3">
    <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</div>
    <div className="mt-1 text-sm font-semibold text-slate-900">{value}</div>
  </div>
);

export const Panel = ({ title, children }) => (
  <section className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
    <h3 className="mb-4 text-xs font-bold uppercase tracking-[0.18em] text-slate-500">{title}</h3>
    <div className="grid gap-3">{children}</div>
  </section>
);

export const Empty = ({ text }) => (
  <div className="rounded-md bg-slate-50 px-3 py-3 text-sm text-slate-500">{text}</div>
);
