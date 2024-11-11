# Receipt-Processor-Challenge

Take-home backend challenge for https://github.com/fetch-rewards

See [docs](docs) for the [instructions](docs/Instructions.md) and [API spec](docs/api.yml),

This challenge was implemented as a REST API with Java 17, Spring Boot and in memory data repository. The app and tests
are able to run using Docker.

The app  utilizes the DTO and Command pattern to cleanly award points to each receipt following all the point
rules. And an options object was also included that can be easily extended to customize each rule, or even disable them.

All classes are unit tested with JUnit and Integration tests are enabled to check the flow of
from controller -> service -> repository

The unit test cases and integration test cases are ran at the time of building the docker image.
## Development Requirements

* Java 17
* Docker & docker-compose
* Preferably IntelliJ IDE
    * Lombok annotations included on IntelliJ, otherwise will need to install plugins for IDE
* Maven 3.9.0, or utilize the maven wrapper (mvnw, or mvnw.cmd for Windows)

## Installation && Running

1. **Installation**:
    ```
    git clone https://github.com/JayanthKarupothula/fetchrewards.git
    cd ReceiptProcessor 
    docker build -t receiptprocessor:latest .
    ```

2. **Running the Application**:

    
    docker run -p 8080:8080 receiptprocessor:latest
    

Your application should now be running on `http://localhost:8080/`

## Testing with Postman
 1. open postman and paste below curl command for post api to process and save the receipt

    ```
    curl --location 'localhost:8080/receipts/process' \
    --header 'Content-Type: application/json' \
    --data '{
    "retailer": "M&M Corner Market",
    "purchaseDate": "2022-03-20",
    "purchaseTime": "14:33",
    "items": [
    {
    "shortDescription": "Gatorade",
    "price": "2.25"
    },{
    "shortDescription": "Gatorade",
    "price": "2.25"
    },{
    "shortDescription": "Gatorade",
    "price": "2.25"
    },{
    "shortDescription": "Gatorade",
    "price": "2.25"
    }
    ],
    "total": "9.00"
    }'

    ```
    2. open postman and paste below curl command for get api to get the points. Use the id generated from post api

        ```
        curl --location 'localhost:8080/receipts/7a15a566-06da-4919-aeec-e9eb4868186f/points'
        ```


