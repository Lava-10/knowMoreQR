import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';

// Mock product data for demo purposes
const mockProducts = [
  {
    id: 'demo1',
    name: 'Organic Cotton T-Shirt',
    series: 'Eco Essentials',
    unitPrice: 29.99,
    description: 'Made from 100% organic cotton, this t-shirt is produced using sustainable farming methods with no harmful chemicals.',
    materials: 'Organic cotton certified by GOTS (Global Organic Textile Standard)',
    carbonFootprint: '4.3 kg CO2',
    waterUsage: '2,700 liters',
    recyclable: true,
    biodegradable: true,
    ethicalLabor: 'Fair Trade Certified',
    image: 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
  },
  {
    id: 'demo2',
    name: 'Recycled Denim Jeans',
    series: 'Second Life Collection',
    unitPrice: 79.99,
    description: 'These jeans are made from 70% recycled denim and 30% organic cotton. Each pair saves approximately 1,500 liters of water compared to traditional manufacturing.',
    materials: '70% recycled denim, 30% organic cotton',
    carbonFootprint: '8.1 kg CO2',
    waterUsage: '800 liters (80% less than traditional jeans)',
    recyclable: true,
    biodegradable: false,
    ethicalLabor: 'Living Wage Certified',
    image: 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
  }
];

interface ParamTypes {
  id?: string;
}

const QRScan: React.FC = () => {
  const { id } = useParams<ParamTypes>();
  const [product, setProduct] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [camera, setCamera] = useState<boolean>(false);

  useEffect(() => {
    // Simulate loading from API for demo
    setLoading(true);
    
    if (id) {
      // If ID is provided, look up product directly
      const foundProduct = mockProducts.find(p => p.id === id);
      if (foundProduct) {
        setTimeout(() => {
          setProduct(foundProduct);
          setLoading(false);
        }, 1000); // Simulate network delay
      } else {
        setTimeout(() => {
          setError('Product not found');
          setLoading(false);
        }, 1000);
      }
    } else {
      // Initial state - no product yet, ready to scan
      setLoading(false);
    }
  }, [id]);

  const startScanning = () => {
    setCamera(true);
    // Simulate QR code scan after 3 seconds
    setTimeout(() => {
      // Randomly select one of the demo products
      const randomProduct = mockProducts[Math.floor(Math.random() * mockProducts.length)];
      setProduct(randomProduct);
      setCamera(false);
    }, 3000);
  };

  if (loading) {
    return (
      <div className="section container has-text-centered">
        <div className="loader-wrapper">
          <div className="loader is-loading"></div>
        </div>
        <p className="mt-4">Loading product information...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="section container">
        <div className="notification is-danger">
          <p>{error}</p>
          <Link to="/scan" className="button is-light mt-3">Try Again</Link>
        </div>
      </div>
    );
  }

  if (camera) {
    return (
      <div className="section container has-text-centered">
        <div className="box p-6">
          <h2 className="title is-4 mb-4">Scanning QR Code...</h2>
          <div className="qr-scanner-box mb-4">
            <div className="scanner-overlay">
              <div className="scanner-line"></div>
            </div>
          </div>
          <p>Position the QR code in the center of the frame</p>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="section container">
        <div className="box has-text-centered p-6">
          <h1 className="title has-text-theme-green-1">Scan a QR Code</h1>
          <p className="subtitle mb-5">Discover the sustainability story behind the product</p>
          
          <button 
            className="button is-large is-primary has-background-theme-green-1 my-4"
            onClick={startScanning}
          >
            <span className="icon">
              <i className="fas fa-qrcode"></i>
            </span>
            <span>Start Scanning</span>
          </button>
          
          <p className="mt-4">Or try our demo products:</p>
          <div className="buttons is-centered mt-3">
            {mockProducts.map(p => (
              <Link 
                key={p.id}
                to={`/scan/${p.id}`}
                className="button is-light"
              >
                {p.name}
              </Link>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="section container">
      <div className="box p-6">
        <div className="columns">
          <div className="column is-5">
            <figure className="image is-4by3">
              <img src={product.image} alt={product.name} className="is-rounded" />
            </figure>
          </div>
          <div className="column is-7">
            <h1 className="title has-text-theme-green-1">{product.name}</h1>
            <h2 className="subtitle has-text-grey">{product.series}</h2>
            
            <div className="tags">
              {product.recyclable && (
                <span className="tag is-success">Recyclable</span>
              )}
              {product.biodegradable && (
                <span className="tag is-success">Biodegradable</span>
              )}
              <span className="tag is-info">{product.ethicalLabor}</span>
            </div>
            
            <div className="content mt-4">
              <p>{product.description}</p>
              
              <h3 className="title is-5 mt-5 mb-3">Environmental Impact</h3>
              <div className="columns is-multiline">
                <div className="column is-6">
                  <div className="box has-background-theme-beige">
                    <h4 className="is-size-6 has-text-weight-bold">Carbon Footprint</h4>
                    <p>{product.carbonFootprint}</p>
                  </div>
                </div>
                <div className="column is-6">
                  <div className="box has-background-theme-beige">
                    <h4 className="is-size-6 has-text-weight-bold">Water Usage</h4>
                    <p>{product.waterUsage}</p>
                  </div>
                </div>
              </div>
              
              <h3 className="title is-5 mt-4 mb-3">Materials</h3>
              <p>{product.materials}</p>
              
              <div className="has-text-right mt-5">
                <p className="has-text-grey mb-2">Price: ${product.unitPrice.toFixed(2)}</p>
                <div className="buttons is-right">
                  <button className="button is-outlined is-primary has-text-theme-green-1">
                    <span className="icon">
                      <i className="fas fa-heart"></i>
                    </span>
                    <span>Add to Wishlist</span>
                  </button>
                  <Link to="/scan" className="button is-primary has-background-theme-green-1">
                    <span className="icon">
                      <i className="fas fa-qrcode"></i>
                    </span>
                    <span>Scan Another</span>
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <style jsx>{`
        .qr-scanner-box {
          width: 300px;
          height: 300px;
          border: 2px solid #83B7A1;
          margin: 0 auto;
          position: relative;
          overflow: hidden;
        }
        .scanner-overlay {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: transparent;
        }
        .scanner-line {
          position: absolute;
          width: 100%;
          height: 2px;
          background: #83B7A1;
          animation: scan 2s infinite;
        }
        @keyframes scan {
          0% { top: 0; }
          50% { top: 100%; }
          100% { top: 0; }
        }
      `}</style>
    </div>
  );
};

export default QRScan; 