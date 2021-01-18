# Market Data [gRPC] Service 

This a redis based market data service, which can be used to service internal market data clients with _real time_ data

### Env Setup

### Bring up the local (dev) env

### Commands
```bash
grpcurl --plaintext localhost:9091 list com.carta.marketdata.service.MarketDataService

grpcurl --plaintext localhost:9091 com.carta.marketdata.service.MarketDataService.heartbeat

grpcurl --plaintext -d '{"symbol" : "AAPL"}' localhost:9091 com.carta.marketdata.service.MarketDataService.spot

grpcurl --plaintext -d '{"row_count": 5, "symbol" : "AAPL"}' localhost:9091 com.carta.marketdata.service.MarketDataService.timeseries

# -- Starting redis with time series:

docker run -p 6379:6379 -it --rm redislabs/redistimeseries
```
