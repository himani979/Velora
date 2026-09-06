import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// ==============================
// LOAD SAVED VELORA THEME
// ==============================

const savedTheme =
    localStorage.getItem("velora_theme") || "light";

let appliedTheme = savedTheme;

if (savedTheme === "system") {

    appliedTheme = window.matchMedia(
        "(prefers-color-scheme: dark)"
    ).matches
        ? "dark"
        : "light";
}

document.documentElement.setAttribute(
    "data-theme",
    appliedTheme
);
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
