import React, { useState } from 'react';
import axios from 'axios';

const ImageUpload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
      setError(''); // Clear any previous errors
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a file first');
      return;
    }
    
    try {
      setLoading(true);
      setError('');
      
      // Build form data
      const formData = new FormData();
      formData.append('image', file);

      // Make API call to backend
      const response = await axios.post('/api/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        timeout: 30000 // 30 seconds timeout
      });

      setResult(response.data);
    } catch (err: any) {
      console.error('Error uploading image:', err);
      
      if (err.response) {
        // Server responded with an error
        setError(`Server error: ${err.response.data || err.response.status}`);
      } else if (err.request) {
        // Request was made but no response
        setError('No response from server. Please try again later.');
      } else {
        // Something else happened
        setError(`Error: ${err.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="box" style={{ maxWidth: 600, margin: '0 auto' }}>
        <h1 className="title has-text-centered">QR Code Scanner</h1>
        <p className="subtitle has-text-centered">Upload an image containing a QR code</p>
        
        <div className="field">
          <div className="file has-name is-fullwidth">
            <label className="file-label">
              <input 
                className="file-input" 
                type="file" 
                accept="image/*" 
                onChange={handleFileChange} 
              />
              <span className="file-cta">
                <span className="file-icon">
                  <i className="fas fa-upload"></i>
                </span>
                <span className="file-label">
                  Choose a file…
                </span>
              </span>
              <span className="file-name">
                {file ? file.name : 'No file selected'}
              </span>
            </label>
          </div>
        </div>
        
        {error && (
          <div className="notification is-danger">
            <button className="delete" onClick={() => setError('')}></button>
            {error}
          </div>
        )}
        
        <div className="field">
          <div className="control">
            <button 
              className={`button is-primary is-fullwidth ${loading ? 'is-loading' : ''}`} 
              onClick={handleUpload} 
              disabled={!file || loading}
            >
              Upload and Process
            </button>
          </div>
        </div>

        {result && (
          <div className="box mt-4">
            <h2 className="subtitle">Product Details:</h2>
            <div className="content">
              <p><strong>Product Name:</strong> {result.name || 'N/A'}</p>
              <p><strong>Category:</strong> {result.category || 'N/A'}</p>
              <p><strong>Price:</strong> {result.price ? `$${result.price}` : 'N/A'}</p>
              
              <details>
                <summary>View Raw Data</summary>
                <pre style={{ 
                  whiteSpace: 'pre-wrap', 
                  maxHeight: '200px', 
                  overflowY: 'auto',
                  padding: '10px',
                  background: '#f5f5f5' 
                }}>
                  {JSON.stringify(result, null, 2)}
                </pre>
              </details>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ImageUpload;
