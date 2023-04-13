# Social Media API
The Social Media API is a mock social media API built using the Spring framework in Java. It allows users to create, update, and delete posts, as well as upload videos which are passed into AssemblyAI's API to get a summary of the video. The posts are also run through OpenAI's API to categorize each post into one of 20 categories. In the near future, search implementation and recommended posts based on past consumed content will be added.

## Technologies Used
The Social Media API is built using the following technologies:

Java

Spring Framework

PostgreSQL

Docker

The database is stored in a Docker container, which is built using the PostgreSQL image. This allows for easy setup and deployment of the API.

### The API also utilizes the following external APIs:

<sub>AssemblyAI's API for video summarization

OpenAI's API for post categorization</sub>


## Getting Started
To get started with the Social Media API, clone the repository to your local machine and run it using a Java IDE such as Eclipse or IntelliJ. You will also need to have Docker installed on your machine to run the PostgreSQL container.

Note: The endpoints for the API are subject to change and may not be fully documented in this README file.
