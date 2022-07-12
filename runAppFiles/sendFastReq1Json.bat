start /b curl -v -X POST --data "@req1.json" -H "Content-Type:application/json" http://localhost:8080/api/ebay/fast1
timeout /T 5
exit 0
