import { NavLink } from 'react-router-dom';

const links = [
  { to: '/home', label: 'Home' },
  { to: '/dashboard', label: 'Dashboard' },
];

export default function Sidebar() {
  return (
    <aside className="hidden w-72 shrink-0 flex-col gap-2 border-r border-slate-200 bg-white p-6 lg:flex">
      <div className="mb-8">
        <p className="text-sm uppercase tracking-[0.24em] text-sky-600">Navigation</p>
        <p className="mt-2 text-2xl font-semibold text-slate-900">Workspace</p>
      </div>
      <nav className="space-y-2">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `block rounded-2xl px-4 py-3 text-sm font-medium transition ${
                isActive ? 'bg-slate-900 text-white' : 'text-slate-700 hover:bg-slate-100'
              }`
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
      <div className="mt-auto rounded-3xl bg-sky-50 p-4 text-sm text-slate-700">
        Use the sidebar to navigate system pages. Your backend is connected via the API gateway.
      </div>
    </aside>
  );
}
