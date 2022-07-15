wmic process where name="cmd.exe" CALL setpriority "Realtime"
wmic process where name="java.exe" CALL setpriority "Realtime"
wmic process where name="conhost.exe" CALL setpriority "Realtime"