import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { FlightsDashboard } from './pages/FlightsDashboard';
import { PassengerOperationsPage } from './pages/PassengerOperationsPage';
import { ReferencesPage } from './pages/ReferencesPage';
import { Layout } from './components/Layout';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<FlightsDashboard />} />
            <Route path="operations" element={<PassengerOperationsPage />} />
            <Route path=":referenceKey" element={<ReferencesPage />} />
          </Route>
        </Routes>
      </Router>
    </QueryClientProvider>
  );
}

export default App;
