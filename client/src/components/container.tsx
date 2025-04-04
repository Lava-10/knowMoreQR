import React from 'react';
import { HashRouter as Router, Route, Link, Switch, useLocation } from 'react-router-dom';
import { withRouter } from 'react-router-dom';
import logo_dark from '../assets/img/full-logo-knowMoreQR-green.png';
import logo_light from '../assets/img/full-logo-knowMoreQR-beige.png';
import '../assets/css/navbar.scss';

// Import all components you need to route
import Home from './home';
import ProductIndex from './stores/product-index';
import ProductNew from './stores/product-new';
import ProductView from './users/product-view';
import Dashboard from './users/dashboard';
import Login from './Login';            // Make sure this path is correct
import SignUp from './SignUp';
import ChatWishlist from './users/ChatWishlist'; // Make sure this path is correct
import ImageUpload from './ImageUpload'; // Fixed the import path
import QRScan from './users/QRScan'; // Add the QR Scan component
import ProtectedRoute from './ProtectedRoute'; // Import ProtectedRoute
import { useAuth } from '../context/AuthContext'; // Import useAuth to adjust Navbar

const Navbar: React.FC = () => {
  const location = useLocation(); // Use useLocation hook
  const { isAuthenticated, logout, userType } = useAuth(); // Get auth state and logout function

  const handleLogout = () => {
    logout();
    // Optionally redirect to home or login after logout
    // props.history.push('/'); // Would need withRouter or useHistory hook
  };

  // Common function to handle burger toggle
  const toggleBurger = () => {
    const burger = document.querySelector('.navbar-burger');
    const menu = document.querySelector('.navbar-menu');
    if (burger && menu) {
      burger.classList.toggle('is-active');
      menu.classList.toggle('is-active');
    }
  };

  // Home page navbar style
  if (location.pathname === "/") {
    return (
      <nav className="navbar shadow is-fixed-top has-background-theme-beige">
        <div className="navbar-brand">
          <Link to="/" className="navbar-item m-0 p-0">
            <img
              className="logo ml-5"
              src={logo_dark}
              width="145"
              height="50"
              alt="knowMoreQR green logo"
            />
          </Link>
          <div
            role="button"
            className="navbar-burger burger"
            data-target="navMenu"
            aria-label="menu"
            aria-expanded="false"
            onClick={toggleBurger}
          >
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
          </div>
        </div>
        <div
          className="navbar-menu has-text-left-touch has-background-theme-beige is-align-items-center"
          id="navMenu"
        >
          <div className="navbar-end">
            {isAuthenticated ? (
              <>
                {userType === 'company' && (
                  <Link
                    to="/sell/dashboard"
                    className="navbar-item link mt-1 has-text-weight-semibold has-text-theme-green-1"
                    onClick={toggleBurger}
                  >
                    My Store
                  </Link>
                )}
                {userType === 'consumer' && (
                  <Link
                    to="/buy/dashboard"
                    className="navbar-item link mt-1 has-text-weight-semibold has-text-theme-green-1"
                    onClick={toggleBurger}
                  >
                    My Dashboard
                  </Link>
                )}
                <Link
                  to="/scan"
                  className="navbar-item link mt-1 has-text-weight-semibold has-text-theme-green-1"
                  onClick={toggleBurger}
                >
                  Scan QR
                </Link>
                <button
                  onClick={() => {
                    handleLogout();
                    toggleBurger();
                  }}
                  className="navbar-item button is-ghost has-text-theme-green-1 has-text-weight-semibold"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/sell/dashboard"
                  className="navbar-item link mt-1 has-text-weight-semibold has-text-theme-green-1"
                  onClick={toggleBurger}
                >
                  Sell (Login Required)
                </Link>
                <Link
                  to="/buy/dashboard/"
                  className="navbar-item link mt-1 has-text-weight-semibold has-text-theme-green-1"
                  onClick={toggleBurger}
                >
                  Shop (Login Required)
                </Link>
                <Link
                  to="/scan"
                  className="navbar-item link mt-1 has-text-weight-semibold has-text-theme-green-1"
                  onClick={toggleBurger}
                >
                  Scan QR
                </Link>
                <Link
                  to="/login"
                  className="navbar-item link mt-1 has-text-weight-semibold has-text-theme-green-1"
                  onClick={toggleBurger}
                >
                  Login
                </Link>
                <div className="navbar-item link">
                  <div className="control">
                    <Link
                      to="/signup"
                      className="has-text-weight-semibold has-text-theme-beige is-theme-green-1 button shadow"
                      onClick={toggleBurger}
                    >
                      <div className="mt-1">Get Started</div>
                    </Link>
                  </div>
                </div>
              </>
            )}
          </div>
        </div>
      </nav>
    );
  } else {
    return (
      <nav className="navbar navbar-2 is-fixed-top shadow has-background-theme-green-1">
        <div className="navbar-brand navbar-brand-2 navbar-sidebar px-5 is-flex is-justify-content-center">
          <Link
            to={isAuthenticated ? (userType === 'consumer' ? "/buy/dashboard" : "/sell/dashboard") : "/"}
            className="navbar-item m-0 p-0"
          >
            <img
              className="logo"
              src={logo_light}
              width="100"
              alt="knowMoreQR beige logo"
            />
          </Link>
          <div
            role="button"
            className="has-text-white navbar-burger navbar-burger-2 burger"
            data-target="navMenu"
            aria-label="menu"
            aria-expanded="false"
            onClick={toggleBurger}
          >
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
          </div>
        </div>
        <div
          className="navbar-menu has-background-theme-green-1 is-align-items-center px-5"
          id="navMenu"
        >
          <div className="navbar-end">
            {isAuthenticated ? (
              <>
                <Link
                  to="/scan"
                  className="navbar-item has-text-theme-beige"
                  onClick={toggleBurger}
                >
                  <i className="fas fa-qrcode is-size-5 mr-2"></i> Scan QR
                </Link>
                <button
                  onClick={() => {
                    handleLogout();
                    toggleBurger();
                  }}
                  className="navbar-item button is-ghost has-text-theme-beige"
                >
                  <i className="fas fa-sign-out-alt is-size-5 mr-2"></i> Logout
                </button>
              </>
            ) : (
              <Link
                to="/login"
                className="navbar-item has-text-theme-beige"
                onClick={toggleBurger}
              >
                Login / Sign Up
              </Link>
            )}
          </div>
        </div>
      </nav>
    );
  }
};

