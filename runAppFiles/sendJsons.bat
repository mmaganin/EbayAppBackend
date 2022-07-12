start /b curl -v -X POST --data "@req1.json" -H "Content-Type:application/json" http://localhost:8080/api/ebay/slow1
timeout /T 10
start /b curl -v -X POST --data "@req2.json" -H "Content-Type:application/json" http://localhost:8080/api/ebay/slow2
timeout /T 5
exit 0