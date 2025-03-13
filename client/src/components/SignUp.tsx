import React, { useState } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router-dom';

const SignUp: React.FC = () => {
  // State for managing form data and registration status
  const [userType, setUserType] = useState('consumer'); // "consumer" or "company"
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [registered, setRegistered] = useState(false);
  const [error, setError] = useState('');

  // Validate form inputs
  const validateForm = (): boolean => {
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return false;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return false;
    }
    return true;
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate inputs
    if (!validateForm()) return;
    
    setError('');
    
    // Determine the endpoint based on user type
    const endpoint = userType === 'consumer' ? '/api/auth/consumer/register' : '/api/auth/company/register';
    
    try {
      const res = await axios.post(endpoint, { name, email, password });
      if (res.status === 201 || res.status === 200) {
        setRegistered(true);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    }
  };

  // Redirect after successful registration
  if (registered) {
    return <Redirect to="/login" />;
  }

  return (
    <div className="signup-container container mt-5">
      <div className="columns is-centered">
        <div className="column is-6">
          <form onSubmit={handleSubmit} className="box">
            <h2 className="title has-text-centered">Create an Account</h2>
            
            <div className="field">
              <label className="label">I am a</label>
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
              <label className="label">Name</label>
              <div className="control">
                <input 
                  className="input" 
                  type="text" 
                  placeholder="Your name or company name" 
                  value={name}
                  onChange={e => setName(e.target.value)}
                  required
                />
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
                  placeholder="******" 
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                  minLength={6}
                />
              </div>
            </div>
            
            <div className="field">
              <label className="label">Confirm Password</label>
              <div className="control">
                <input 
                  className="input" 
                  type="password" 
                  placeholder="******" 
                  value={confirmPassword}
                  onChange={e => setConfirmPassword(e.target.value)}
                  required
                />
              </div>
            </div>
            
            {error && <p className="help is-danger">{error}</p>}
            
            <div className="field mt-4">
              <div className="control">
                <button type="submit" className="button is-primary is-fullwidth">
                  Sign Up
                </button>
              </div>
            </div>
            
            <div className="has-text-centered mt-3">
              <p>
                Already have an account? <a href="/login">Login</a>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SignUp; 