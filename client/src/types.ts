export interface Tag {
    id: string; // UUID as string
    companyId: string;
    name: string;
    series: string;
    unitPrice: number;
    salePrice: number;
    description: string;
    colourways: string[][]; // Array of [name, hex, image_url?]
    sizeChart: number[][]; // Array of [size_numeric, measurement1, measurement2, ...]? (Define structure more clearly)
    media: string[]; // Array of image/video URLs
    stories: string[][]; // Array of [title, content] or similar
    materials: string;
    instructions: string;
    itemFeatures: string[];
    views: number;
    saves: number;
    carbonFootprint: number;
    waterUsage: number;
    recycledContentPercent: number;
    wasteReductionPractices: string;
    qAndA: string[][]; // Array of [question, answer]
    userReviews: string[];
} 