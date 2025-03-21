import React from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
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
}

class ProductIndex extends React.Component<ProductsProps, ProductsState> {
  constructor(props: ProductsProps) {
    super(props);

    this.state = {
      totalScans: 0,
      knowMoreQRtags: []
    }
  }

  componentDidMount() {
    axios.get("https://api.knowMoreQR.info/tags")
    .then(res => {
      const knowMoreQRtags = res.data.data;
      const arr = Object.entries(knowMoreQRtags).map((e) => ( { [e[0]]: e[1] } ));
      const knowMoreQRtagsNew : any[] = [];

      let scanCount = 0;

      arr.forEach(function(el, i) {
        let key = Object.keys(arr[i]);
        let obj : any;
        obj = el[key.toString()];

        let newTag = {
          id: key,
          name: obj.name,
          series: obj.series,
          unitPrice: obj.unitPrice,
          salePrice: obj.salePrice,
          description: obj.description,
          colourways: obj.colourways,
          sizeChart: obj.sizeChart,
          media: obj.media,
          stories: obj.stories,
          materials: obj.materials,
          instructions: obj.instructions,
          itemFeatures: obj.itemFeatures,
          saves: obj.saves,
          views: obj.views,
          qr: `https://api.qrserver.com/v1/create-qr-code/?data=knowMoreQR.info/#/buy/dashboard/${key}&size=1000x1000`
        }
        knowMoreQRtagsNew.push(newTag);
        scanCount += newTag.views;
      });
      this.setState({knowMoreQRtags: knowMoreQRtagsNew});
      this.setState({totalScans: scanCount});
    });
  }

  render() {
    return(
      <div className="is-flex is-flex-direction-row is-flex-direction-column-touch">
        <Sidebar/>
        <div className="main is-flex is-flex-direction-column w-100 pt-6 px-custom-touch">
          <div className="columns w-100 mx-0 is-mobile is-flex-wrap-wrap">
            <StatCard number={this.state.knowMoreQRtags.length.toString()} text="knowMoreQRTAGS CREATED" icon="fa-tags"/>
            <StatCard number={this.state.totalScans.toString()} text="knowMoreQRTAGS SCANNED" icon="fa-qrcode"/>
            <StatCard number="-21%" text="REDUCED PACKAGING" icon="fa-leaf"/>
          </div>
          <div className="is-flex is-flex-direction-column is-align-items-start mt-5 mx-2">
            <div className="is-flex is-flex-direction-column-touch is-justify-content-space-between w-100">
              <div className="title is-size-4 mt-2 mb-5 has-text-theme-green-1 has-text-weight-normal">
                My knowMoreQRtags
              </div>
              <Link to="/sell/dashboard/new">
                <button className="button custom-button py-2">
                  <span className="mt-1 mx-3">CREATE A knowMoreQRTAG</span>
                </button>
              </Link>
            </div>
            <div className="is-flex my-3">
              <div className="dropdown mr-4">
                <div className="dropdown-trigger">
                  <button className="button custom-filter">
                    <span className="mt-1 mr-3">SORT BY</span>
                    <span className="icon is-small">
                      <i className="fas fa-caret-down"></i>
                    </span>
                  </button>
                </div>
              </div>
              <div className="dropdown">
                <div className="dropdown-trigger">
                  <button className="button custom-filter">
                    <span className="mt-1 mr-3">FILTERS</span>
                    <span className="icon is-small">
                      <i className="fas fa-caret-down"></i>
                    </span>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div className="mt-2 columns w-100 mx-0 is-mobile is-flex-wrap-wrap">
          {this.state.knowMoreQRtags.map(function(tag, i) {
            return(
              <ProductCard id={tag.id} key={i} name={tag.name} image={tag.colourways[0][2]} price={tag.unitPrice.toString()} qr={tag.qr} scanned={tag.views} wishlisted={tag.saves} purchased={86}/>
            );
          })}
          </div>
          <div className="is-flex is-justify-content-center-touch mx-2 mb-6">
            <button className="button custom-button py-2 mx-2">
              <span className="mt-1 mx-3">SHOW ALL TAGS</span>
            </button>
          </div>
        </div>
      </div>
    );
  }
}

export default ProductIndex;
