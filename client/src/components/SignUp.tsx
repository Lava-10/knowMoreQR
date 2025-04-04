import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';
import axios from 'axios';

const SignUp: React.FC = () => {
  // State for managing form data and registration status
  const [userType, setUserType] = useState('consumer'); // "consumer" or "company"
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [registered, setRegistered] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

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
    setLoading(true);
    
    try {
      const endpoint = userType === 'consumer' 
        ? '/api/auth/consumer/register' 
        : '/api/auth/company/register';
      
      const userData = {
        name: name,
        email: email,
        password: password
      };
      
      const response = await axios.post(endpoint, userData);
      
      if (response.status === 201 || response.status === 200) {
        setRegistered(true);
      }
    } catch (err: any) {
      console.error('Registration error:', err);
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
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
              <div className="control has-icons-left">
                <input 
                  className="input" 
                  type="text" 
                  placeholder="Your name or company name" 
                  value={name}
                  onChange={e => setName(e.target.value)}
                  required
                />
                <span className="icon is-small is-left">
                  <i className="fas fa-user"></i>
                </span>
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
                  placeholder="******" 
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                  minLength={6}
                />
                <span className="icon is-small is-left">
                  <i className="fas fa-lock"></i>
                </span>
              </div>
            </div>
            
            <div className="field">
              <label className="label">Confirm Password</label>
              <div className="control has-icons-left">
                <input 
                  className="input" 
                  type="password" 
                  placeholder="******" 
                  value={confirmPassword}
                  onChange={e => setConfirmPassword(e.target.value)}
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
                <button 
                  type="submit" 
                  className={`button is-primary is-fullwidth has-background-theme-green-1 ${loading ? 'is-loading' : ''}`}
                  disabled={loading}
                >
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