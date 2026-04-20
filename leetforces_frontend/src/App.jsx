import React from 'react'
import Navbar from './Navbar'
import ProfileLinksPanel from './ProfileLinksPanel'

export default function App() {
  return (
    <div>
      <Navbar />
      <div className="app-layout">
        <ProfileLinksPanel profileName="User Profile" />
        <main className="main-content">
          <h2>Welcome to LeetForces</h2>
          <p>Your coding challenges platform</p>
        </main>
      </div>
    </div>
  )
}

