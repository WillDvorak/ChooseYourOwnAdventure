import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import AppLayout from './components/structural/AppLayout.jsx'


createRoot(document.getElementById('root')).render(
    <AppLayout />
    // To use the test skeleton instead:
    // <SimpleTest_test />
)
