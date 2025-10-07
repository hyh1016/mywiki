import React from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import MainPage from "./pages/MainPage";
import {AuthProvider} from "./contexts/AuthContext";
import PrivateRoute from "./components/common/PrivateRoute";

import AddBookmarkPage from "./pages/AddBookmarkPage";
import BookmarkListPage from "./pages/BookmarkListPage";
import BookmarkDetailPage from "./pages/BookmarkDetailPage";
import AddSummaryPage from "./pages/AddSummaryPage";

function App() {
  return (
      <Router>
          <AuthProvider>
              <Routes>
                  <Route path="/" element={<PrivateRoute><MainPage /></PrivateRoute>} />
                  <Route path="/add-bookmark" element={<PrivateRoute><AddBookmarkPage /></PrivateRoute>} />
                  <Route path="/bookmarks" element={<PrivateRoute><BookmarkListPage /></PrivateRoute>} />
                  <Route path="/bookmarks/:id" element={<PrivateRoute><BookmarkDetailPage /></PrivateRoute>} />
                  <Route path="/summaries/new" element={<PrivateRoute><AddSummaryPage /></PrivateRoute>} />
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="*" element={<Navigate to="/" />} />
              </Routes>
          </AuthProvider>
      </Router>
  );
}

export default App;