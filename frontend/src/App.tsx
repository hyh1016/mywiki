import React from 'react';
import {createBrowserRouter, Outlet, RouterProvider} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import MainPage from "./pages/MainPage";
import {AuthProvider} from "./contexts/AuthContext";
import PrivateRoute from "./components/common/PrivateRoute";
import AddBookmarkPage from "./pages/AddBookmarkPage";
import BookmarkListPage from "./pages/BookmarkListPage";
import BookmarkDetailPage from "./pages/BookmarkDetailPage";
import SummaryDetailPage from "./pages/SummaryDetailPage";
import SummaryListPage from "./pages/SummaryListPage";
import SummaryFormPage from "./pages/SummaryFormPage";

const AppLayout = () => (
    <AuthProvider>
        <Outlet />
    </AuthProvider>
);

const router = createBrowserRouter([
    {
        element: <AppLayout />,
        children: [
            { path: "/", element: <PrivateRoute><MainPage /></PrivateRoute> },
            { path: "/add-bookmark", element: <PrivateRoute><AddBookmarkPage /></PrivateRoute> },
            { path: "/bookmarks", element: <PrivateRoute><BookmarkListPage /></PrivateRoute> },
            { path: "/bookmarks/:id", element: <PrivateRoute><BookmarkDetailPage /></PrivateRoute> },
            { path: "/summaries", element: <PrivateRoute><SummaryListPage /></PrivateRoute> },
            { path: "/summaries/new", element: <PrivateRoute><SummaryFormPage /></PrivateRoute> },
            { path: "/summaries/:id", element: <PrivateRoute><SummaryDetailPage /></PrivateRoute> },
            { path: "/summaries/:id/edit", element: <PrivateRoute><SummaryFormPage /></PrivateRoute> },
            { path: "/login", element: <LoginPage /> },
        ]
    }
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;