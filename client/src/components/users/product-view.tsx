import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Tag } from '../../types'; // Import Tag type
import '../../assets/css/product.scss';

// Define Params type for router
interface RouteParams {
    id: string;
}

const ProductView: React.FC = () => {
    // Get ID from route params
    const { id } = useParams<RouteParams>();

    // State for tag data, loading, error, color selection, and modal
    const [tag, setTag] = useState<Tag | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');
    const [activeColorIndex, setActiveColorIndex] = useState<number>(0);
    const [isSizeChartOpen, setIsSizeChartOpen] = useState<boolean>(false);

    // Fetch tag data when component mounts or ID changes
    useEffect(() => {
        const fetchTag = async () => {
            setIsLoading(true);
            setError('');
            try {
                // Use relative path for API call - proxy should handle it
                const response = await axios.get<Tag>(`/tags/${id}`);
                setTag(response.data);
                setActiveColorIndex(0); // Reset color index when new tag loads
            } catch (err: any) {
                console.error("Error fetching tag:", err);
                setError(`Failed to load product details. ${err.response?.data?.message || err.message || 'Please try again later.'}`);
                setTag(null);
            } finally {
                setIsLoading(false);
            }
        };

        if (id) {
            fetchTag();
        }
    }, [id]);

    // Handlers for color selection and modal toggle
    const handleColorChange = (index: number) => {
        setActiveColorIndex(index);
    };

    const toggleSizeChart = () => {
        setIsSizeChartOpen(!isSizeChartOpen);
    };

    // Conditional rendering based on state
    if (isLoading) {
        return <div className="container mt-6 pt-6 has-text-centered"><p>Loading product...</p></div>;
    }

    if (error) {
        return <div className="container mt-6 pt-6"><div className="notification is-danger">{error}</div></div>;
    }

    if (!tag) {
        return <div className="container mt-6 pt-6"><p>Product not found.</p></div>;
    }

    // Get current colorway based on active index (handle potential empty array)
    const currentColorway = tag.colourways && tag.colourways.length > activeColorIndex ? tag.colourways[activeColorIndex] : ['N/A', '#cccccc', ''];
    const currentImageUrl = currentColorway[2] || (tag.media && tag.media.length > 0 ? tag.media[0] : '/placeholder.png'); // Use first media as fallback

    // --- Render Logic (Adapted from class component, using state hooks) ---
    return (
        <div className="navbar-offset pt-6">
            <div className="container mt-6 container-custom">
                {/* Main Product Info Row */}
                <div className="columns mt-6 mb-4 w-100 mx-0 is-flex-wrap-wrap">
                    {/* Image Column */}
                    <div className="column is-6-desktop is-full-mobile">
                        <div className="product-image-container mx-5">
                            <img className="product-image" src={currentImageUrl} alt={tag.name} />
                        </div>
                    </div>
                    {/* Details Column */}
                    <div className="column is-6-desktop is-full-mobile has-text-left px-5">
                        <h1 className="title is-size-4 has-text-theme-green-1">{tag.name}</h1>
                        <p className="collection pt-3 pb-4">{tag.series}</p>
                        <hr />
                        {/* Color Selection */}
                        <p className="is-size-6 is-text-centered-touch has-text-theme-green-1 has-text-weight-light opacity-60">
                            Color: {currentColorway[0]}
                        </p>
                        <div className="is-flex is-justify-content-center-touch w-100 mt-2 mb-5 pb-3">
                            {tag.colourways?.map((colour, i) => (
                                <label
                                    key={i}
                                    className={`color-radio mr-4 ${i === activeColorIndex ? 'active' : ''}`}
                                    style={{ backgroundColor: colour[1] || '#cccccc' }}
                                    onClick={() => handleColorChange(i)}
                                    title={colour[0]}
                                />
                            ))}
                        </div>
                        <hr />
                        {/* Size Selection & Chart */}
                        <div className="is-flex is-flex-direction-row is-flex-direction-column-touch w-100 mt-2 mb-5 is-justify-content-space-between is-align-items-center">
                            {/* TODO: Implement actual size selection logic if needed */}
                            <div className="is-flex">
                                <span className="is-flex is-justify-content-center is-align-items-center size-radio mr-4 pt-1 is-static">XS</span>
                                <span className="is-flex is-justify-content-center is-align-items-center size-radio mr-4 pt-1 is-static">S</span>
                                <span className="is-flex is-justify-content-center is-align-items-center size-radio mr-4 pt-1 is-static">M</span>
                                <span className="is-flex is-justify-content-center is-align-items-center size-radio mr-4 pt-1 is-static">L</span>
                                <span className="is-flex is-justify-content-center is-align-items-center size-radio mr-4 pt-1 is-static">XL</span>
                            </div>
                            <button className="button is-text is-small underline-click is-size-7 has-text-theme-green-1 has-text-weight-light mt-3" onClick={toggleSizeChart}>
                                Size Chart
                            </button>
                            {/* Size Chart Modal */}
                            <div className={`modal ${isSizeChartOpen ? 'is-active' : ''}`} id="sizeChart">
                                <div className="modal-background" onClick={toggleSizeChart}></div>
                                <div className="modal-card">
                                    <header className="modal-card-head">
                                        <p className="modal-card-title is-size-6 mb-0 pb-0 pt-1">Size Chart</p>
                                        <button className="delete" aria-label="close" onClick={toggleSizeChart}></button>
                                    </header>
                                    <section className="modal-card-body">
                                        {/* TODO: Render size chart table dynamically based on tag.sizeChart */} 
                                        <table className="table is-hoverable mt-5 mb-6 border-radius-8 w-100">
                                            <thead>
                                            <tr className="has-background-grey-lighter">
                                                <th></th><th>XS</th><th>S</th><th>M</th><th>L</th><th>XL</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                                {/* Example rows - replace with dynamic data from tag.sizeChart */}
                                                <tr><th>CHEST</th><td>{tag.sizeChart?.[0]?.[0] ?? '-'}</td><td>{tag.sizeChart?.[0]?.[1] ?? '-'}</td><td>{tag.sizeChart?.[0]?.[2] ?? '-'}</td><td>{tag.sizeChart?.[0]?.[3] ?? '-'}</td><td>{tag.sizeChart?.[0]?.[4] ?? '-'}</td></tr>
                                                <tr><th>WAIST</th><td>{tag.sizeChart?.[1]?.[0] ?? '-'}</td><td>{tag.sizeChart?.[1]?.[1] ?? '-'}</td><td>{tag.sizeChart?.[1]?.[2] ?? '-'}</td><td>{tag.sizeChart?.[1]?.[3] ?? '-'}</td><td>{tag.sizeChart?.[1]?.[4] ?? '-'}</td></tr>
                                                <tr><th>SLEEVE</th><td>{tag.sizeChart?.[2]?.[0] ?? '-'}</td><td>{tag.sizeChart?.[2]?.[1] ?? '-'}</td><td>{tag.sizeChart?.[2]?.[2] ?? '-'}</td><td>{tag.sizeChart?.[2]?.[3] ?? '-'}</td><td>{tag.sizeChart?.[2]?.[4] ?? '-'}</td></tr>
                                            </tbody>
                                        </table>
                                    </section>
                                </div>
                                <button className="modal-close is-large" aria-label="close" onClick={toggleSizeChart}></button>
                            </div>
                        </div>
                        <hr />
                        {/* Description */}
                        <div className="description-title pt-3 pb-4">DESCRIPTION</div>
                        <div className="content is-size-7 has-text-grey has-text-weight-light">
                            <p>{tag.description || 'No description available.'}</p>
                            <p><strong>Materials:</strong> {tag.materials || 'N/A'}</p>
                            <p><strong>Care Instructions:</strong> {tag.instructions || 'N/A'}</p>
                        </div>
                        <hr />
                        {/* Add to Wishlist Button? */}
                        {/* TODO: Add button to call NLP wishlist add command */} 
                         <button className="button is-primary is-fullwidth has-background-theme-green-1">
                            {/* TODO: Wire up to wishlist */} Add to Wishlist
                         </button>
                    </div>
                </div>

                {/* Features Section */}
                {tag.itemFeatures && tag.itemFeatures.length > 0 && (
                     <div className="has-text-left mx-5">
                        <div className="description-title pt-3 pb-4">FEATURES</div>
                        <hr />
                        {/* Assuming features come in pairs: [Title1, Desc1, Title2, Desc2,...] ? Adapt as needed */}
                        <div className="columns my-5 mx-0 w-100 is-flex-wrap-wrap">
                            {Array.from({ length: Math.ceil(tag.itemFeatures.length / 2) }).map((_, index) => (
                                <div key={index} className="column is-one-third-desktop is-full-touch">
                                    <div className="px-2 feature-box p-5">
                                        <div className="is-size-5 mb-3 has-text-weight-semibold">{tag.itemFeatures[index * 2] || 'Feature'}</div>
                                        <div className="is-size-7 has-text-grey has-text-weight-light">
                                            {tag.itemFeatures[index * 2 + 1] || 'Details not available.'}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {/* Media Section */}
                {tag.media && tag.media.length > 0 && (
                    <div className="has-text-left mx-5">
                        <div className="description-title pt-3 pb-4">GALLERY</div>
                        <hr />
                        <div className="columns is-multiline">
                            {tag.media.map((url, i) => (
                                <div key={i} className="column is-one-third">
                                    <figure className="image is-4by3">
                                         <img src={url} alt={`Product media ${i + 1}`} />
                                    </figure>
                                </div>
                            ))}
                         </div>
                    </div>
                )}
                
                {/* Stories/Commitments Section */}
                {tag.stories && tag.stories.length > 0 && (
                    <div className="has-text-left mx-5 mb-6">
                        <div className="description-title pt-3 pb-4">EXPLORE OUR COMMITMENTS</div>
                        <hr />
                        <div className="columns my-5 mx-0 w-100 is-flex-wrap-wrap">
                            {tag.stories.map((story, i) => (
                                <div key={i} className="column is-one-third-desktop is-full-touch">
                                    {/* Assuming story format: [image_url, title, description?] */}
                                    <div className="is-flex is-flex-direction-column">
                                         {story[0] && (
                                             <div className="square-img-container mb-3">
                                                <img className="product-img" src={story[0]} alt={story[1] || 'Commitment'} />
                                            </div>
                                         )}
                                        <p className="has-text-theme-green-1 is-size-6 mt-2 has-text-weight-semibold">{story[1] || 'Our Commitment'}</p>
                                        {story[2] && <p className="is-size-7 has-text-grey mt-1">{story[2]}</p>}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
                 {/* TODO: Add Q&A and User Reviews sections if data exists */}
            </div>
        </div>
    );
};

export default ProductView;
