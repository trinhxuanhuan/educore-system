export default function LoadingSpinner() {
  return (
    <div className="flex items-center justify-center py-24 text-slate-500">
      <div className="h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-sky-500" />
    </div>
  );
}
