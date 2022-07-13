start /b curl -v -X PUT --data "@req1.json" -H "Content-Type:application/json" http://localhost:8080/api/ebay/slow1
timeout /T 2
exit 0
