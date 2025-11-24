import React from 'react';
import './HealthBar.css';


/**
 * 
 * @param {*} param0 
 * @returns 
 */
const HealthBar = ({ health = 100, maxHealth = 100 }) => {
  const percentage = Math.max(0, Math.min((health / maxHealth) * 100, 100));
  
  const getHealthColor = () => {
    if (percentage > 60) return '#4caf50'; // Green
    if (percentage > 30) return '#ff9800'; // Orange
    return '#f44336'; // Red
  };

  return (
    <div className="health-bar-container">
      <div className="health-bar-label">
        <span>HP</span>
        <span>{health}/{maxHealth}</span>
      </div>
      <div className="health-bar-background">
        <div 
          className="health-bar-fill"
          style={{ 
            width: `${percentage}%`,
            backgroundColor: getHealthColor()
          }}
        />
      </div>
    </div>
  );
};

export default HealthBar;

