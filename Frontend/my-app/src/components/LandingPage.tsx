import heroImg from '../assets/hero.png'

type LandingPageProps = {
  userName: string
  onLogout: () => void
}

const highlights = [
  {
    title: 'Resume your flow',
    description: 'Jump into your latest tasks, streaks, and saved notes without friction.',
  },
  {
    title: 'Prioritize quickly',
    description: 'A landing page layout that keeps goals, reminders, and next steps visible.',
  },
  {
    title: 'Stay consistent',
    description: 'Use the dashboard to keep your progress visible and your routine simple.',
  },
]

export function LandingPage({ userName, onLogout }: LandingPageProps) {
  return (
    <main className="landing-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Leetforce</p>
          <h1>Welcome back, {userName}.</h1>
        </div>

        <button className="secondary-button" onClick={onLogout}>
          Log out
        </button>
      </header>

      <section className="landing-hero">
        <div className="landing-copy">
          <p className="eyebrow">Landing page</p>
          <h2>Your next session starts here.</h2>
          <p>
            This landing page gives you a focused space for progress, shortcuts, and
            quick updates after login.
          </p>

          <div className="landing-actions">
            <a className="primary-button" href="#highlights">
              Explore highlights
            </a>
            <a className="secondary-button secondary-button--link" href="#overview">
              View overview
            </a>
          </div>

          <div className="stats" id="overview">
            <article>
              <strong>12</strong>
              <span>tasks queued</span>
            </article>
            <article>
              <strong>7</strong>
              <span>day streak</span>
            </article>
            <article>
              <strong>94%</strong>
              <span>goal progress</span>
            </article>
          </div>
        </div>

        <div className="landing-visual">
          <img src={heroImg} alt="Workspace illustration" />
        </div>
      </section>

      <section className="highlights" id="highlights">
        {highlights.map((item) => (
          <article key={item.title} className="highlight-card">
            <p className="card-index">{item.title}</p>
            <p>{item.description}</p>
          </article>
        ))}
      </section>
    </main>
  )
}

