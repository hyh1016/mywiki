import React from 'react';
import './Button.css';

interface ButtonProps {
    onClick: () => void;
    children: React.ReactNode;
    disabled?: boolean;
    className?: string;
}

const Button: React.FC<ButtonProps> = ({ onClick, children, disabled = false, className = '' }) => {
    return (
        <button 
            className={`common-btn ${className}`}
            onClick={onClick} 
            disabled={disabled}
        >
            {children}
        </button>
    );
};

export default Button;
