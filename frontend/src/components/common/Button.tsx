import React from 'react';
import './Button.css';

interface ButtonProps {
    onClick?: () => void;
    children: React.ReactNode;
    disabled?: boolean;
    className?: string;
    type?: 'button' | 'submit' | 'reset';
}

const Button: React.FC<ButtonProps> = ({ onClick, children, disabled = false, className = '', type = 'button' }) => {
    return (
        <button 
            className={`common-btn ${className}`}
            onClick={onClick} 
            disabled={disabled}
            type={type}
        >
            {children}
        </button>
    );
};

export default Button;
