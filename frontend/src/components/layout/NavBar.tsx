import React from 'react';
import {Link} from 'react-router-dom';
import logo from '../../assets/logo.png';
import './NavBar.css';

interface NavBarProps {
    title: string;
}

const NavBar: React.FC<NavBarProps> = ({ title }) => {
    return (
        <nav className="nav-bar">
            <Link to="/">
                <img src={logo} alt="mywiki logo" className="nav-logo" />
            </Link>
            <h1 className="nav-title">{title}</h1>
        </nav>
    );
};

export default NavBar;
