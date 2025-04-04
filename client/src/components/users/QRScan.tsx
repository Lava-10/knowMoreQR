import React, { useState } from 'react';
import { QrReader } from 'react-qr-reader';
import { useHistory } from 'react-router-dom';

const QRScan: React.FC = () => {
    const history = useHistory();
    const [error, setError] = useState<string | null>(null);
    const [data, setData] = useState<string | null>(null); // Store scanned data

    const handleResult = (result: any, error: any) => {
        if (!!result) {
            const scannedText = result?.text;
            if (scannedText) {
                setData(scannedText);
                setError(null);
                console.log('QR Code Scanned:', scannedText);
                
                // --- Logic to handle scanned data --- 
                // Example: Assuming QR code contains the Tag ID (UUID)
                // Basic validation (is it a potential UUID?)
                // A more robust check would use a regex for UUID format
                if (scannedText.length > 30 && scannedText.includes('-')) { 
                    // Redirect to product view page
                    history.push(`/buy/dashboard/${scannedText}`); 
                } else if (scannedText.startsWith('http')) {
                    // If it's a URL, maybe try to extract an ID or navigate differently
                    // For now, just display it
                    setError('Scanned a URL, unsure how to proceed: ' + scannedText);
                } else {
                    // If it's not recognized as ID or URL
                    setError('Scanned data format not recognized: ' + scannedText);
                }
            }
        }

        if (!!error) {
            // Don't spam console for common errors like no QR code found
            if (error?.message?.includes('No QR code found')) {
                // console.info('No QR code found in frame.'); 
            } else { 
                 console.error('QR Scan Error:', error);
                 setError('Error scanning QR code. Please ensure camera permissions and try again.');
            }
          
        }
    };

    return (
        <div className="container mt-5 pt-5">
            <h1 className="title has-text-centered">Scan Product QR Code</h1>
            <div className="box" style={{ maxWidth: '500px', margin: 'auto' }}>
                <QrReader
                    onResult={handleResult}
                    constraints={{ facingMode: 'environment' }} // Use rear camera
                    scanDelay={500} // Delay between scans
                    containerStyle={{ width: '100%' }}
                />
                {error && (
                    <div className="notification is-danger mt-4">
                       {error}
                    </div>
                )}
                {data && !error && (
                     <div className="notification is-success mt-4">
                        Scanned: {data} (Redirecting...)
                    </div>
                )}
                {!data && !error && (
                    <p className="mt-3 has-text-centered'>Point your camera at a QR code.</p>
                )}
            </div>
        </div>
    );
};

export default QRScan; 