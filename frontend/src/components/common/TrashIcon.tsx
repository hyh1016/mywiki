import React from 'react';

interface TrashIconProps {
    className?: string;
}

const TrashIcon: React.FC<TrashIconProps> = ({className}) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20" className={className}>
        <path fillRule="evenodd"
              d="M16.5 4.478v.227a48.816 48.816 0 0 1 3.878.512.75.75 0 1 1-.231 1.479 47.376 47.376 0 0 0-3.686-.468l.971 10.81c.07.863-.603 1.62-1.465 1.62H6.227c-.862 0-1.535-.757-1.465-1.62L5.8 6.734A47.376 47.376 0 0 0 2.112 6.22a.75.75 0 0 1-.231-1.479 48.816 48.816 0 0 0 3.878-.512V4.478c0-1.1.9-2 2-2h6.5c1.1 0 2 .9 2 2ZM12.75 9.25a.75.75 0 0 0-1.5 0v7.5a.75.75 0 0 0 1.5 0v-7.5ZM9.75 9.25a.75.75 0 0 0-1.5 0v7.5a.75.75 0 0 0 1.5 0v-7.5ZM19.5 6.75a.75.75 0 0 0-1.5 0V18a2.25 2.25 0 0 1-2.25 2.25H6.75A2.25 2.25 0 0 1 4.5 18V6.75a.75.75 0 0 0-1.5 0V18a3.75 3.75 0 0 0 3.75 3.75h10.5A3.75 3.75 0 0 0 21 18V6.75h-.001Z"
              clipRule="evenodd"/>
    </svg>
);

export default TrashIcon;