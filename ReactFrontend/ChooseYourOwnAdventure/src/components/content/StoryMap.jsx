import React, { useState, useEffect } from 'react';
import './StoryMap.css';

/**
 * StoryMap - Shows a visual map of all scenes in the game
 * Highlights where the player currently is in the story
 */
const StoryMap = ({ currentScene }) => {

  // ===== STATE =====
  // nodes = all the scenes in the game (boxes on the map)
  // edges = connections between scenes (arrows on the map)
  // loading = true while we're fetching data from the backend
  const [nodes, setNodes] = useState([]);
  const [edges, setEdges] = useState([]);
  const [loading, setLoading] = useState(true);

  // ===== FETCH DATA ON LOAD =====
  // Runs once when component mounts - gets the map structure from backend
  useEffect(() => {
    fetch('/api/game/story-map')
      .then(res => res.json())
      .then(data => {

        // Position each node in a grid layout (2 columns)
        // x = horizontal position, y = vertical position
        const layoutNodes = data.nodes.map((node, index) => ({
          ...node,
          x: 60 + (index % 2) * 120,      // Alternates between 2 columns
          y: 40 + Math.floor(index / 2) * 80, // New row every 2 nodes
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

  // ===== HELPER FUNCTIONS =====

  // Finds the x,y position of a node by its ID
  const getNodePosition = (nodeId) => {
    const node = nodes.find(n => n.id === nodeId);
    return node ? { x: node.x, y: node.y } : { x: 0, y: 0 };
  };

  // Gets start and end points for drawing an arrow between two nodes
  const getArrowPath = (from, to) => {
    const fromPos = getNodePosition(from);
    const toPos = getNodePosition(to);
    
    return {
      x1: fromPos.x,  // Start x
      y1: fromPos.y,  // Start y
      x2: toPos.x,    // End x
      y2: toPos.y     // End y
    };
  };

  // ===== LOADING STATE =====
  if (loading) {
    return (
      <div className="story-map-container-compact">
        <div className="loading-compact">Loading map...</div>
      </div>
    );
  }

  // Get the current scene code to highlight it on the map
  const currentSceneCode = currentScene?.code;

  // ===== RENDER THE MAP =====
  return (
    <div className="story-map-container-compact">
      <h3 className="story-map-title-compact">🗺️ Story Map</h3>
      
      {/* SVG canvas where we draw the map */}
      <svg width="280" height="400" className="story-map-svg-compact">

        {/* ----- ARROWS (drawn first so they appear behind nodes) ----- */}
        {edges.map((edge, index) => {
          const arrow = getArrowPath(edge.from, edge.to);
          
          // Check if this arrow has special meaning
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
            </g>
          );
        })}
        
        {/* ----- ARROWHEAD SHAPE DEFINITION ----- */}
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
        
        {/* ----- SCENE BOXES (nodes) ----- */}
        {nodes.map(node => {
          const isCurrentNode = node.id === currentSceneCode;
          
          return (
            <g key={node.id} className="node-group">
              
              {/* The box itself */}
              <rect
                x={node.x - 50}
                y={node.y - 20}
                width="100"
                height="40"
                className={`node-compact ${node.isTerminal ? 'node-terminal' : 'node-regular'} ${isCurrentNode ? 'node-current' : ''}`}
                rx="4"
              />
              
              {/* Pulsing circle around current scene */}
              {isCurrentNode && (
                <circle
                  cx={node.x}
                  cy={node.y}
                  r="55"
                  className="node-pulse"
                />
              )}
              
              {/* Scene name label */}
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
