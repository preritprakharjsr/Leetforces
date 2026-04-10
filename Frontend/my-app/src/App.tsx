import { useState } from 'react'
import { LandingPage } from './components/LandingPage'
import { LoginPage } from './components/LoginPage'
import './App.css'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [displayName, setDisplayName] = useState('Aisha')

  function handleLogin(nextDisplayName: string) {
    setDisplayName(nextDisplayName)
    setIsLoggedIn(true)
  }

  function handleLogout() {
    setIsLoggedIn(false)
  }

  return isLoggedIn ? (
    <LandingPage userName={displayName} onLogout={handleLogout} />
  ) : (
    <LoginPage onLogin={handleLogin} />
  )
}

export default App
