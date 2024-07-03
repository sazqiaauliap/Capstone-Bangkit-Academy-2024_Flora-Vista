# Plant Tips API

## Overview
This project is a REST API designed to fetch tips and guides about various plants from specified URLs and provide this data through a GET request.


## Prerequisites
- Node.js (version 20 or higher)
- Visual Studio Code (VSCode)

Install dependencies:
npm install

Create a .env file:
touch .env

Add the following content to the .env file:
PORT=8080


## Running the Server
Open the project in VSCode:

Open Visual Studio Code.
Navigate to File > Open Folder and select the my-plant-tips-api folder.
Run the server:

Open server.js in the editor.
Press F5 or go to Run > Start Debugging to start the server.
The server will start on the specified port (8080 by default).

## API Endpoint
Get Plant Tips
URL: /guides
Method: GET
Description: Fetches a list of tips and guides about various plants.

Request
No request body or parameters are needed for this endpoint.

Response
Status: 200 OK
