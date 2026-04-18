import { Outlet } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';

export default function MainLayout() {
  return (
    <div className="min-h-screen bg-slate-50">
      <div className="lg:flex lg:min-h-screen">
        <Sidebar />
        <div className="flex-1">
          <Navbar />
          <main className="px-6 py-8 lg:px-10">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
