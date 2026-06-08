#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <cli-jar-path> <install-dir>"
  exit 1
fi

JAR_PATH="$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
INSTALL_DIR="$2"

mkdir -p "$INSTALL_DIR"

cat > "$INSTALL_DIR/tasks" <<EOF
#!/usr/bin/env bash
exec java -jar "$JAR_PATH" "\$@"
EOF
chmod +x "$INSTALL_DIR/tasks"

while IFS= read -r command_name; do
  [[ -z "$command_name" ]] && continue
  cat > "$INSTALL_DIR/$command_name" <<EOF
#!/usr/bin/env bash
exec java -jar "$JAR_PATH" "$command_name" "\$@"
EOF
  chmod +x "$INSTALL_DIR/$command_name"
done < <(java -jar "$JAR_PATH" --list-commands)

echo "Installed CLI wrappers to $INSTALL_DIR"
