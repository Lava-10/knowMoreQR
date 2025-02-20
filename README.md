# knowMoreQR

knowMoreQR is a full-stack web platform created to enhance product transparency for eco conscious consumers by providing detailed information about products. Currently, companies struggle to share more product details due to the limited space on product tags. With knowMoreQR, companies can easily enter product details on the platform, generate a QR code, and attach it to the product. Consumers can then scan the QR code to access more information. While any company or consumer can use the platform, it primarily promotes eco friendly shopping.

## Background

The fashion industry is one of the largest polluters globally, yet many consumers express a desire to support brands that prioritize social and environmental responsibility. However, while studies show that a high percentage of Gen Z would favor sustainable companies, a smaller portion actually engage with corporate CSR efforts. knowMoreQR addresses this disconnect by making sustainability information instantly accessible through a simple QR scan on product tags.

## How It Works

- **For Companies:**  
  - Fill out a form detailing the sustainability efforts behind each product line.
  - Print knowMoreQRtags, which include a QR code, to attach to products.
  - Access a dashboard to view and track scan statistics and customer engagement.

- **For Consumers:**  
  - Scan the knowMoreQRtag using a mobile device.
  - Instantly access detailed information about the product’s environmental and social impact.
  - Save scanned items to a wishlist, review product histories, and interact with an AI-powered chatbot.

## Key Features

- **Full-Stack Web Platform:**  
  Built with a React frontend and a Spring Boot backend, the system uses a QR code driven interface (via the goqr.me API) to enable companies to share detailed product information with eco conscious consumers.

- **Secure and Scalable Infrastructure:**  
  The platform uses Apache Cassandra (DataStax Astra) for robust product data storage and integrates Spring Data JPA for secure user authentication and authorization.

- **AI Enhanced Functionality:**  
  Powered by the OpenAI API, features include:
  - Natural language based wishlist commands (e.g., add/remove items by color, carbon footprint, etc.)
  - AI driven OCR for automated product tracking and categorization
  - GPT powered recommendations for eco friendly shopping

## System Architecture and Engineering

- **Frontend:**  
  Developed using React and TypeScript, with integrated Ada bot for enhanced customer interaction.

- **Backend:**  
  The backend connects to an Apache Cassandra database via DataStax Astra and employs Spring Data JPA for improved authentication and user management.

- **Security & Scalability:**  
  The combination of Cassandra for data storage and Spring Security with JPA repositories ensures both robust performance and secure handling of user data.

## Challenges and Learnings

Developing knowMoreQR required overcoming several challenges:
- Learning and integrating new technologies (Typescript, Spring Boot, Cassandra).
- Addressing outdated documentation during the learning process.
- Reconfiguring the authentication system to work seamlessly with both Cassandra and a relational database.
  
These challenges ultimately led to a more robust and reliable platform that enhances transparency and supports sustainable consumer behavior.
