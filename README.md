# Getting Started

Project use Spring Boot, Maven-based project. Data are stored in H2 in-memory database.

### Run the project
mvn clean spring-boot:run

### Checking available Slots
curl -X GET "http://localhost:8080/slots" -H 'Content-type:application/json' -d '{"date": "2022-03-19", "serviceType": "COVID_TEST"}'

Available options for serviceType are : FIRST_VISIT, BODY_CHECK, COVID_TEST
Validation on past date has not been done.

### Booking a Slot
curl -X POST "http://localhost:8080/slot" -H 'Content-type:application/json' -d '{"slotId": "1", "serviceType": "COVID_TEST", "date": "2022-03-19"}'

Available options for serviceType are : FIRST_VISIT, BODY_CHECK, COVID_TEST
Date has been added in the list of parameters because the first request is state-less and does not preserve the checked date