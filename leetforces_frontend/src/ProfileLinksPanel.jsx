import React from 'react'
import './ProfileLinksPanel.css'

export default function ProfileLinksPanel({ profileName }) {
  return (
    <aside className="profile-panel">
      <div className="profile-card">
        <h3 className="profile-name">{profileName}</h3>

        <label className="profile-label" htmlFor="leetcode-link">
          LeetCode link
        </label>
        <input
          id="leetcode-link"
          className="profile-input"
          type="url"
          placeholder="https://leetcode.com/u/yourname"
        />

        <label className="profile-label" htmlFor="codeforces-link">
          Codeforces link
        </label>
        <input
          id="codeforces-link"
          className="profile-input"
          type="url"
          placeholder="https://codeforces.com/profile/yourname"
        />
      </div>
    </aside>
  )
}

