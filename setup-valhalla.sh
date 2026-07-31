#!/usr/bin/env bash

set -euo pipefail

VALHALLA_PAGE="https://jdk.java.net/valhalla/"
INSTALL_ROOT="${HOME}/.jdks"
PROFILE_FILE="${HOME}/.bashrc"
ENV_FILE="${HOME}/.valhalla-jdk-env"
MANAGED_START="# >>> valhalla-jdk >>>"
MANAGED_END="# <<< valhalla-jdk <<<"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command not found: $1" >&2
    exit 1
  fi
}

detect_arch() {
  case "$(uname -m)" in
    x86_64) echo "x64" ;;
    aarch64 | arm64) echo "aarch64" ;;
    *)
      echo "Unsupported architecture: $(uname -m)" >&2
      exit 1
      ;;
  esac
}

extract_link() {
  local page="$1"
  local arch="$2"

  # Pick the first Linux binary link, which is the newest build on this page.
  printf '%s\n' "$page" \
    | grep -Eo 'https://download\.java\.net/java/early_access/valhalla/[^"[:space:]]+_linux-[^"[:space:]]+_bin\.tar\.gz' \
    | grep "_linux-${arch}_bin\.tar\.gz" \
    | head -n1
}

main() {
  require_cmd curl
  require_cmd grep
  require_cmd tar
  require_cmd sha256sum
  require_cmd mktemp

  if [[ "$(uname -s)" != "Linux" ]]; then
    echo "This script currently supports Linux only." >&2
    exit 1
  fi

  local arch
  arch="$(detect_arch)"

  echo "Fetching build metadata from ${VALHALLA_PAGE} ..."
  local page
  page="$(curl -fsSL "${VALHALLA_PAGE}")"

  local tarball_url
  tarball_url="$(extract_link "$page" "$arch")"
  if [[ -z "$tarball_url" ]]; then
    echo "Could not find a Linux ${arch} Valhalla download URL on ${VALHALLA_PAGE}." >&2
    exit 1
  fi

  local sha_url
  sha_url="${tarball_url}.sha256"

  local archive_name
  archive_name="$(basename "$tarball_url")"

  local build_name
  build_name="${archive_name%_bin.tar.gz}"

  local install_dir
  install_dir="${INSTALL_ROOT}/${build_name}"

  mkdir -p "$INSTALL_ROOT"

  if [[ -d "$install_dir" ]]; then
    echo "Valhalla build already installed at ${install_dir}."
  else
    local tmpdir
    tmpdir="$(mktemp -d)"
    trap 'rm -rf "$tmpdir"' EXIT

    local archive_path
    archive_path="${tmpdir}/${archive_name}"

    echo "Downloading ${archive_name} ..."
    curl -fL "$tarball_url" -o "$archive_path"

    echo "Verifying SHA-256 checksum ..."
    local expected
    expected="$(curl -fsSL "$sha_url" | awk '{print $1}')"
    local actual
    actual="$(sha256sum "$archive_path" | awk '{print $1}')"
    if [[ "$expected" != "$actual" ]]; then
      echo "Checksum verification failed." >&2
      echo "Expected: ${expected}" >&2
      echo "Actual:   ${actual}" >&2
      exit 1
    fi

    echo "Extracting to ${install_dir} ..."
    mkdir -p "$install_dir"
    tar -xzf "$archive_path" -C "$install_dir" --strip-components=1
  fi

  cat >"$ENV_FILE" <<EOF
export JAVA_HOME="${install_dir}"
export PATH="\$JAVA_HOME/bin:\$PATH"
EOF

  if [[ -f "$PROFILE_FILE" ]]; then
    awk -v start="$MANAGED_START" -v end="$MANAGED_END" '
      $0 == start {skip=1; next}
      $0 == end {skip=0; next}
      !skip {print}
    ' "$PROFILE_FILE" >"${PROFILE_FILE}.tmp"
    mv "${PROFILE_FILE}.tmp" "$PROFILE_FILE"
  fi

  {
    echo ""
    echo "$MANAGED_START"
    echo "if [ -f \"${ENV_FILE}\" ]; then"
    echo "  . \"${ENV_FILE}\""
    echo "fi"
    echo "$MANAGED_END"
  } >>"$PROFILE_FILE"

  echo ""
  echo "Valhalla has been installed and configured as the default JDK for new bash shells."
  echo "Run this command to activate it in your current shell:"
  echo ""
  echo "  source \"${ENV_FILE}\""
  echo ""
  echo "Verification command:"
  echo "  java -version"
}

main "$@"