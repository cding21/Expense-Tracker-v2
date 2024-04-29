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

```
# Example .env file to include in the root of the project

# Ktor Application
DEVELOPMENT_MODE=

# Database
DB_TYPE=

# MongoDB
DB_MONGO_USER=
DB_MONGO_PW=
DB_MONGO_HOST=
DB_MONGO_MAX_POOL_SIZE=
DB_MONGO_DATABASE=

# PostgreSQL
DB_POSTGRES_USER=
DB_POSTGRES_PW=
DB_POSTGRES_URL=
DB_POSTGRES_DATABASE=

# JWT
JWT_ISSUER=
JWT_DOMAIN=
JWT_SECRET=
```