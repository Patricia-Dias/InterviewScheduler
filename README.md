# Interview Scheduler App

To run this project for the first time, you should run all the following commands.
Whenever you want to re-run, you only need to run the command in topic 2.

## Available Scripts

In this directory, to run the App:

### 1. `docker-compose build`

To build docker.

### 2. `docker-compose up`

Runs the server and the database.\
Open [http://localhost:8083/api/scheduler](http://localhost:8083/api/scheduler) to view it in your browser.\
To check the API endpoints go to [Swagger](http://localhost:8083/swagger-ui/index.html#/).\
It also launches the React application.\
Open [Interview Scheduler](http://localhost:3000) to browse the applicatio.

### 3. `chmod u+x loadDatabase.sh`

To allow the excecution of the script.

### 4. `./loadDatabase.sh`

Loads data to the database.
