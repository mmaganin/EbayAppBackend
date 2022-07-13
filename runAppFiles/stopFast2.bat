start /b curl -v -X PUT --data "@req2.json" -H "Content-Type:application/json" http://localhost:8080/api/ebay/fast2
timeout /T 2
exit 0
