import React, { useState } from 'react';
import axios from 'axios';

const ImageUpload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<any>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
    }
  };

  const handleUpload = async () => {
    if (!file) return;
    try {
      // Build form data
      const formData = new FormData();
      formData.append('image', file);

      // Adjust the URL if your backend runs on a different host/port
      // e.g., "http://localhost:8080/api/upload"
      const response = await axios.post('/api/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      setResult(response.data);
    } catch (err) {
      console.error('Error uploading image:', err);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '40px auto' }}>
      <h1>Upload an Image</h1>
      <div style={{ marginBottom: '1rem' }}>
        <input type="file" accept="image/*" onChange={handleFileChange} />
      </div>
      <button onClick={handleUpload} disabled={!file}>
        Upload
      </button>

      {result && (
        <div style={{ marginTop: '1rem', whiteSpace: 'pre-wrap' }}>
          <h2>Result from OCR & Classification:</h2>
          <pre>{JSON.stringify(result, null, 2)}</pre>
        </div>
      )}
    </div>
  );
};

export default ImageUpload;
