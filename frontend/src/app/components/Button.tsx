import React from 'react';

type ButtonProps = {
    label: string;
    onClick: () => void;
    type?: 'button' | 'submit' | 'reset';
};

const Button: React.FC<ButtonProps> = ({ label, onClick, type = 'button' }) => {
    return (
        <button
            type={type}
            onClick={onClick}
            className="bg-blue-500 text-white font-semibold py-2 px-4 rounded hover:bg-blue-600"
        >
            {label}
        </button>
    );
};

export default Button;
