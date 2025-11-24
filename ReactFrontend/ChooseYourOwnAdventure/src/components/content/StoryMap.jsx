import React, { useState, useEffect } from 'react';
import './StoryMap.css';

/**
 * 
 * @param {*} param0 
 * @returns a story map component with a compact layout for sidebar use
 */
const StoryMap = ({ currentScene }) => {
  const [nodes, setNodes] = useState([]);
  const [edges, setEdges] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/game/story-map')
      .then(res => res.json())
      .then(data => {
        // Compact layout for sidebar: 2 columns, smaller spacing
        const layoutNodes = data.nodes.map((node, index) => ({
          ...node,
          x: 60 + (index % 2) * 120, // 2 columns, compact
          y: 40 + Math.floor(index / 2) * 80, // Tighter row height
        }));
        
        setNodes(layoutNodes);
        setEdges(data.edges);
        setLoading(false);
      })
      .catch(err => {
        console.error('Error loading story map:', err);
        setLoading(false);
      });
  }, []);

  // Helper to find node position by id
  const getNodePosition = (nodeId) => {
    const node = nodes.find(n => n.id === nodeId);
    return node ? { x: node.x, y: node.y } : { x: 0, y: 0 };
  };

  // Calculate arrow path
  const getArrowPath = (from, to) => {
    const fromPos = getNodePosition(from);
    const toPos = getNodePosition(to);
    
    // Simple straight line with arrow
    return {
      x1: fromPos.x,
      y1: fromPos.y,
      x2: toPos.x,
      y2: toPos.y
    };
  };

  if (loading) {
    return (
      <div className="story-map-container-compact">
        <div className="loading-compact">Loading map...</div>
      </div>
    );
  }

  const currentSceneCode = currentScene?.code;

  return (
    <div className="story-map-container-compact">
      <h3 className="story-map-title-compact">🗺️ Story Map</h3>
      
      <svg width="280" height="400" className="story-map-svg-compact">
        {/* Draw edges first (so they appear behind nodes) */}
        {edges.map((edge, index) => {
          const arrow = getArrowPath(edge.from, edge.to);
          const isHealthEdge = edge.setsFlag && edge.setsFlag.includes('health');
          const isConditional = edge.requiresFlag && edge.requiresFlag !== '';
          
          return (
            <g key={`edge-${index}`}>
              <line
                x1={arrow.x1}
                y1={arrow.y1}
                x2={arrow.x2}
                y2={arrow.y2}
                className={`edge ${isHealthEdge ? 'edge-health' : ''} ${isConditional ? 'edge-conditional' : ''}`}
                markerEnd="url(#arrowhead)"
              />
              {/* Edge label */}
              {/* Skip edge labels in compact mode for clarity */}
            </g>
          );
        })}
        
        {/* Arrow marker definition */}
        <defs>
          <marker
            id="arrowhead"
            markerWidth="10"
            markerHeight="10"
            refX="9"
            refY="3"
            orient="auto"
          >
            <polygon points="0 0, 10 3, 0 6" fill="#d4af37" />
          </marker>
        </defs>
        
        {/* Draw nodes */}
        {nodes.map(node => {
          const isCurrentNode = node.id === currentSceneCode;
          return (
            <g key={node.id} className="node-group">
              <rect
                x={node.x - 50}
                y={node.y - 20}
                width="100"
                height="40"
                className={`node-compact ${node.isTerminal ? 'node-terminal' : 'node-regular'} ${isCurrentNode ? 'node-current' : ''}`}
                rx="4"
              />
              {isCurrentNode && (
                <circle
                  cx={node.x}
                  cy={node.y}
                  r="55"
                  className="node-pulse"
                />
              )}
              <text
                x={node.x}
                y={node.y + 5}
                className="node-label-compact"
                textAnchor="middle"
              >
                {node.id} {node.isTerminal ? '🏁' : ''} {isCurrentNode ? '📍' : ''}
              </text>
            </g>
          );
        })}
      </svg>
    </div>
  );
};

export default StoryMap;

