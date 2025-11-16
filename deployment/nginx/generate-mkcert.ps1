Param(
# Liste des domaines pour lesquels générer les certificats
    [string[]]$Domains = @(
    "frontend",
    "admin",
    "api",
    "auth",
    "grafana",
    "prometheus",
    "tempo",
    "loki"
),

# Nom des fichiers de sortie pour Nginx
    [string]$CertFileName = "ashs-local.crt",
    [string]$KeyFileName = "ashs-local.key"
)

# -----------------------------
# 1) Fonctions utilitaires
# -----------------------------
function Write-Info($msg)
{
    Write-Host "[INFO ] $msg" -ForegroundColor Cyan
}
function Write-Warn($msg)
{
    Write-Host "[WARN ] $msg" -ForegroundColor Yellow
}
function Write-ErrorMsg($msg)
{
    Write-Host "[ERROR] $msg" -ForegroundColor Red
}

# Dossier courant = dossier nginx du projet
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

Write-Info "Dossier du script : $ScriptDir"

# Dossier où l'on garde les certificats originaux
$CertsDir = Join-Path $ScriptDir "certs"
if (-not (Test-Path $CertsDir))
{
    New-Item -ItemType Directory -Path $CertsDir | Out-Null
    Write-Info "Dossier 'certs' créé : $CertsDir"
}

# -----------------------------
# 2) Vérification de l'exécution en administrateur
# -----------------------------
$windowsIdentity = [Security.Principal.WindowsIdentity]::GetCurrent()
$windowsPrincipal = New-Object Security.Principal.WindowsPrincipal($windowsIdentity)
$adminRole = [Security.Principal.WindowsBuiltInRole]::Administrator

if (-not $windowsPrincipal.IsInRole($adminRole))
{
    Write-Warn "Ce script doit être exécuté en tant qu'Administrateur."
    Write-Warn "Clique droit sur PowerShell → 'Exécuter en tant qu'administrateur'."
    exit 1
}

# -----------------------------
# 3) Installation de Chocolatey (si nécessaire)
# -----------------------------
if (-not (Get-Command choco -ErrorAction SilentlyContinue))
{
    Write-Info "Chocolatey non trouvé. Installation en cours..."

    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072

    Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

    if (-not (Get-Command choco -ErrorAction SilentlyContinue))
    {
        Write-ErrorMsg "Impossible d'installer Chocolatey. Vérifie ta connexion internet et réessaie."
        exit 1
    }

    Write-Info "Chocolatey installé avec succès."
}
else
{
    Write-Info "Chocolatey déjà installé."
}

# -----------------------------
# 4) Installation de mkcert (si nécessaire)
# -----------------------------
if (-not (Get-Command mkcert -ErrorAction SilentlyContinue))
{
    Write-Info "mkcert non trouvé. Installation via Chocolatey..."

    choco install mkcert -y

    $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" +   `
                  [System.Environment]::GetEnvironmentVariable("Path", "User")

    if (-not (Get-Command mkcert -ErrorAction SilentlyContinue))
    {
        Write-ErrorMsg "mkcert semble ne pas être correctement installé. Redémarre PowerShell puis réessaie."
        exit 1
    }

    Write-Info "mkcert installé avec succès."
}
else
{
    Write-Info "mkcert déjà installé."
}

# -----------------------------
# 5) Installation de la CA locale mkcert
# -----------------------------
Write-Info "Installation / vérification de l'autorité de certification locale mkcert..."
mkcert -install
if ($LASTEXITCODE -ne 0)
{
    Write-ErrorMsg "Échec lors de l'installation de la CA locale mkcert."
    exit 1
}

# -----------------------------
# 6) Génération des certificats
# -----------------------------
Write-Info "Génération des certificats pour les domaines suivants :"
$Domains | ForEach-Object { Write-Host " - $_" -ForegroundColor Green }

# Fichier de sortie dans le dossier 'certs'
$FullCertPath = Join-Path $CertsDir "fullchain.pem"
$FullKeyPath = Join-Path $CertsDir "privkey.pem"

# Construction de la commande mkcert
$domainArgs = $Domains -join " "

Write-Info "Exécution de : mkcert -cert-file `"$FullCertPath`" -key-file `"$FullKeyPath`" $domainArgs"

mkcert -cert-file "$FullCertPath" -key-file "$FullKeyPath" $Domains

if ($LASTEXITCODE -ne 0 -or -not (Test-Path $FullCertPath) -or -not (Test-Path $FullKeyPath))
{
    Write-ErrorMsg "La génération des certificats mkcert a échoué."
    exit 1
}

Write-Info "Certificats générés dans :"
Write-Info " - Certificat : $FullCertPath"
Write-Info " - Clé       : $FullKeyPath"

# -----------------------------
# 7) Copie / renommage pour Nginx
# -----------------------------
$TargetCertPath = Join-Path $ScriptDir $CertFileName
$TargetKeyPath = Join-Path $ScriptDir $KeyFileName

Copy-Item -Path $FullCertPath -Destination $TargetCertPath -Force
Copy-Item -Path $FullKeyPath  -Destination $TargetKeyPath  -Force

Write-Info "Certificats copiés dans le dossier nginx :"
Write-Info " - $TargetCertPath"
Write-Info " - $TargetKeyPath"

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  mkcert : génération des certificats OK"     -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Tu peux maintenant lancer Docker Compose :"   -ForegroundColor Cyan
Write-Host "  cd .."                                      -ForegroundColor Cyan
Write-Host "  docker-compose -f docker-compose.yml -f nginx\docker-compose-staging.yml up -d" -ForegroundColor Cyan
Write-Host ""