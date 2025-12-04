import React from 'react';
import './HealthBar.css';

/**
 * HealthBar - Displays the player's current health as a colored bar
 * Changes color based on health level (green → orange → red)
 */
const HealthBar = ({ health = 100, maxHealth = 100 }) => {

  // ===== CALCULATE HEALTH PERCENTAGE =====
  // Clamps value between 0 and 100 to prevent overflow
  const percentage = Math.max(0, Math.min((health / maxHealth) * 100, 100));
  
  // ===== COLOR BASED ON HEALTH LEVEL =====
  // Green = healthy, Orange = caution, Red = danger
  const getHealthColor = () => {
    if (percentage > 60) return '#4caf50'; // Green - above 60%
    if (percentage > 30) return '#ff9800'; // Orange - between 30-60%
    return '#f44336';                       // Red - below 30%
  };

  // ===== RENDER THE HEALTH BAR =====
  return (
    <div className="health-bar-container">

      {/* Label row: "HP" on left, "current/max" on right */}
      <div className="health-bar-label">
        <span>HP</span>
        <span>{health}/{maxHealth}</span>
      </div>

      {/* The actual bar - gray background with colored fill */}
      <div className="health-bar-background">
        <div 
          className="health-bar-fill"
          style={{ 
            width: `${percentage}%`,        // Fill width = health %
            backgroundColor: getHealthColor() // Color changes with health
          }}
        />
      </div>
    </div>
  );
};

export default HealthBar;
