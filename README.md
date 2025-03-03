# knowMoreQR

knowMoreQR is a full-stack web platform designed to enhance product transparency for eco-conscious consumers in the fashion industry. By integrating physical QR-code tags with a dynamic web application, knowMoreQR bridges the gap between sustainable production practices and informed consumer choices.

## Background

The fashion industry is one of the largest polluters globally, yet many consumers express a desire to support brands that prioritize social and environmental responsibility. However, while studies show that a high percentage of Gen Z would favor sustainable companies, a smaller portion actually engage with corporate CSR efforts. knowMoreQR addresses this disconnect by making sustainability information instantly accessible through a simple QR scan on product tags.

## How It Works

- **For Companies:**  
  - Fill out a comprehensive form detailing the sustainability efforts behind each product line.
  - Print knowMoreQRtags, which include a QR code, to attach to products.
  - Access a dashboard to view and track scan statistics and customer engagement.

- **For Consumers:**  
  - Scan the knowMoreQRtag using a mobile device.
  - Instantly access detailed information about the product’s environmental and social impact.
  - Save scanned items to a wishlist, review product histories, and interact with an AI-powered chatbot.

## Key Features

- **Full-Stack Web Platform:**  
  Built with a React frontend and a Spring Boot backend, the system uses a QR-code–driven interface (via the goqr.me API) to enable companies to share detailed product information with eco-conscious consumers.

- **Secure and Scalable Infrastructure:**  
  The platform utilizes Apache Cassandra (DataStax Astra) for robust product data storage and integrates Spring Data JPA for secure user authentication and authorization.

- **AI-Enhanced Functionality:**  
  Powered by the OpenAI API, features include:
  - Natural-language–based wishlist commands (e.g., add/remove items by color, carbon footprint, etc.)
  - AI-driven OCR for automated product tracking and categorization
  - GPT-powered recommendations for eco-friendly shopping

## Design and User Experience

The design process followed an accelerated version of IDEO’s human-centered design methodology:
- **User Research and Synthesis:**  
  Extensive secondary research and affinity mapping identified key consumer pain points and motivations.
  
- **Prototyping:**  
  Low-fidelity sketches and a concise user flow informed the development of high-fidelity mock-ups in Figma.
  
- **Visual Design:**  
  The UI kit prioritizes readability and usability, employing Krona One for headings and Gotham for body text. A palette of dark turquoise, green, and beige evokes tranquility, trust, and a connection to nature.

## System Architecture and Engineering

- **Frontend:**  
  Developed using React and TypeScript, with integrated Ada bot for enhanced customer interaction.

- **Backend:**  
  Built using Spring Boot and deployed on AWS. The backend connects to an Apache Cassandra database via DataStax Astra and employs Spring Data JPA for improved authentication and user management.

- **Security & Scalability:**  
  The combination of Cassandra for data storage and Spring Security with JPA repositories ensures both robust performance and secure handling of user data.

## Challenges and Learnings

Developing knowMoreQR required overcoming several challenges:
- Learning and integrating new technologies (Typescript, Spring Boot, Cassandra).
- Addressing outdated documentation during the learning process.
- Reconfiguring the authentication system to work seamlessly with both Cassandra and a relational database.
  
These challenges ultimately led to a more robust and reliable platform that enhances transparency and supports sustainable consumer behavior.
