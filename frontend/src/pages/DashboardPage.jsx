import { useEffect, useState } from 'react';
import { analyticsService } from '../services/analyticsService';
import { studentService } from '../services/studentService';
import { useAuth } from '../hooks/useAuth';
import LoadingSpinner from '../components/LoadingSpinner';

export default function DashboardPage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    setError('');

    Promise.all([studentService.getMyProfile(), analyticsService.getMyAnalytics()])
      .then(([profileResponse, analyticsResponse]) => {
        setProfile(profileResponse.data);
        setAnalytics(analyticsResponse.data);
      })
      .catch((err) => {
        setError('Unable to load dashboard data. Please verify your login and backend status.');
        console.error(err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [user]);

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <section className="space-y-8">
      <div className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.24em] text-sky-600">Dashboard</p>
            <h2 className="mt-3 text-3xl font-semibold text-slate-900">Live data from the API gateway</h2>
          </div>
          <div className="rounded-3xl bg-slate-50 px-4 py-3 text-slate-700">{user?.roles?.join(' • ')}</div>
        </div>
      </div>

      {error && <p className="rounded-3xl bg-red-50 p-4 text-sm text-red-700">{error}</p>}

      <div className="grid gap-6 xl:grid-cols-2">
        <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-xl font-semibold text-slate-900">Profile summary</h3>
          <div className="mt-6 space-y-4 text-slate-700">
            <div className="rounded-2xl bg-slate-50 p-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Name</p>
              <p className="mt-2 text-lg font-medium text-slate-900">{profile?.username}</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Email</p>
              <p className="mt-2 text-lg font-medium text-slate-900">{profile?.email}</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Roles</p>
              <p className="mt-2 text-lg font-medium text-slate-900">{user?.roles?.join(', ')}</p>
            </div>
          </div>
        </div>

        <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-xl font-semibold text-slate-900">Analytics snapshot</h3>
          <div className="mt-6 space-y-4 text-slate-700">
            <div className="rounded-2xl bg-slate-50 p-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Performance</p>
              <p className="mt-2 text-lg font-medium text-slate-900">{analytics?.performance || 'Unavailable'}</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">GPA</p>
              <p className="mt-2 text-lg font-medium text-slate-900">{analytics?.gpa ?? 'Not available'}</p>
            </div>
            <div className="rounded-2xl bg-slate-50 p-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Growth</p>
              <p className="mt-2 text-lg font-medium text-slate-900">{analytics?.growth ?? 'Not available'}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
