import { FormEvent, useMemo, useState } from 'react'
import heroImg from '../assets/hero.png'

type LoginPageProps = {
  onLogin: (displayName: string) => void
}

function buildDisplayName(email: string) {
  const localPart = email.split('@')[0]?.trim() || 'builder'
  return localPart
    .split(/[._-]/)
    .filter(Boolean)
    .map((word) => word[0].toUpperCase() + word.slice(1))
    .join(' ')
}

export function LoginPage({ onLogin }: LoginPageProps) {
  const [email, setEmail] = useState('priya@leetforce.dev')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(true)

  const canSubmit = useMemo(() => email.trim().length > 0 && password.trim().length > 0, [
    email,
    password,
  ])

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!canSubmit) {
      return
    }

    onLogin(buildDisplayName(email))
  }

  return (
    <main className="auth-shell">
      <section className="auth-intro" aria-labelledby="auth-intro-title">
        <p className="eyebrow">Leetforce</p>
        <h1 id="auth-intro-title">Build momentum with a cleaner login flow.</h1>
        <p className="auth-text">
          Welcome back. Sign in to unlock your personalized landing page, track your
          progress, and jump straight into your daily workflow.
        </p>

        <div className="auth-benefits">
          <article>
            <span>01</span>
            <h2>Focus</h2>
            <p>Keep your most important tasks and goals in one place.</p>
          </article>
          <article>
            <span>02</span>
            <h2>Momentum</h2>
            <p>Pick up where you left off with a quick, friction-free login.</p>
          </article>
          <article>
            <span>03</span>
            <h2>Clarity</h2>
            <p>See progress at a glance with a landing page built for action.</p>
          </article>
        </div>

        <div className="auth-visual">
          <img src={heroImg} alt="Dashboard preview" />
        </div>
      </section>

      <section className="auth-card" aria-labelledby="login-title">
        <div className="card-header">
          <p className="eyebrow">Login</p>
          <h2 id="login-title">Access your workspace</h2>
          <p>Use any email and password combination to continue into the demo landing page.</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          <label>
            <span>Email</span>
            <input
              type="email"
              name="email"
              placeholder="you@example.com"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              autoComplete="email"
            />
          </label>

          <label>
            <span>Password</span>
            <input
              type="password"
              name="password"
              placeholder="Enter your password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="current-password"
            />
          </label>

          <div className="form-row">
            <label className="checkbox">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(event) => setRememberMe(event.target.checked)}
              />
              <span>Remember me</span>
            </label>

            <button type="button" className="text-button">
              Forgot password?
            </button>
          </div>

          <button className="primary-button" type="submit" disabled={!canSubmit}>
            Log in
          </button>

          <p className="form-note">
            Tip: this demo uses local state only, so no backend setup is required.
          </p>
        </form>
      </section>
    </main>
  )
}

