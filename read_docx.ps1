Add-Type -AssemblyName System.IO.Compression.FileSystem
$z = [System.IO.Compression.ZipFile]::OpenRead("d:\Price Tracker\Proposal_JAVA.docx")
$e = $z.GetEntry("word/document.xml")
$r = New-Object System.IO.StreamReader($e.Open())
$xmlStr = $r.ReadToEnd()
$r.Close()
$z.Dispose()
$xml = [xml]$xmlStr
$nm = New-Object System.Xml.XmlNamespaceManager($xml.NameTable)
$nm.AddNamespace("w", "http://schemas.openxmlformats.org/wordprocessingml/2006/main")
$paras = $xml.SelectNodes("//w:p", $nm)
foreach ($p in $paras) {
    if ($p.InnerText) { Write-Output $p.InnerText }
}
