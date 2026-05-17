Set-StrictMode -Version Latest

function Get-ExistingAvdNames {
    param(
        [string]$AvdRoot = (Join-Path $env:USERPROFILE ".android\avd")
    )

    if (-not (Test-Path $AvdRoot)) {
        return @()
    }

    return @(
        Get-ChildItem -Path $AvdRoot -Filter "*.ini" -File |
            ForEach-Object { [System.IO.Path]::GetFileNameWithoutExtension($_.Name) }
    )
}

function Resolve-AvdName {
    param(
        [Parameter(Mandatory = $true)]
        [string]$PreferredName,

        [string[]]$LegacyNames = @(),

        [string[]]$ExistingAvdNames = @()
    )

    if ($ExistingAvdNames -contains $PreferredName) {
        return $PreferredName
    }

    foreach ($legacyName in $LegacyNames) {
        if ($ExistingAvdNames -contains $legacyName) {
            return $legacyName
        }
    }

    return $PreferredName
}

function Parse-AvdDeviceIds {
    param(
        [string[]]$OutputLines
    )

    $ids = foreach ($line in $OutputLines) {
        if ($line -match 'id:\s+\d+\s+or\s+"([^"]+)"') {
            $matches[1]
        }
    }

    return @($ids)
}

function Select-AvdDeviceId {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$PreferredDeviceIds,

        [Parameter(Mandatory = $true)]
        [string[]]$AvailableDeviceIds
    )

    foreach ($deviceId in $PreferredDeviceIds) {
        if ($AvailableDeviceIds -contains $deviceId) {
            return $deviceId
        }
    }

    $available = if ($AvailableDeviceIds) {
        $AvailableDeviceIds -join ", "
    }
    else {
        "<nenhum>"
    }

    throw "Nao encontrei um device profile suportado. Disponiveis: $available"
}

function Assert-NativeCommandSucceeded {
    param(
        [Parameter(Mandatory = $true)]
        [string]$CommandName,

        [Parameter(Mandatory = $true)]
        [int]$ExitCode
    )

    if ($ExitCode -ne 0) {
        throw "$CommandName falhou com exit code $ExitCode."
    }
}

function Resolve-GradleJavaHome {
    param(
        [string]$ConfiguredPath,

        [string[]]$CandidatePaths = @(),

        [scriptblock]$PathExists = {
            param([string]$Path)
            Test-Path -Path $Path -PathType Container
        }
    )

    if ($ConfiguredPath -and (& $PathExists $ConfiguredPath)) {
        return $ConfiguredPath
    }

    $candidates = @()
    foreach ($candidate in $CandidatePaths) {
        if ($candidate -and $candidates -notcontains $candidate) {
            $candidates += $candidate
        }
    }

    $preferredPatterns = @("jdk-17", "jbr", "jdk-21", "jdk-25")
    foreach ($pattern in $preferredPatterns) {
        foreach ($candidate in $candidates) {
            if (($candidate -match [regex]::Escape($pattern)) -and (& $PathExists $candidate)) {
                return $candidate
            }
        }
    }

    foreach ($candidate in $candidates) {
        if (& $PathExists $candidate) {
            return $candidate
        }
    }

    throw "Nao encontrei um Java home valido para o Gradle."
}
