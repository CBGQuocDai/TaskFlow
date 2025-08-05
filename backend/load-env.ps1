function Set-EnvironmentVariablesFromFile {
    param (
        [Parameter(Mandatory=$true)]
        [string]$Path
    )

    if (-not (Test-Path -Path $Path)) {
        Write-Error "File does not exist at '$Path'."
        return
    }

    $content = Get-Content -Path $Path -Raw
    $lines = $content -split "`n" | Where-Object { $_ -match '^\s*[a-zA-Z_][a-zA-Z0-9_]*\s*=' }

    foreach ($line in $lines) {
        $line = $line.Trim()

        # Bỏ qua các dòng comment và dòng trống
        if ($line.StartsWith('#') -or [string]::IsNullOrWhiteSpace($line)) {
            continue
        }

        # Tách tên biến và giá trị
        $parts = $line.Split('=', 2)
        $name = $parts[0].Trim()
        $value = $parts[1].Trim()

        # Xóa các dấu ngoặc kép (nếu có)
        if ($value.StartsWith('"') -and $value.EndsWith('"')) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        if ($value.StartsWith("'") -and $value.EndsWith("'")) {
            $value = $value.Substring(1, $value.Length - 2)
        }

        # Thiết lập biến môi trường
        [System.Environment]::SetEnvironmentVariable($name, $value, [System.EnvironmentVariableTarget]::Process)
        Write-Host "Set environment variable: $name = '$value'"
    }
}

$envFilePath = ".env"
Set-EnvironmentVariablesFromFile -Path $envFilePath
