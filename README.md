# Clue Backend Challenge

Service that calculates the running average menstrual cycle length.

## How to run

```
sbt run
```

## Interface

### Posting events

```
POST /events

{
  "user_id": 123456,
  "symptom": 3,
  "timestamp": "2017-04-23T18:25:43.511Z"
}
```

The possible symptoms are:

| SYMPTOM ID  | SYMPTOM         |
| ----------- | --------------  |
| 1           | light bleeding  |
| 2           | medium bleeding |
| 3           | heavy bleeding  |
| 4           | increased focus |
| 5           | cramps          |
| 6           | tender breasts  |

### Querying for the average cycle length

```
GET /cycles/average
```

Response example:

```
{
  "average_cycle": {
    "length": 28.1 
  }
}
```

## Implementation details

### Order of events

This service only handles an ordered stream of events, which means events coming from each user must come with 
increasing timestamps.

If handling data from the past is required, we'll have to store time series of events and aggregate over them.

### State

The state that is kept by the service is represented in `clue.calculator.state.InternalState` case class. For each user 
the service stores 2 timestamps: when the most recent period started, and when the most recent bleeding was tracked. 
In addition two numbers are stored: count of cycles detected for all the users, and their cumulative length. When 
the average cycle length is queried, it's calculated as cumulative length divided by count.

This is the very first version of the service, it keeps all the state in memory with no persistence, and doesn't 
handle parallel requests properly. Moreover it's not possible to scale it by running multiple instances. All these
problems must be solved in the next iteration by handling the state better.

If scalability is not an issue, and we can afford keeping the whole state in memory, I would suggest using Akka 
actors with persistence.

If we actually want to have several instances of this service deployed behind a load balancer, I would suggest to 
persist the state in a database (for example, Postgres) transactionally.

If Kafka is already used in the system, this service can be reimplemeted with Kafka Streams, so that all the state 
required for calculations is persisted on Kafka.

### Period detection

Period phases are consecutive bleeding days (days with any type of bleeding). We derive that two consecutive events 
with bleeding symptoms belong to one period phase if time difference between them is less than 48 hours.

### Other further improvements

- Average cycle length is returned as a `Double`, makes sense to bound its precision.
- Have more exhaustive unit and integration test coverage.
- Add Dockerfile.
- Implement proper logging.
- Store symptom IDs in an external database in case it needs to be shared and updated.