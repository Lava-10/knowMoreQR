import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext'; // Import useAuth hook

const Login: React.FC = () => {
  // Use auth context
  const { login, isAuthenticated, userType: authUserType } = useAuth();

  // State for managing form data (userType selection, email, password)
  const [userType, setUserType] = useState('consumer'); // Form selection
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  // Removed local loggedIn state
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const endpoint = userType === 'consumer' ? '/api/auth/consumer/login' : '/api/auth/company/login';
      // The response now contains: { id, email, userType, token }
      const response = await axios.post<{ id: string; email: string; userType: 'consumer' | 'company'; token: string }>(endpoint, { email, password });

      if (response.data && response.data.token) {
        // Call the login function from context
        login({ 
          token: response.data.token,
          id: response.data.id,
          email: response.data.email,
          userType: response.data.userType
        });
        // No need to set local loggedIn state, context handles it
        // No need to manipulate localStorage directly, context handles it
      } else {
          // Handle case where token might be missing in response
          setError('Login failed: Invalid response from server.');
      }
    } catch (err: any) {
      console.error('Login error:', err);
       if (axios.isAxiosError(err) && err.response && err.response.status === 401) {
            setError('Invalid email or password.');
       } else {
            setError('An unexpected error occurred. Please try again.');
       }
    } finally {
      setLoading(false);
    }
  };

  // Redirect based on isAuthenticated state from context
  if (isAuthenticated) {
    // Redirect based on the userType stored in the context
    return <Redirect to={authUserType === 'consumer' ? "/buy/dashboard" : "/sell/dashboard"} />;
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
                <button 
                  type="submit" 
                  className={`button is-primary is-fullwidth has-background-theme-green-1 ${loading ? 'is-loading' : ''}`}
                  disabled={loading}
                >
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
