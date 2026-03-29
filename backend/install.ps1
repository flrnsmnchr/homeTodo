# Stop the scheduled task if it is running
$taskName = "HomeTodo"
$dest = "C:\Users\Florian Simnacher\Dateien\Development\homeTodo"
$jarFile = "C:\dev\src\homeTodo\backend\build\libs\hometodo-0.0.1-SNAPSHOT.jar"

Stop-ScheduledTask -TaskName $taskName

Start-Sleep -Seconds 3

Remove-Item .\build\libs\hometodo-0.0.1-SNAPSHOT.jar

cd ..\frontend
npm run build
cd ..\backend
.\gradlew clean build

# Create timestamped temp folder inside destination
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$tempFolder = Join-Path $dest $timestamp
New-Item -ItemType Directory -Path $tempFolder | Out-Null

# Copy .db files
Get-ChildItem -Path $dest -Filter *.db -File | Copy-Item -Destination $tempFolder

# Move .log files
Get-ChildItem -Path $dest -Filter *.log -File | Move-Item -Destination $tempFolder

# Move .jar files
Get-ChildItem -Path $dest -Filter *.jar -File | Move-Item -Destination $tempFolder

# Copy new JAR file into destination
Copy-Item -Path $jarFile -Destination $dest -Force

# Start the scheduled task again
Start-ScheduledTask -TaskName $taskName

Write-Host "Install script completed."
