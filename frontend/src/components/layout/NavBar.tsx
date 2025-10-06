import React from 'react';
import {useNavigate} from 'react-router-dom';
import logo from '../../assets/logo.png';
import './NavBar.css';

interface NavBarProps {
    title: string;
}

const NavBar: React.FC<NavBarProps> = ({ title }) => {
    const navigate = useNavigate();

    const handleLogoClick = () => {
        navigate('/');
    };

    return (
        <nav className="nav-bar">
            <img src={logo} alt="mywiki logo" className="nav-logo" onClick={handleLogoClick} />
            <h1 className="nav-title">{title}</h1>
        </nav>
    );
};

export default NavBar;
