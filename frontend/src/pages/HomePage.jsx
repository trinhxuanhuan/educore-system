import { useAuth } from '../hooks/useAuth';

export default function HomePage() {
  const { user } = useAuth();

  return (
    <section className="space-y-8">
      <div className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.24em] text-sky-600">Welcome back</p>
            <h2 className="mt-3 text-3xl font-semibold text-slate-900">EduCore dashboard</h2>
          </div>
          <div className="rounded-3xl bg-slate-50 px-4 py-3 text-slate-700">
            Logged in as <span className="font-semibold text-slate-900">{user?.username}</span>
          </div>
        </div>

        <p className="mt-6 max-w-2xl text-slate-600">
          This portal connects to your backend services through the API gateway. Use the dashboard page for analytics, and your profile is kept synchronized with the server.
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        <article className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Profile</p>
          <h3 className="mt-4 text-xl font-semibold text-slate-900">User information</h3>
          <p className="mt-3 text-slate-600">Your account data comes from /api/v1/users/me.</p>
        </article>

        <article className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Analytics</p>
          <h3 className="mt-4 text-xl font-semibold text-slate-900">Student analytics</h3>
          <p className="mt-3 text-slate-600">The dashboard queries /api/v1/analytics/me for your current performance snapshot.</p>
        </article>

        <article className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Grades</p>
          <h3 className="mt-4 text-xl font-semibold text-slate-900">Score reports</h3>
          <p className="mt-3 text-slate-600">Grade and subject services are available through the backend gateway and protected with JWT.</p>
        </article>
      </div>
    </section>
  );
}
