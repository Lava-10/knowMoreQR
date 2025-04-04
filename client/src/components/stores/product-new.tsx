import React, { useState, FormEvent } from 'react';
import axios from 'axios';
import { Link, Redirect, useHistory } from 'react-router-dom';
import '../../assets/css/store-dashboard.scss';
import Sidebar from './sidebar';
import tagPreview from '../../assets/img/tag-preview.png';
import { Tag } from '../../types';
import { useAuth } from '../../context/AuthContext';

interface ProductNewProps {
}

interface knowMoreQRtag {
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

  // Q&A as an array of objects
  qAndA: { question: string; answer: string }[];
  // userReviews as an array of strings, each a testimonial
  userReviews: string[];
}

interface ProductNewState {
  redirect: string;
  colorsCount: number;
  knowMoreQRtag: knowMoreQRtag;
}

function Colorway(clickHandler: (e: React.MouseEvent<HTMLButtonElement>) => void, count: number) {
  return(
    <div className="colors columns is-mobile w-100 mx-0 is-flex-wrap-wrap" key={count}>
      <div className="column is-4-desktop is-8-touch">
        <div className="field">COLOUR NAME</div>
        <input id="colorName" className="input is-normal" type="text" placeholder="Black"/>
      </div>
      <div className="column is-2-desktop is-4-touch">
        <div className="field">PICKER</div>
        <input id="colorPicker" className="input is-normal p-1" defaultValue="#83B7A1" type="color"/>
      </div>
      <div className="column is-4-desktop is-8-touch">
        <div className="field">IMAGE URL</div>
        <input id="colorImageUrl" className="input is-normal" type="text" placeholder="url.image.com"/>
      </div>
      <div className="column is-2-desktop is-4-touch">
        <div className="field">DELETE</div>
        <button className="button border-radius-8 is-danger is-light px-3" onClick={clickHandler}>
          <i className="fa fa-times"></i>
        </button>
      </div>
    </div>
  );
}

