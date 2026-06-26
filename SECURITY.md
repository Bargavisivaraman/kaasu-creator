# Security Policy

## Supported versions

This project is in active development. Security fixes are applied to the
`main` branch.

## Reporting a vulnerability

If you discover a security vulnerability, please report it privately rather
than opening a public issue.

1. Use GitHub's [private vulnerability reporting](https://github.com/Bargavisivaraman/kaasu-creator/security/advisories/new) for this repository, or
2. Contact the maintainer directly.

Please include:

* A description of the vulnerability and its impact
* Steps to reproduce
* Any suggested remediation

You can expect an acknowledgement within a few days. Once the issue is
confirmed and fixed, the report will be disclosed responsibly.

## Handling secrets

Database credentials and API keys must never be committed. This project reads
all secrets from environment variables (see `application.properties.example`).
If a secret is ever committed by mistake, rotate it immediately and scrub it
from history.
