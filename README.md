open terminal, login to server:
```
ssh root@104.236.14.157
cd campaign-performance-test
sbt run
```

wrk installed on machine, so, you can login on second terminal and run your tests
```
wrk -c 64 -d 10s http://localhost:9080/search_auto
```

or, using remote connection
```
wrk -c 64 -d 10s http://104.236.14.157:9080/search_auto
```

all test files are in project directory
you can upload test data using:
```
curl -X POST -d @test-campaign-1000.json http://104.236.14.157:9080/import_camp --header "Content-Type:application/json"
```

results on my local machine (best after 3 runs):
```
wrk -c 64 -d 10s http://localhost:9080/search_auto
Running 10s test @ http://localhost:9080/search_auto
  2 threads and 64 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     7.88ms    2.82ms  73.22ms   84.03%
    Req/Sec     4.11k   220.88     4.46k    77.50%
  81946 requests in 10.01s, 141.57MB read
Requests/sec:   8182.37
Transfer/sec:     14.14MB
```