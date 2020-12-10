
#
#$params = @{
#  FilePath = [string]::Format("{0}\bin\java.exe",$env:JAVA_HOME)
##  WorkingDirectory = "D:\translation\config\"
#  ArgumentList = @("-Xms8g","-Xmx16g", "-jar", "owl2lpg-translation-cli.jar", "translate","--help")
#  RedirectStandardError = "c:\temp\JavaErrorInTranslation.txt"
#  PassThru = $true
#  Wait = $true
#}
$rc = Start-Process `
    -FilePath "C:\Program Files\OpenJDK\jdk-14.0.1\bin\java.exe" `
    -ArgumentList "-Xms8g","-Xmx16g","-jar",".\owl2lpg-translation-cli-1.0-SNAPSHOT-shaded.jar","translate","-help" `
    -NoNewWindow

#$rc = Start-Process $params

if ($rc.ExitCode -eq 0)
{
  Write-Output "Translation complete"
}
else
{
  Write-Output "Translation failed"
}
