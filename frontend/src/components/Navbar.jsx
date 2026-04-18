import { useAuth } from '../hooks/useAuth';

export default function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="flex items-center justify-between border-b border-slate-200 bg-white px-6 py-4 shadow-sm">
      <div>
        <h1 className="text-lg font-semibold text-slate-900">EduCore Portal</h1>
        <p className="text-sm text-slate-500">Fast access to student analytics and profile tools.</p>
      </div>
      <div className="flex items-center gap-4">
        {user && (
          <div className="text-right">
            <p className="font-medium text-slate-900">{user.username}</p>
            <p className="text-sm text-slate-500">{user.email}</p>
          </div>
        )}
        <button
          onClick={logout}
          className="rounded-md bg-slate-900 px-4 py-2 text-white transition hover:bg-slate-700"
        >
          Sign out
        </button>
      </div>
    </header>
  );
}
