# nubank-authorizer

Clojure solution for nubank authorize challenge. Read STDIN file or string text and output this validation

## Program solution

This program was created using Leiningen as build tool and Midje for the testing.
Because it have a simple business logic, i created a simple architecture due i have no previous experience with clojure and i focused to solve all challenges proposed. 

## Core

Have the main function who starts the verification if an file was used with parameter to start the program then call the service to do the job. 

## Services

Read the entered instruction and verify if is an valid Json. If so, verify if the line begins with an account or transaction text to execute next operation.

## Domain-Account

Create an in-memory object of the Json account, verify if this account already exists in memory and generate violations if needed.

## Domain-Transaction

Execute the verification of business transaction rules:
*   isAccountInitialized - verify if have one valid account in memory
*   isAccountCardActive - verify if the in-memory account have active card
*   isSingleTransaction - verify if have no doubled transaction in same two minutes interval
*   isOnTransactionInterval - verify if have just a maximum of 3 transactions in the same two minutes interval
*   isAccountLimitSufficient - verify if in-memory account have funds to do the transaction
*   doTransaction - subtract the amount in the account

## Util-TransformUtil

Replaces text [TZ] from the Json datetime.
A problem with ISO datetime from the json data and the time limit to send the challenge generate this work around. 

## Logs

The program log some executions and possible exceptions on a log file located in `log/logger1.log`. check it for more execution details

## Tests

I tried to keep the tests as readable and simple as possible, focusing on business rules. I tried to use (flow) to create integration test but was unsuccessful so I opted to complete other features instead.

## Running the program

This program use Leiningen as its build tool and expects a `file` to read. 
Use the `basicTestFile.txt` if an example is needed. 

### Leiningen

`lein run < basicTestFile.txt`

## Testing

The tests were implemented using `midje`. The whole test suite can be run using the `lein midje :autotest` command.

## Production/Deployment

This project can be deployed using JAR or Docker.

### JAR

1. Build an uberjar: `lein uberjar`
2. Run it:

        $ java -jar target/uberjar/nubank-authorizer-0.1.0-SNAPSHOT-standalone.jar < [FILE]    

##Running with docker

Use lein to create an jar with all dependencies

`lein uberjar`

Generate your docker image

    docker build -t nubank-authorize .
Run your image passing an input file

    docker run -i nubank-authorize < basicTestFile.txt

## Input file

The input file from where the application will read the transactions should have one transaction per line and the first line must have an account type for no business issues.
See the example:

```
{"account": {"active-card": true, "available-limit": 100}}
{"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z"}}
{"transaction": {"merchant": "Habbib's", "amount": 10, "time": "2019-02-13T11:00:10.000Z"}}
{"transaction": {"merchant": "Habbib's", "amount": 10, "time": "2019-02-13T11:00:20.000Z"}}
{"transaction": {"merchant": "PizzaPlanet", "amount": 10, "time": "2019-02-13T11:00:30.000Z"}}
{"transaction": {"merchant": "Bazzar Plaza", "amount": 10, "time": "2019-02-13T11:00:40.000Z"}}
{"transaction": {"merchant": "Saraiva Megastore", "amount": 200, "time": "2019-02-13T12:00:41.000Z"}}
{"transaction": {"merchant": "Saraiva Megastore", "amount": 20, "time": "2019-02-13T12:00:42.000Z"}}
```

...

### Known Bugs

Due to restricted time limit for the challenge and my available time to do it, this solutions have some known bugs:

*   The account violations do not concatenate in the vector, just the last violation of each transaction are showed.
*   Transactions are only validated neatly, if there are duplicate transactions within a period of less than two minutes but they are not in direct sequence, the system will not validate.  

...


