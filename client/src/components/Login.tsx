import React, { useState } from 'react';
import axios from 'axios';
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
    // Determine the endpoint based on user type
    const endpoint = userType === 'consumer' ? '/auth/consumer/login' : '/auth/company/login';
    try {
      const res = await axios.post(endpoint, { email, password });
      if (res.status === 200) {
        // You can store a JWT or other auth token here if returned
        setLoggedIn(true);
      }
    } catch (err) {
      setError('Invalid credentials. Please try again.');
    }
  };

  // Redirect after login (modify the path as needed)
  if (loggedIn) {
    return <Redirect to={userType === 'consumer' ? "/buy/dashboard" : "/sell/dashboard"} />;
  }

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="box">
        <h2 className="title">Login</h2>
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
          <div className="control">
            <input 
              className="input" 
              type="email" 
              placeholder="you@example.com" 
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>
        </div>
        <div className="field">
          <label className="label">Password</label>
          <div className="control">
            <input 
              className="input" 
              type="password" 
              placeholder="********" 
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>
        </div>
        {error && <p className="help is-danger">{error}</p>}
        <div className="field mt-4">
          <div className="control">
            <button type="submit" className="button is-primary">Login</button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default Login;
