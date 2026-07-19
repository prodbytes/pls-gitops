# Welcome to "Podless Gitops" 

```$ pls``` for friends :)

`pls` is a GitOps tool: point it at a directory of infrastructure resources,
tell it your **goal**, and it does the right thing — automated resource
deployment, destruction and pruning, from a single command.

```bash
pls gitops ./infra
```

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/prodbytes/pls-gitops)
[![Open in Dev Containers](https://img.shields.io/static/v1?label=Dev%20Containers&message=Open&color=007ACC&logo=visualstudiocode)](https://vscode.dev/redirect?url=vscode://ms-vscode-remote.remote-containers/cloneInVolume?url=https://github.com/prodbytes/pls-gitops)

## Goals, not recipes

Remember moving from Ant (or a pile of shell scripts) to Maven? With Ant you
wrote a recipe: compile these files, then copy them here, then zip that folder
— and every project's recipe drifted apart. With Maven you state a goal —
`mvn install` — and the tool knows what that means for your project.

`pls` brings the same shift to infrastructure. Instead of a pipeline of
hand-wired `aws cloudformation deploy` / `terraform apply` / `cdk deploy`
calls, you declare the goal:

| Goal | Meaning |
|------|---------|
| `deploy` | create or update the resources found in the directory |
| `destroy` | tear them down |
| `prune` | remove what exists remotely but is no longer in the repo |
| `gitops` | `deploy` then `prune` — make reality match the repo |

## Simple

`pls` scans your resources and figures out what applies to each one. A
CloudFormation template, a Terraform module, a CDK app — each file or folder
is recognized by kind and mapped to the actions it supports. CloudFormation
and shell hooks work today; Terraform, CDK and friends follow the same
scan-and-recognize model.

## Transparent

No magic, no hidden state: `pls` just runs commands, and tells you exactly
what happens at every stage. Every goal executes the same four phases per
action:

1. 🔎 **Scan** — walk the target directory and list every resource found
2. 🧐 **Plan** — decide which action applies to each resource
3. ⚡ **Act** — run the underlying commands
4. 📝 **Report** — summarize what changed

What is scanned, planned, actioned and reported is always visible — on your
terminal or in your CI logs.

## Runs anywhere

The same binary behaves like a good citizen wherever it lands:

- **Your machine** — a nice terminal UI shows progress interactively.
- **GitHub Codespaces / devcontainers** — same experience, zero setup.
- **GitHub Actions** — plain logs, no TUI; the target directory defaults to
  `$GITHUB_WORKSPACE` so `pls gitops` just works as a step.
- **Kubernetes / containers** — use the image `docker.io/prodbytes/pls`; the
  target directory defaults to `/docker-entrypoint.d` when present.

The TUI turns on automatically on a desktop and stays off on headless CI/CD.
Override with the `pls.tui-enabled` config property (e.g.
`-Dpls.tui-enabled=false`).

## Getting started

Grab the native Linux binary from the
[latest release](https://github.com/prodbytes/pls-gitops/releases/latest), or
run the container image:

```bash
# binary
pls gitops ./infra

# container
docker run --rm -v "$PWD/infra:/docker-entrypoint.d" docker.io/prodbytes/pls gitops
```

Run `pls help` (or just `pls`) for usage. The target directory is resolved in
order: explicit argument → `$GITHUB_WORKSPACE` → `/docker-entrypoint.d` →
current directory.

## Development

The repo is a [Devbox](https://www.jetify.com/devbox)-powered
[Dev Container](https://containers.dev/): open it in Codespaces or VS Code
("Reopen in Container") and the toolchain — GraalVM CE (musl), Node.js,
Python, PostgreSQL — is pinned by [devbox.json](devbox.json) and locked in
[devbox.lock](devbox.lock). The container ships docker-in-docker, so
`docker ps` works out of the box.

```bash
devbox shell          # enter the environment
devbox services up    # start PostgreSQL (devbox-db) + health monitor
devbox services stop
```

Services are defined in [compose.yaml](compose.yaml) and
[process-compose.yaml](process-compose.yaml); the health monitor
([scripts/health-check.sh](scripts/health-check.sh)) logs one status line per
check once the database accepts connections.

The CLI itself is a [Quarkus](https://quarkus.io) + picocli application in
[pls-cli/](pls-cli/), compiled to a native binary with GraalVM. Build and test
with `mvn` inside the devbox shell.

### How the container is built

The [Containerfile](.devcontainer/Containerfile) keeps the Microsoft
`ubuntu-24.04` devcontainer base image and layers Devbox on top:

1. Devbox is installed as root, then everything else runs as the `vscode` user
   so the Nix store ownership matches the container's `remoteUser`.
2. Nix is installed in single-user mode (`--no-daemon`) — containers have no
   systemd, so the multi-user Nix daemon can't run.
3. At build time the locked store paths are fetched straight from
   `cache.nixos.org` to warm `/nix/store` (no GitHub API calls, so builds
   don't hit unauthenticated rate limits).
4. On container start, `postCreateCommand` runs `devbox install`, which finds
   the heavy downloads already cached. The first install still evaluates
   nixpkgs, which takes a few minutes; after that the environment is instant.

### Releasing

Push a tag ending in `GA` (e.g. `v0.1.202607192035-GA`) — or dispatch the
[Release workflow](.github/workflows/release.yml) manually — and CI runs
`make release`: the container image is built and pushed to Docker Hub, then
JReleaser publishes the native binary as a GitHub release with a
conventional-commits changelog.