const ProductNew: React.FC = () => {
  const history = useHistory();
  const { userType, userId: companyLoginId } = useAuth();

  const [formData, setFormData] = useState<TagFormData>({
    name: '',
    series: '',
    unitPrice: '',
    salePrice: '',
    description: '',
    materials: '',
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    if (!formData.name || !formData.series || !formData.unitPrice || !formData.salePrice) {
      setError('Please fill in all required fields (Name, Series, Prices).');
      setIsLoading(false);
      return;
    }

    const companyIdForTag = "temp-company-id";

    const payload: Partial<Tag> = {
      ...formData,
      companyId: companyIdForTag,
      unitPrice: parseFloat(formData.unitPrice.toString()),
      salePrice: parseFloat(formData.salePrice.toString()),
    };

    try {
      await axios.post<Tag>('/tags', payload);
      history.push('/sell/dashboard');
    } catch (err: any) {
      console.error("Error creating product:", err);
      setError(`Failed to create product. ${err.response?.data?.message || err.message || 'Please try again.'}`);
    } finally {
      setIsLoading(false);
    }
  };

  if (userType !== 'company') {
    return <div className="container mt-5 pt-5"><p>Access Denied. Only companies can add products.</p></div>;
  }

  return (
    <div className="is-flex is-flex-direction-row is-flex-direction-column-touch">
      <Sidebar/>
      <form className="main is-flex is-flex-direction-column is-align-items-start w-100 pt-6 px-custom-touch" onSubmit={handleSubmit}>
        <div className="is-flex w-100 is-flex-direction-row is-flex-direction-column-touch is-justify-content-space-between is-align-items-center">
          <div className="is-flex is-flex-direction-row is-align-items-center my-3 ml-5">
            <Link to="/sell/dashboard">
              <i className="fa fa-lg mr-4 fa-caret-square-left has-text-grey-lighter"></i>
            </Link>
            <div className="title is-size-5 has-text-weight-normal mb-1">Add Product</div>
          </div>
          <div className="is-flex is-flex-direction-row is-align-items-center my-3">
            <button className="button custom-button-beige py-2 mx-2"><span className="mt-1 mx-3">DISCARD</span></button>
            <button className="button custom-button py-2 mx-2"><span className="mt-1 mx-3">SAVE</span></button>
          </div>
        </div>
        <div className="mt-2 columns w-100 mx-0 is-flex-wrap-wrap">
          <div className="column is-full is-two-thirds-fullhd">
            <div className="has-text-left has-text-weight-light has-text-theme-green-1 mt-2 mb-5 mx-2">
              <span className="has-text-weight-bold">Step 1: </span>
              Tell us about your product
            </div>
            <div className="mx-2 has-background-theme-beige border-radius-8 mb-6">
              <div className="p-6 has-text-left">
                <div className="subtitle">
                  PRODUCT INFORMATION
                </div>
                <div className="mb-5">
                  <div className="field">NAME</div>
                  <input className="input is-normal" type="text" name="name" value={formData.name} onChange={handleChange} placeholder="Ribbed Knit Henley"/>
                </div>
                <div className="mb-5">
                  <div className="field">SERIES</div>
                  <input className="input is-normal" type="text" name="series" value={formData.series} onChange={handleChange} placeholder="The ReNew Collection"/>
                </div>
                <div className="columns is-mobile w-100 mx-0 is-flex-wrap-wrap mb-5">
                  <div className="column pl-0 pr-1 is-half-desktop is-full-touch">
                    <div className="field">UNIT PRICE</div>
                    <input className="input is-normal" name="unitPrice" type="number" step="0.01" value={formData.unitPrice} onChange={handleChange} placeholder="₹0.00"/>
                  </div>
                  <div className="column pr-0 pl-1 is-half-desktop is-full-touch">
                    <div className="field">SALE PRICE (optional)</div>
                    <input className="input is-normal" name="salePrice" type="number" step="0.01" value={formData.salePrice} onChange={handleChange} placeholder="₹0.00"/>
                  </div>
                </div>
                <div className="mb-1">
                  <div className="field">DESCRIPTION</div>
                  <textarea className="textarea is-normal" name="description" value={formData.description} onChange={handleChange} placeholder=""/>
                </div>
              </div>
            </div>

            <div className="has-text-left has-text-weight-light has-text-theme-green-1 mt-2 mb-5 mx-2">
              <span className="has-text-weight-bold">Step 2: </span>
              Enter colors, sizes, and upload media
            </div>
            <div className="mx-2 has-background-theme-beige border-radius-8 mb-6">
              <div className="p-6 has-text-left">
                <div className="subtitle">
                  VARIANTS & MEDIA
                </div>
                <div className="header py-2">COLOURWAYS</div>
                <hr/>
                <div className="is-flex is-align-items-center mb-6">
                  <button className="button border-radius-8 is-light px-3 mx-3">
                    <i className="fa fa-plus has-text-grey-light"></i>
                  </button>
                  <div className="has-text-grey-light is-size-6 has-text-weight-light">Add additional colors</div>
                </div>

                <div className="header py-2">SIZING CHART</div>
                <hr/>
                <div className="has-text-grey-light is-size-6 has-text-weight-light">Enter all corresponding measurements in inches</div>
                <table className="table is-hoverable mt-5 mb-6 border-radius-8">
                  <tbody>
                    <tr className="has-background-grey-lighter">
                      <td></td>
                      <td className="pt-3 px-4"><div className="pt-2 has-text-centered is-size-6">XS</div></td>
                      <td className="pt-3 px-4"><div className="pt-2 has-text-centered is-size-6">S</div></td>
                      <td className="pt-3 px-4"><div className="pt-2 has-text-centered is-size-6">M</div></td>
                      <td className="pt-3 px-4"><div className="pt-2 has-text-centered is-size-6">L</div></td>
                      <td className="pt-3 px-4"><div className="pt-2 has-text-centered is-size-6">XL</div></td>
                    </tr>
                    <tr>
                      <td className="pt-3 px-4 has-background-grey-lighter has-text-centered"><div className="pt-2 is-size-6">CHEST</div></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                    </tr>
                    <tr>
                      <td className="pt-3 px-4 has-background-grey-lighter has-text-centered"><div className="pt-2 is-size-6">WAIST</div></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                    </tr>
                    <tr>
                      <td className="pt-3 px-4 has-background-grey-lighter has-text-centered"><div className="pt-2 is-size-6">SLEEVE</div></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                      <td><input className="input is-normal" type="number"/></td>
                    </tr>
                  </tbody>
                </table>

                <div id="mediaLinks" className="my-6">
                  <div className="header py-2">ADDITIONAL MEDIA</div>
                  <hr/>
                  <div className="header is-size-7 mb-3">ENTER LINKS TO ANY ADDITIONAL PHOTOS YOU WOULD LIKE TO FEATURE</div>
                  <input className="input is-normal my-2" type="text" placeholder="https://featuredphoto.com"/>
                  <input className="input is-normal my-2" type="text" placeholder="Thttps://featuredphoto.com"/>
                  <input className="input is-normal my-2" type="text" placeholder="https://featuredphoto.com"/>
                </div>

                <div id="stories" className="my-6">
                  <div className="header py-2">ADDITIONAL FEATURE STORIES</div>
                  <hr/>
                  <div className="header is-size-7 mb-5">ENTER LINKS TO ANY ARTICLES, COMMITMENTS, OR PRESS RELEASES YOU WOULD LIKE TO FEATURE, ALONG WITH A CAPTION.</div>
                  <div className="columns is-mobile w-100 mx-0 is-flex-wrap-wrap">
                    <div className="column py-2 pl-0 pr-1 is-7-desktop is-full-touch">
                      <input className="input is-normal" type="text" placeholder="https://featuredarticle.com"/>
                    </div>
                    <div className="column py-2 pr-0 pl-1 is-5-desktop is-full-touch">
                      <input className="input is-normal" type="text" placeholder="Caption"/>
                    </div>
                  </div>
                  <div className="columns is-mobile w-100 mx-0 is-flex-wrap-wrap">
                    <div className="column py-2 pl-0 pr-1 is-7-desktop is-full-touch">
                      <input className="input is-normal" type="text" placeholder="https://featuredarticle.com"/>
                    </div>
                    <div className="column py-2 pr-0 pl-1 is-5-desktop is-full-touch">
                      <input className="input is-normal" type="text" placeholder="Caption"/>
                    </div>
                  </div>
                  <div className="columns is-mobile w-100 mx-0 is-flex-wrap-wrap">
                    <div className="column py-2 pl-0 pr-1 is-7-desktop is-full-touch">
                      <input className="input is-normal" type="text" placeholder="https://featuredarticle.com"/>
                    </div>
                    <div className="column py-2 pr-0 pl-1 is-5-desktop is-full-touch">
                      <input className="input is-normal" type="text" placeholder="Caption"/>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="has-text-left has-text-weight-light has-text-theme-green-1 mt-2 mb-5 mx-2">
              <span className="has-text-weight-bold">Step 3: </span>
              How was it made and how should we care?
            </div>
            <div className="mx-2 has-background-theme-beige border-radius-8 mb-6">
              <div className="p-6 has-text-left">
                <div className="subtitle">
                  CARE & MANUFACTURING
                </div>
                <div className="mb-5">
                  <div className="field">MATERIALS</div>
                  <textarea className="textarea is-normal" name="materials" value={formData.materials} onChange={handleChange} placeholder=""/>
                </div>
                <div className="mb-5">
                  <div className="field">WASH INSTRUCTIONS</div>
                  <textarea className="textarea is-normal" name="instructions" value={formData.instructions} onChange={handleChange} placeholder=""/>
                </div>
                <div id="itemFeatures">
                  <div className="mb-5">
                    <div className="field">ITEM FEATURES</div>
                    <textarea className="textarea is-normal" placeholder="Enter a short description about your product's features."/>
                  </div>
                  <div className="mb-2">
                    <div className="field">FEATURE #1</div>
                    <input className="input is-normal" type="text" placeholder="Title of feature #1"/>
                  </div>
                  <div className="mb-5">
                    <textarea className="textarea is-normal"/>
                  </div>
                  <div className="mb-2">
                    <div className="field">FEATURE #2</div>
                    <input className="input is-normal" type="text" placeholder="Title of feature #2"/>
                  </div>
                  <div className="mb-5">
                    <textarea className="textarea is-normal"/>
                  </div>
                  <div className="mb-2">
                    <div className="field">FEATURE #3</div>
                    <input className="input is-normal" type="text" placeholder="Title of feature #2"/>
                  </div>
                  <div className="mb-5">
                    <textarea className="textarea is-normal"/>
                  </div>
                </div>
              </div>
            </div>
            <div className="has-text-left has-text-weight-light has-text-theme-green-1 mt-2 mb-5 mx-2">
              <span className="has-text-weight-bold">Step 4: </span>
              Step 4: Preview your knowMoreQRtag and customize
            </div>
            <div className="mx-2 has-background-theme-beige border-radius-8">
              <div className="p-6 has-text-left">
                <div className="subtitle">
                  knowMoreQRTAG PREVIEW
                </div>
                <img src={tagPreview} alt="knowMoreQRtag product preview"/>
              </div>
            </div>
            <div className="w-100 is-flex is-justify-content-start">
              <button className="button custom-button py-2 mx-2 my-6" type="submit" disabled={isLoading}><span className="mt-1 mx-3">CREATE knowMoreQRTAG</span></button>
            </div>
          </div>
          <div className="column is-full is-one-third-fullhd pt-3 mt-2">
            <div className="mx-2 has-background-theme-grey border-radius-8 mt-6">
              <div className="p-6 has-text-left">
                <div className="subtitle">
                  TAG STATUS
                </div>
                <div className="control">
                  <label className="radio is-flex is-align-items-center">
                    <input className="radio-custom" type="radio" name="answer"/>
                    <span className="has-text-theme-green-1 ml-3 mt-1">ACTIVE</span>
                  </label>
                  <div className="mt-2 ml-5 pl-3 has-text-grey-light is-size-7">This QR code will lead to your product's page.</div>
                  <br/>
                  <label className="radio is-flex is-align-items-center">
                    <input className="radio-custom" type="radio" name="answer"/>
                    <span className="has-text-theme-green-1 ml-3 mt-1">DRAFT</span>
                    <br/>
                  </label>
                  <div className="mt-2 ml-5 pl-3 has-text-grey-light is-size-7">This QR code will not be scannable.</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
};

export default ProductNew;