const Container: React.FC = () => {
  return (
    <Router>
      <Navbar />
      <Switch>
        {/* Authentication */}
        <Route exact path="/login" component={Login} />
        <Route exact path="/signup" component={SignUp} />
        {/* Home */}
        <Route exact path="/" component={Home} />
        {/* Seller Dashboard */}
        <ProtectedRoute exact path="/sell/dashboard" component={ProductIndex} />
        <ProtectedRoute exact path="/sell/dashboard/new" component={ProductNew} />
        {/* Buyer Dashboard */}
        <ProtectedRoute exact path="/buy/dashboard" component={Dashboard} />
        <ProtectedRoute exact path="/buy/dashboard/:id" component={ProductView} />
        <ProtectedRoute exact path="/buy/chat-wishlist" component={ChatWishlist} />
        {/* QR Scan */}
        <ProtectedRoute exact path="/scan/:id?" component={QRScan} />
        {/* NEW Route: Image Upload Demo */}
        <ProtectedRoute exact path="/upload-image" component={ImageUpload} />
        {/* 404 catch-all */}
        <Route path="*">
          <h1 className="title is-size-1 mt-6 pt-6">404 Not Found.</h1>
          <Link to="/">Go Home</Link>
        </Route>
      </Switch>
    </Router>
  );
};

export default Container;
