import React from 'react'
import './Navbar.css'

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-left">
          <h1 className="navbar-brand">LeetForces</h1>
        </div>
        <div className="navbar-right">
          <button className="navbar-settings">Settings</button>
        </div>
      </div>
    </nav>
  )
}

