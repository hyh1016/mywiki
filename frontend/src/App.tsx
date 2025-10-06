import React from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import MainPage from "./pages/MainPage";
import {AuthProvider} from "./contexts/AuthContext";
import PrivateRoute from "./components/common/PrivateRoute";

function App() {
  return (
      <Router>
          <AuthProvider>
              <Routes>
                  <Route path="/" element={<PrivateRoute><MainPage /></PrivateRoute>} />
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="*" element={<Navigate to="/" />} />
              </Routes>
          </AuthProvider>
      </Router>
  );
}

export default App;