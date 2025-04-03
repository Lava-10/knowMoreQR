import React from 'react';
import '../assets/css/home.scss';
import landing from '../assets/img/landing.png';
import { Link } from 'react-router-dom';

interface Props {
}

interface State {
  scrollTop: number;
}

class Home extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      scrollTop: 0
    };
  }

  componentDidMount() {
    window.addEventListener("scroll", this.handleScroll);
  }

  componentWillUnmount() {
    window.removeEventListener("scroll", this.handleScroll);
  }

  handleScroll = () => {
    if (document.documentElement.scrollTop < 600) {
      this.setState({scrollTop: document.documentElement.scrollTop});
    }
  };

  render() {
    return (
      <div className="home">
        {/* Hero Section */}
        <section className="hero is-fullheight-with-navbar has-background-theme-beige">
          <div className="hero-body">
            <div className="container">
              <div className="columns is-vcentered">
                <div className="column is-6">
                  <h1 className="title is-1 has-text-theme-green-1">
                    Transparency for Sustainable Fashion
                  </h1>
                  <h2 className="subtitle is-4 mt-4 has-text-theme-green-1">
                    Bridging the gap between sustainable production and informed consumer choices
                  </h2>
                  <div className="buttons mt-5">
                    <Link to="/signup" className="button is-primary has-background-theme-green-1 is-large">
                      Get Started
                    </Link>
                    <Link to="/login" className="button is-outlined is-primary has-text-theme-green-1 is-large">
                      Login
                    </Link>
                  </div>
                </div>
                <div className="column is-6">
                  <img src={landing} alt="QR code scanning demonstration" className="is-rounded shadow" />
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Features Section */}
        <section className="section has-background-white">
          <div className="container">
            <h2 className="title has-text-centered has-text-theme-green-1 mb-6">How It Works</h2>
            
            <div className="columns">
              <div className="column is-4">
                <div className="card has-background-theme-beige h-100">
                  <div className="card-content">
                    <div className="has-text-centered mb-4">
                      <span className="icon is-large">
                        <i className="fas fa-building fa-3x has-text-theme-green-1"></i>
                      </span>
                    </div>
                    <h3 className="title is-4 has-text-theme-green-1">For Companies</h3>
                    <div className="content">
                      <ul>
                        <li>Document your sustainability efforts</li>
                        <li>Create QR code tags for products</li>
                        <li>Track scan statistics and engagement</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
              
              <div className="column is-4">
                <div className="card has-background-theme-beige h-100">
                  <div className="card-content">
                    <div className="has-text-centered mb-4">
                      <span className="icon is-large">
                        <i className="fas fa-qrcode fa-3x has-text-theme-green-1"></i>
                      </span>
                    </div>
                    <h3 className="title is-4 has-text-theme-green-1">QR Technology</h3>
                    <div className="content">
                      <ul>
                        <li>Simple scan with any smartphone</li>
                        <li>Instant access to product information</li>
                        <li>Connect physical products to digital details</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
              
              <div className="column is-4">
                <div className="card has-background-theme-beige h-100">
                  <div className="card-content">
                    <div className="has-text-centered mb-4">
                      <span className="icon is-large">
                        <i className="fas fa-user fa-3x has-text-theme-green-1"></i>
                      </span>
                    </div>
                    <h3 className="title is-4 has-text-theme-green-1">For Consumers</h3>
                    <div className="content">
                      <ul>
                        <li>Learn about product environmental impact</li>
                        <li>Save items to a personalized wishlist</li>
                        <li>Chat with AI for recommendations</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
        
        {/* Call to Action */}
        <section className="section has-background-theme-green-1">
          <div className="container has-text-centered">
            <h2 className="title has-text-theme-beige mb-5">Ready to make more sustainable choices?</h2>
            <Link to="/signup" className="button is-large is-primary has-background-theme-beige has-text-theme-green-1">
              Join knowMoreQR Today
            </Link>
          </div>
        </section>
      </div>
    );
  }
}

export default Home;
