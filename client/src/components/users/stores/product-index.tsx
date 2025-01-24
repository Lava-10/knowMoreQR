import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Tag } from '../../types';
import { useAuth } from '../../context/AuthContext';
import '../../assets/css/store-dashboard.scss';
import Sidebar from './sidebar';
import StatCard from './stat-card';
import ProductCard from './product-card';

interface ProductsProps {
}

interface knowMoreQRtag {
  id: string,
  name: string;
  series: string;
  unitPrice: number;
  salePrice: number;
  description: string;
  colourways: string[][];
  sizeChart: number[][];
  media: string[];
  stories: string[][];
  materials: string;
  instructions: string;
  itemFeatures: string[];
  views: number;
  saves: number;
  qr: string;
}

interface ProductsState {
  totalScans: number;
  knowMoreQRtags: knowMoreQRtag[];
  tags: Tag[];
  isLoading: boolean;
  error: string;
}

const ProductIndex: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [tags, setTags] = useState<Tag[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const fetchCompanyTags = async () => {
      setIsLoading(true);
      setError('');
      try {
        const response = await axios.get<Tag[]>('/tags/my-tags');
        setTags(response.data);
      } catch (err: any) {
        console.error("Error fetching company tags:", err);
        if (err.response?.status === 403) {
          setError('Access denied. You must be logged in as a company.');
        } else {
          setError(`Failed to load products. ${err.response?.data?.message || err.message || 'Please try again later.'}`);
        }
        setTags([]);
      } finally {
        setIsLoading(false);
      }
    };

    if (isAuthenticated) {
      fetchCompanyTags();
    } else {
      setError('Please log in as a company to view your products.');
      setIsLoading(false);
      setTags([]);
    }
  }, [isAuthenticated]);

  return (
    <div className="container mt-5 pt-5">
      <div className="is-flex is-justify-content-space-between is-align-items-center mb-4">
        <h1 className="title">My Products</h1>
        <Link to="/sell/dashboard/new" className="button is-primary has-background-theme-green-1">
          Add New Product
        </Link>
      </div>

      {isLoading && <p>Loading products...</p>}
      {error && <div className="notification is-danger">{error}</div>}

      {!isLoading && !error && tags.length === 0 && (
        <div className="notification is-warning">You haven't added any products yet.</div>
      )}

      {!isLoading && !error && tags.length > 0 && (
        <table className="table is-fullwidth is-striped is-hoverable">
          <thead>
            <tr>
              <th>Name</th>
              <th>Series</th>
              <th>Price</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {tags.map(tag => (
              <tr key={tag.id}>
                <td>{tag.name || 'N/A'}</td>
                <td>{tag.series || 'N/A'}</td>
                <td>${tag.salePrice?.toFixed(2) ?? tag.unitPrice?.toFixed(2) ?? 'N/A'}</td>
                <td>
                  <Link to={`/sell/dashboard/edit/${tag.id}`} className="button is-small is-link is-light mr-2">
                    Edit
                  </Link>
                  <button className="button is-small is-danger is-light">
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ProductIndex;
