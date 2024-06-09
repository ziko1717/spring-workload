# Set the path to your SSH private key
param(
    [string]$SSHPrivateKey
)

git config --local core.sshCommand "ssh -i $SSHPrivateKey"

# Function to clone or pull a git repository
function CloneOrPull {
    param(
        [string]$repoUrl,
        [string]$repoDir
    )

    if (-not (Test-Path $repoDir)) {
        # If the directory doesn't exist, clone the repository
        Write-Host "Cloning repository from $repoUrl to $repoDir"
        & git clone $repoUrl $repoDir
    } else {
        # If the directory exists, pull changes
        cd $repoDir
        Write-Host "Pulling changes in repository at $repoDir"
        & git pull
    }
}

# Function to commit and push changes to a git repository
function CommitAndPush {
    param(
        [string]$repoDir,
        [string]$commitMessage
    )

    cd $repoDir
    & git add .
    & git commit -m $commitMessage
    & git push origin master
}
