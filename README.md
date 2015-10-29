See PerformanceTestProjectNew.pdf for details.

open terminal, login to server:
```
ssh root@104.236.14.157
cd campaign-performance-test
sbt run
```

wrk installed on machine, so, you can login on second terminal and run your tests
```
wrk -c 64 -d 10s http://104.236.14.157:9080/search_auto
```

or, using remote connection
```
wrk -c 64 -d 10s http://104.236.14.157:9080/search_auto
```

you can upload test data file using:
```
curl -X POST -d @test-campaign-1000.json http://104.236.14.157:9080/import_camp --header "Content-Type:application/json"
```

search tests:
```
curl -X POST -d @test-campaign-2.json http://104.236.14.157:9080/import_camp --header "Content-Type:application/json"

curl -X POST -d @test-user-1.json http://104.236.14.157:9080/search --header "Content-Type:application/json"
curl -X POST -d @test-user-2.json http://104.236.14.157:9080/search --header "Content-Type:application/json"
curl -X POST -d @test-user-3.json http://104.236.14.157:9080/search --header "Content-Type:application/json"
curl -X POST -d @test-user-4.json http://104.236.14.157:9080/search --header "Content-Type:application/json"
```

search results on remote machine (best after 3 runs)
```
root@Test-DK:~# wrk -c 64 -d 10s http://104.236.14.157:9080/search_auto
Running 10s test @ http://104.236.14.157:9080/search_auto
  2 threads and 64 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   136.73ms   51.88ms 454.71ms   76.05%
    Req/Sec   236.76     80.88   444.00     65.15%
  4679 requests in 10.01s, 8.12MB read
Requests/sec:    467.25
Transfer/sec:    830.13KB
```

search results on my local machine (best after 3 runs):
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

there are pure server performance test - just request without searching

remote:
```
root@Test-DK:~# wrk -c 64 -d 10s http://104.236.14.157:9080/empty
Running 10s test @ http://104.236.14.157:9080/empty
  2 threads and 64 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    27.18ms   15.85ms 176.49ms   88.15%
    Req/Sec     1.25k   425.53     1.96k    63.82%
  24742 requests in 10.01s, 2.36MB read
Requests/sec:   2471.36
Transfer/sec:    241.34KB
```

local:
```
 $ wrk -c 64 -d 10s http://localhost:9080/empty
Running 10s test @ http://localhost:9080/empty
  2 threads and 64 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.43ms    3.81ms  50.33ms   95.56%
    Req/Sec    42.91k     6.19k   55.33k    77.00%
  855320 requests in 10.02s, 81.57MB read
Requests/sec:  85353.36
Transfer/sec:      8.14MB
```

you can also perform random user search (random user generation instead of incremental on /search_auto)
```
wrk -c 64 -d 10s http://104.236.14.157:9080/search_random
```

