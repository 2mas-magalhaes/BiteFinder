param(
    [switch]$RestartEmulator
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $repoRoot "run-bytefinder.helpers.ps1")

function Write-Step {
    param([string]$Message)
    Write-Host "`n==> $Message" -ForegroundColor Cyan
}

function Get-PhpExe {
    $candidates = @(
        (Get-Command php -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source -ErrorAction SilentlyContinue),
        "C:\Users\tomas\AppData\Local\Microsoft\WinGet\Packages\PHP.PHP.8.3_Microsoft.Winget.Source_8wekyb3d8bbwe\php.exe",
        "C:\php\php.exe"
    ) | Where-Object { $_ }

    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    throw "Nao encontrei php.exe."
}

function Get-SdkDir {
    param([string]$LocalPropertiesPath)

    if (Test-Path $LocalPropertiesPath) {
        $sdkLine = Get-Content $LocalPropertiesPath | Where-Object { $_ -like "sdk.dir=*" } | Select-Object -First 1
        if ($sdkLine) {
            $value = $sdkLine.Substring("sdk.dir=".Length).Replace("\\", "\")
            if (Test-Path $value) {
                return $value
            }
        }
    }

    $candidates = @(
        $env:ANDROID_SDK_ROOT,
        $env:ANDROID_HOME,
        "C:\Users\tomas\AppData\Local\Android\Sdk"
    ) | Where-Object { $_ }

    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    throw "Nao encontrei o Android SDK."
}

function Get-FreeBytesForPath {
    param([string]$Path)

    $root = [System.IO.Path]::GetPathRoot($Path)
    if (-not $root) {
        return 0
    }

    $driveName = $root.TrimEnd("\").TrimEnd(":")
    $drive = Get-PSDrive -Name $driveName -PSProvider FileSystem -ErrorAction SilentlyContinue
    if (-not $drive) {
        return 0
    }

    return $drive.Free
}

function Resolve-AvdHome {
    param(
        [string]$RepoRoot,
        [long]$MinimumFreeBytes = 8GB
    )

    if ($env:ANDROID_AVD_HOME) {
        return $env:ANDROID_AVD_HOME
    }

    $defaultAvdHome = Join-Path $env:USERPROFILE ".android\avd"
    if ((Get-FreeBytesForPath -Path $defaultAvdHome) -ge $MinimumFreeBytes) {
        return $defaultAvdHome
    }

    $repoDrive = [System.IO.Path]::GetPathRoot($RepoRoot)
    $candidates = @()
    if ($repoDrive) {
        $candidates += (Join-Path $repoDrive "Android\avd")
    }

    $candidates += Get-PSDrive -PSProvider FileSystem |
        Sort-Object Free -Descending |
        ForEach-Object { Join-Path $_.Root "Android\avd" }

    foreach ($candidate in ($candidates | Select-Object -Unique)) {
        if ((Get-FreeBytesForPath -Path $candidate) -ge $MinimumFreeBytes) {
            return $candidate
        }
    }

    return $defaultAvdHome
}

function Ensure-AvdHome {
    param([string]$AvdHome)

    if (-not (Test-Path $AvdHome)) {
        New-Item -ItemType Directory -Path $AvdHome -Force | Out-Null
    }

    $env:ANDROID_AVD_HOME = $AvdHome
    Write-Host "Android AVD home: $AvdHome" -ForegroundColor DarkCyan
}

function Ensure-LocalProperties {
    param(
        [string]$Path,
        [string]$SdkDir
    )

    $sdkEscaped = $SdkDir.Replace("\", "\\")
    $defaults = [ordered]@{
        "sdk.dir" = $sdkEscaped
        "API_BASE_URL" = "http://10.0.2.2:8000/"
        "MAPS_API_KEY" = ""
        "ADMOB_APP_ID" = "ca-app-pub-3940256099942544~3347511713"
    }

    $lines = @()

    if (Test-Path $Path) {
        $lines = @(Get-Content $Path -ErrorAction Stop)
    }

    $seen = @{}
    $updated = foreach ($line in $lines) {
        if ($line -match "^\s*([^#][^=]+?)\s*=(.*)$") {
            $key = $matches[1].Trim()
            $value = $matches[2]
            $seen[$key] = $true

            if ($key -eq "sdk.dir") {
                "sdk.dir=$sdkEscaped"
                continue
            }

            if ($key -eq "API_BASE_URL" -and [string]::IsNullOrWhiteSpace($value)) {
                "API_BASE_URL=$($defaults["API_BASE_URL"])"
                continue
            }

            if ($key -eq "ADMOB_APP_ID" -and [string]::IsNullOrWhiteSpace($value)) {
                "ADMOB_APP_ID=$($defaults["ADMOB_APP_ID"])"
                continue
            }
        }

        $line
    }

    foreach ($key in $defaults.Keys) {
        if (-not $seen.ContainsKey($key)) {
            $updated += "$key=$($defaults[$key])"
        }
    }

    if ((@($lines) -join "`n") -eq (@($updated) -join "`n")) {
        return
    }

    try {
        Set-Content -Path $Path -Value $updated -Encoding ASCII
    }
    catch {
        Write-Host "Aviso: nao consegui atualizar $Path porque esta em uso por outro processo. Feche o editor/bloqueio se faltar alguma chave." -ForegroundColor Yellow
        Write-Host "Detalhe: $($_.Exception.Message)" -ForegroundColor DarkYellow
    }
}

function Get-GradleJavaHomeSetting {
    param([string]$GradlePropertiesPath)

    if (-not (Test-Path $GradlePropertiesPath)) {
        return $null
    }

    $line = Get-Content $GradlePropertiesPath |
        Where-Object { $_ -like "org.gradle.java.home=*" } |
        Select-Object -First 1

    if (-not $line) {
        return $null
    }

    return $line.Substring("org.gradle.java.home=".Length).Replace("\\", "\")
}

function Ensure-GradleJavaHome {
    param([string]$GradlePropertiesPath)

    $configuredJavaHome = Get-GradleJavaHomeSetting -GradlePropertiesPath $GradlePropertiesPath
    $candidatePaths = @(
        $env:JAVA_HOME,
        "C:\Program Files\Android\Android Studio\jbr",
        "C:\Program Files\Android\Android Studio\jre"
    )

    $candidatePaths += @(Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName)
    $candidatePaths += @(Get-ChildItem "C:\Program Files\Java" -Directory -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName)

    $javaHome = Resolve-GradleJavaHome -ConfiguredPath $configuredJavaHome -CandidatePaths $candidatePaths
    $javaHomeEscaped = $javaHome.Replace("\", "\\")

    $lines = if (Test-Path $GradlePropertiesPath) {
        @(Get-Content $GradlePropertiesPath -ErrorAction Stop)
    }
    else {
        @()
    }

    $updated = @()
    $found = $false
    foreach ($line in $lines) {
        if ($line -like "org.gradle.java.home=*") {
            $updated += "org.gradle.java.home=$javaHomeEscaped"
            $found = $true
        }
        else {
            $updated += $line
        }
    }

    if (-not $found) {
        $updated += "org.gradle.java.home=$javaHomeEscaped"
    }

    if ((@($lines) -join "`n") -ne (@($updated) -join "`n")) {
        Set-Content -Path $GradlePropertiesPath -Value $updated -Encoding ASCII
    }

    $env:JAVA_HOME = $javaHome
    return $javaHome
}

function Get-AvailableAvdDeviceIds {
    param([string]$AvdManagerPath)

    $output = & $AvdManagerPath list device 2>&1
    $exitCode = $LASTEXITCODE
    Assert-NativeCommandSucceeded -CommandName "avdmanager list device" -ExitCode $exitCode

    return Parse-AvdDeviceIds -OutputLines $output
}

function Get-SystemImagePackage {
    param([string]$SdkDir)

    $preferred = @(
        "system-images;android-35;google_apis;x86_64",
        "system-images;android-35;google_apis_playstore_ps16k;x86_64",
        "system-images;android-36;google_apis_playstore;x86_64"
    )

    foreach ($package in $preferred) {
        $dir = Join-Path $SdkDir ($package.Replace(";", "\"))
        if (Test-Path $dir) {
            return $package
        }
    }

    throw "Nao encontrei uma system image Android instalada."
}

function Ensure-Avd {
    param(
        [string]$SdkDir,
        [string]$AvdName,
        [string[]]$LegacyAvdNames = @(),
        [string]$AvdRoot = $env:ANDROID_AVD_HOME
    )

    $resolvedAvdName = Resolve-AvdName `
        -PreferredName $AvdName `
        -LegacyNames $LegacyAvdNames `
        -ExistingAvdNames (Get-ExistingAvdNames -AvdRoot $AvdRoot)

    $avdDir = Join-Path $AvdRoot "$resolvedAvdName.avd"
    if (Test-Path $avdDir) {
        if ($resolvedAvdName -ne $AvdName) {
            Write-Host "A reutilizar o AVD existente $resolvedAvdName." -ForegroundColor Yellow
        }
        return $resolvedAvdName
    }

    $imagePackage = Get-SystemImagePackage -SdkDir $SdkDir
    $avdManager = Join-Path $SdkDir "cmdline-tools\latest\bin\avdmanager.bat"
    $deviceId = Select-AvdDeviceId `
        -PreferredDeviceIds @("pixel_8", "pixel_7", "pixel_6", "medium_phone") `
        -AvailableDeviceIds (Get-AvailableAvdDeviceIds -AvdManagerPath $avdManager)

    Write-Step "A criar o AVD $resolvedAvdName"
    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    try {
        "no" | & $avdManager create avd -n $resolvedAvdName -k $imagePackage -d $deviceId --force 2>&1 | Out-Host
        $exitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    Assert-NativeCommandSucceeded -CommandName "avdmanager create avd" -ExitCode $exitCode

    return $resolvedAvdName
}

function Ensure-AvdConfig {
    param(
        [string]$AvdName,
        [string]$AvdRoot = $env:ANDROID_AVD_HOME
    )

    $configPath = Join-Path $AvdRoot "$AvdName.avd\config.ini"
    if (-not (Test-Path $configPath)) {
        return
    }

    $config = Get-Content $configPath
    $updated = foreach ($line in $config) {
        switch -Regex ($line) {
            "^hw\.ramSize\s*=" { "hw.ramSize = 4096"; continue }
            "^vm\.heapSize\s*=" { "vm.heapSize = 512"; continue }
            default { $line }
        }
    }

    if (-not ($updated -match "^hw\.ramSize\s*=")) {
        $updated += "hw.ramSize = 4096"
    }
    if (-not ($updated -match "^vm\.heapSize\s*=")) {
        $updated += "vm.heapSize = 512"
    }

    Set-Content -Path $configPath -Value $updated -Encoding ASCII
}

function Start-PhpServer {
    param(
        [string]$PhpExe,
        [string]$RepoRoot
    )

    $serverDir = Join-Path $RepoRoot "bitefinder-api"
    $logDir = Join-Path $RepoRoot ".logs"
    $stdoutLogPath = Join-Path $logDir "php-server.out.log"
    $stderrLogPath = Join-Path $logDir "php-server.err.log"

    if (-not (Test-Path $logDir)) {
        New-Item -ItemType Directory -Path $logDir | Out-Null
    }

    $phpServer = Get-CimInstance Win32_Process |
        Where-Object { $_.Name -eq "php.exe" -and $_.CommandLine -like "*127.0.0.1:8000*" } |
        Select-Object -First 1

    if ($phpServer) {
        Write-Host "PHP server ja estava a correr na porta 8000." -ForegroundColor Yellow
        return
    }

    Write-Step "A arrancar o servidor PHP em http://127.0.0.1:8000"
    Start-Process -FilePath $PhpExe `
        -ArgumentList "-S", "127.0.0.1:8000", "-t", $serverDir `
        -WorkingDirectory $RepoRoot `
        -RedirectStandardOutput $stdoutLogPath `
        -RedirectStandardError $stderrLogPath `
        -WindowStyle Hidden
}

function Get-EmulatorSerial {
    param([string]$AdbExe)

    $devices = & $AdbExe devices | Select-String "emulator-.*\s+device$"
    if ($devices) {
        return (($devices | Select-Object -First 1).ToString() -split "\s+")[0]
    }

    return $null
}

function Start-Or-ReuseEmulator {
    param(
        [string]$SdkDir,
        [string]$AvdName,
        [switch]$Restart
    )

    $adbExe = Join-Path $SdkDir "platform-tools\adb.exe"
    $emulatorExe = Join-Path $SdkDir "emulator\emulator.exe"

    $serial = Get-EmulatorSerial -AdbExe $adbExe
    if ($Restart -and $serial) {
        Write-Step "A reiniciar o emulador atual"
        & $adbExe -s $serial emu kill | Out-Host
        Start-Sleep -Seconds 5
        $serial = $null
    }

    if (-not $serial) {
        Write-Step "A arrancar o emulador $AvdName"
        Start-Process -FilePath $emulatorExe `
            -ArgumentList "-avd", $AvdName, "-memory", "4096", "-gpu", "swiftshader_indirect", "-no-snapshot-load", "-no-boot-anim", "-netdelay", "none", "-netspeed", "full"
    }

    Write-Step "A esperar pelo boot do Android"
    & $adbExe start-server | Out-Null
    & $adbExe wait-for-device

    for ($i = 0; $i -lt 90; $i++) {
        $state = & $adbExe get-state 2>$null
        if ($state -eq "device") {
            $boot = (& $adbExe shell getprop sys.boot_completed 2>$null).Trim()
            if ($boot -eq "1") {
                $serial = Get-EmulatorSerial -AdbExe $adbExe
                if ($serial) {
                    & $adbExe -s $serial shell input keyevent 82 | Out-Null
                    return $serial
                }
            }
        }
        Start-Sleep -Seconds 5
    }

    throw "O emulador nao terminou o boot a tempo."
}

function Build-And-InstallApp {
    param(
        [string]$ProjectDir
    )

    Write-Step "A compilar e instalar o APK debug"
    Push-Location $ProjectDir
    try {
        & ".\gradlew.bat" installDebug
        Assert-NativeCommandSucceeded -CommandName "gradlew installDebug" -ExitCode $LASTEXITCODE
    }
    finally {
        Pop-Location
    }
}

function Launch-App {
    param(
        [string]$AdbExe,
        [string]$Serial
    )

    Write-Step "A abrir a app no emulador"
    & $AdbExe -s $Serial shell am force-stop com.example.bytefinder | Out-Null
    & $AdbExe -s $Serial shell am start -W -n com.example.bytefinder/.MainActivity | Out-Host

    Start-Sleep -Seconds 5
    $appPid = (& $AdbExe -s $Serial shell pidof com.example.bytefinder 2>$null)
    if (-not $appPid) {
        Write-Host "A app nao ficou residente. Vou tentar mais uma vez." -ForegroundColor Yellow
        & $AdbExe -s $Serial shell am start -W -n com.example.bytefinder/.MainActivity | Out-Host
    }
}

$projectDir = Join-Path $repoRoot "ByteFinder"
$localPropertiesPath = Join-Path $projectDir "local.properties"
$gradlePropertiesPath = Join-Path $projectDir "gradle.properties"
$preferredAvdName = "BiteFinder_API35"
$legacyAvdNames = @("ByteFinder_API35")

Write-Step "A resolver ferramentas instaladas"
$phpExe = Get-PhpExe
$sdkDir = Get-SdkDir -LocalPropertiesPath $localPropertiesPath
$adbExe = Join-Path $sdkDir "platform-tools\adb.exe"
$avdHome = Resolve-AvdHome -RepoRoot $repoRoot
Ensure-AvdHome -AvdHome $avdHome

Write-Step "A garantir local.properties"
Ensure-LocalProperties -Path $localPropertiesPath -SdkDir $sdkDir

Write-Step "A alinhar o Java do Gradle"
$gradleJavaHome = Ensure-GradleJavaHome -GradlePropertiesPath $gradlePropertiesPath
Write-Host "Gradle Java home: $gradleJavaHome" -ForegroundColor DarkCyan

Write-Step "A verificar o backend PHP"
$phpModules = & $phpExe -m
if ($phpModules -notcontains "pdo_sqlsrv") {
    Write-Host "Aviso: pdo_sqlsrv nao esta instalado. A API local sobe, mas endpoints com SQL Server podem falhar." -ForegroundColor Yellow
}

Start-PhpServer -PhpExe $phpExe -RepoRoot $repoRoot
$avdName = Ensure-Avd -SdkDir $sdkDir -AvdName $preferredAvdName -LegacyAvdNames $legacyAvdNames -AvdRoot $avdHome
Ensure-AvdConfig -AvdName $avdName -AvdRoot $avdHome
$serial = Start-Or-ReuseEmulator -SdkDir $sdkDir -AvdName $avdName -Restart:$RestartEmulator
Build-And-InstallApp -ProjectDir $projectDir
Launch-App -AdbExe $adbExe -Serial $serial

Write-Host "`nTudo pronto." -ForegroundColor Green
Write-Host "Emulador: $serial"
Write-Host "Servidor PHP: http://127.0.0.1:8000"
Write-Host "APK: $(Join-Path $projectDir 'app\build\outputs\apk\debug\app-debug.apk')"
