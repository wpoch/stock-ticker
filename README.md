# Stock Ticker Service

API Rest service that looks up a fixed number of closing prices of a specific stock.

The API Spec for the exposed service is [stock-ticker-api.yaml](src/main/resources/api/stock-ticker-api.yaml).

## Compile and run

The service is written in Java 17, to install it:

```bash
brew install openjdk
```

The service requires an API Key from AlphaVantage. 
To get a new API Key got to https://www.alphavantage.co/support/#api-key and request one.
Once you have it, create a new file `src/main/resources/application-local.yaml` and add the context of the
key as shown below:

```yaml
alphavantage:
  apiKey: xxxx
```

Once you have the key in place you will be able to run the service with:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=local
```

Once it's running you can send a request with:
```bash
curl http://localhost:8080/price --silent
```

## Container

To build the container run:

```bash
./mvnw package && docker build . -t stock-ticker
```

> NOTE: In a future version the build should happen also in a container image as a multi-stage build.

To run the container:
```
docker run -ti -e SYMBOL=CRM -e NDAYS=5 -e APIKEY=xxxxx --name stock-ticker --rm -P stock-ticker
```

To try it out:

```bash
curl http://$(docker port stock-ticker 8080)/price --silent
```

