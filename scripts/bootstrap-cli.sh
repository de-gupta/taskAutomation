#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
INSTALL_DIR="${TASK_AUTOMATION_INSTALL_DIR:-$HOME/.local/bin}"

cd "$REPO_ROOT"

mvn -q -DskipTests package

CLI_JAR="$(find "$REPO_ROOT/target" -maxdepth 1 -type f -name '*-cli.jar' | sort | tail -n 1)"

if [[ -z "${CLI_JAR:-}" ]]; then
  echo "Could not find packaged CLI jar under target/."
  exit 1
fi

"$SCRIPT_DIR/install-cli.sh" "$CLI_JAR" "$INSTALL_DIR"

case ":$PATH:" in
  *":$INSTALL_DIR:"*)
    echo "CLI wrappers are ready on PATH."
    ;;
  *)
    echo "CLI wrappers were installed to $INSTALL_DIR."
    echo "Add this to your shell profile if needed:"
    echo "export PATH=\"$INSTALL_DIR:\$PATH\""
    ;;
esac
