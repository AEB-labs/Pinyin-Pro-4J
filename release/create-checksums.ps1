<#
.SYNOPSIS
    Erzeugt für alle Dateien im aktuellen Verzeichnis .md5-, .sha1- und .asc-Dateien.

.DESCRIPTION
    - <Datei>.md5  : MD5-Prüfsumme (Hex, klein geschrieben)
    - <Datei>.sha1 : SHA1-Prüfsumme (Hex, klein geschrieben)
    - <Datei>.asc  : Detached, ASCII-armored PGP-Signatur (benötigt gpg im PATH)

    Bereits vorhandene Prüfsummen-/Signaturdateien werden nicht erneut verarbeitet.

.PARAMETER GpgKey
    Optional: Schlüssel-ID/E-Mail des zu verwendenden PGP-Schlüssels.

.EXAMPLE
    .\create-checksums.ps1
    .\create-checksums.ps1 -GpgKey "me@example.com"
#>
param(
    [string]$GpgKey
)

$ErrorActionPreference = 'Stop'

$gpg = Get-Command gpg -ErrorAction SilentlyContinue
if (-not $gpg) {
    Write-Warning "gpg wurde nicht gefunden - .asc-Dateien werden nicht erzeugt."
}

$skipExtensions = @('.md5', '.sha1', '.asc')

Get-ChildItem -File | Where-Object { $skipExtensions -notcontains $_.Extension.ToLower() } | ForEach-Object {
    $file = $_
    Write-Host "Verarbeite $($file.Name)"

    foreach ($algorithm in @(@{ Name = 'MD5'; Ext = 'md5' }, @{ Name = 'SHA1'; Ext = 'sha1' })) {
        $hash = (Get-FileHash -Path $file.FullName -Algorithm $algorithm.Name).Hash.ToLower()
        $target = "$($file.FullName).$($algorithm.Ext)"
        [System.IO.File]::WriteAllText($target, $hash, (New-Object System.Text.UTF8Encoding($false)))
    }

    if ($gpg) {
        $gpgArgs = @('--batch', '--yes', '--armor', '--detach-sign')
        if ($GpgKey) { $gpgArgs += @('--local-user', $GpgKey) }
        $gpgArgs += @('--output', "$($file.FullName).asc", $file.FullName)
        & gpg @gpgArgs
        if ($LASTEXITCODE -ne 0) {
            Write-Warning "gpg-Signatur für $($file.Name) fehlgeschlagen."
        }
    }
}
