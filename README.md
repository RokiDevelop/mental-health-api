# Mental Health API  

Mental Health API is a RESTful web service designed to provide tools for mental health support and analytics. 
It is built with **Java 17**, **Spring Boot**, and leverages OAuth2 for authentication, with optional integration with VK.com. 
The project supports data storage in either local storage or Yandex Cloud.  

## Features  
- OAuth2-based authentication and authorization (Basic & VK.com).
- PostgreSQL database integration.  
- Liquibase for database migrations.  
- QueryDSL for type-safe queries.  
- Redis support for caching.  
- Dockerized deployment.  
- Optional Yandex Cloud storage support.  

## Prerequisites  
- Java 17  
- Docker and Docker Compose  
- PostgreSQL  
- Redis  

## Setup and Run  

### Using Docker  
1. Clone the repository:  
   ```bash
   $ git clone https://github.com/yourusername/mental-health.git  
   $ cd mental-health
   
2. Configure the env file with your database and storage credentials (focus on the "env.example" file)
   
3. Build and run the application with Docker:
   ```bash
   $ docker compose up --build
   
### Technologies Used
- Java 17
- Spring Boot
- PostgreSQL
- Liquibase
- OAuth2
- Yandex Cloud
- Redis
- Docker
