import React from 'react';
import Footer from './Footer';
import NavBar from './NavBar';
import './Layout.css';

interface LayoutProps {
    children: React.ReactNode;
    title?: string;
}

const Layout: React.FC<LayoutProps> = ({ children, title }) => {
    return (
        <div className="layout-container">
            {title && <NavBar title={title} />}
            <main className="main-content">
                {children}
            </main>
            <Footer />
        </div>
    );
};

export default Layout;
