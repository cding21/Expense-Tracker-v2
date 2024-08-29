# Expense Tracker v2 (Backend)
This is the backend for the Expense Tracker v2 project. The project is built using Ktor, a Kotlin framework for building
robust web applications. The project uses MongoDB and PostgreSQL databases to store data. The project also uses JWT for 
user authentication and authorization.


## Set-up
### Database
The project uses MongoDB and PostgreSQL databases. The databases should be set up before running the application.

### Environment Variables
The project uses environment variables to store sensitive information. The `.env` file should be created in the root of 
the project. The `.env` file should be added to the `.gitignore` file to prevent it from being pushed to the repository. 

The `.env` file should contain the following variables:

``` properties
# Example .env.dev file included in the root of the project

# Ktor Application
DEVELOPMENT_MODE=false
API_VERSION=/api/v1

CORS_STATIC_WEBSITE=localhost:3000
CORS_DASHBOARD=localhost:3001

# Database
DB_TYPE=mongo

# MongoDB
DB_MONGO_USER=test
DB_MONGO_PW=test
DB_MONGO_HOST=mongodb://localhost:27017
DB_MONGO_MAX_POOL_SIZE=32
DB_MONGO_DATABASE=test

# PostgreSQL
DB_POSTGRES_USER=test
DB_POSTGRES_PW=test
DB_POSTGRES_URL=http://localhost:5432
DB_POSTGRES_DATABASE=test

# JWT
JWT_ISSUER=expense-tracker
JWT_DOMAIN=cding21.com.au
JWT_SECRET=topsecret
JWT_EXPIRATION=1000
```