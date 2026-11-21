Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead('GERMAN BASICS A1.docx')
$entry = $zip.GetEntry('word/document.xml')
$stream = $entry.Open()
$reader = New-Object System.IO.StreamReader($stream)
$xml = $reader.ReadToEnd()
$reader.Close()
$stream.Close()
$zip.Dispose()
$xml = $xml -replace '<w:p[^>]*>', "`n<w:p>"
$xml = $xml -replace '<w:t[^>]*>([^<]*)</w:t>', '$1'
$xml = $xml -replace '<[^>]+>', ''
$xml = $xml -replace '\s+', ' '
Write-Host $xml