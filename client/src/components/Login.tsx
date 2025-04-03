import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';

const Login: React.FC = () => {
  // State for managing form data and authentication status
  const [userType, setUserType] = useState('consumer'); // "consumer" or "company"
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loggedIn, setLoggedIn] = useState(false);
  const [error, setError] = useState('');

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Mock authentication - accept any credentials for demo purposes
    // In a real app, this would connect to the backend
    if (email && password) {
      // Store user info in localStorage for persistence
      localStorage.setItem('userType', userType);
      localStorage.setItem('userEmail', email);
      localStorage.setItem('isLoggedIn', 'true');
      
      setLoggedIn(true);
    } else {
      setError('Please enter both email and password');
    }
  };

  // Redirect after login based on user type
  if (loggedIn) {
    return <Redirect to={userType === 'consumer' ? "/buy/dashboard" : "/sell/dashboard"} />;
  }

  return (
    <div className="login-container container mt-5">
      <div className="columns is-centered">
        <div className="column is-6">
          <form onSubmit={handleSubmit} className="box">
            <h2 className="title has-text-centered">Login to knowMoreQR</h2>
            <div className="field">
              <label className="label">User Type</label>
              <div className="control">
                <label className="radio">
                  <input 
                    type="radio" 
                    name="userType" 
                    value="consumer"
                    checked={userType === 'consumer'}
                    onChange={() => setUserType('consumer')}
                  /> 
                  Consumer
                </label>
                <label className="radio ml-3">
                  <input 
                    type="radio" 
                    name="userType" 
                    value="company"
                    checked={userType === 'company'}
                    onChange={() => setUserType('company')}
                  /> 
                  Company
                </label>
              </div>
            </div>
            <div className="field">
              <label className="label">Email</label>
              <div className="control has-icons-left">
                <input 
                  className="input" 
                  type="email" 
                  placeholder="you@example.com" 
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  required
                />
                <span className="icon is-small is-left">
                  <i className="fas fa-envelope"></i>
                </span>
              </div>
            </div>
            <div className="field">
              <label className="label">Password</label>
              <div className="control has-icons-left">
                <input 
                  className="input" 
                  type="password" 
                  placeholder="********" 
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                />
                <span className="icon is-small is-left">
                  <i className="fas fa-lock"></i>
                </span>
              </div>
            </div>
            {error && <p className="help is-danger">{error}</p>}
            <div className="field mt-4">
              <div className="control">
                <button type="submit" className="button is-primary is-fullwidth has-background-theme-green-1">
                  Login
                </button>
              </div>
            </div>
            <div className="has-text-centered mt-3">
              <p>
                Don't have an account? <a href="/signup">Sign Up</a>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
