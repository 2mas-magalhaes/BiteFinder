Set-StrictMode -Version Latest

$helpersPath = Join-Path $PSScriptRoot "..\run-bytefinder.helpers.ps1"
. $helpersPath

Describe "run-bytefinder helpers" {
    It "reuses the legacy ByteFinder AVD when the BiteFinder AVD does not exist" {
        $resolved = Resolve-AvdName `
            -PreferredName "BiteFinder_API35" `
            -LegacyNames @("ByteFinder_API35") `
            -ExistingAvdNames @("ByteFinder_API35")

        $resolved | Should Be "ByteFinder_API35"
    }

    It "picks the first supported device id from the preferred list" {
        $deviceId = Select-AvdDeviceId `
            -PreferredDeviceIds @("pixel_8", "pixel_7", "pixel_6", "medium_phone") `
            -AvailableDeviceIds @("pixel_6", "medium_phone")

        $deviceId | Should Be "pixel_6"
    }

    It "throws when a native command exits with a non-zero code" {
        { Assert-NativeCommandSucceeded -CommandName "cmd /c exit 7" -ExitCode 7 } |
            Should Throw "falhou com exit code 7"
    }

    It "prefers a valid JDK 17 candidate when the configured Gradle Java home is invalid" {
        $javaHome = Resolve-GradleJavaHome `
            -ConfiguredPath "C:\Invalid\jdk-17" `
            -CandidatePaths @(
                "C:\Program Files\Eclipse Adoptium\jdk-11.0.29.7-hotspot",
                "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot",
                "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
            ) `
            -PathExists {
                param([string]$Path)
                return $Path -eq "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot" `
                    -or $Path -eq "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
            }

        $javaHome | Should Be "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
    }
}
