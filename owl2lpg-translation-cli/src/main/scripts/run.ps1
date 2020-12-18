
$rc = Start-Process `
    -FilePath "C:\Program Files\OpenJDK\jdk-14.0.1\bin\java.exe" `
    -ArgumentList "-Xms8g","-Xmx16g","-jar",".\owl2lpg-translation-cli-1.0-SNAPSHOT-shaded.jar","translate","-help" `
    -NoNewWindow

if ($rc.ExitCode -eq 0)
{
  Write-Output "Translation complete"
}
else
{
  Write-Output "Translation failed"
}
