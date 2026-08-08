#!/usr/bin/env python3
"""Generate a PNG QR code for presentation slides (``.qr-card``).

Usage (from repo root or this directory)::

    python docs/presentation-template/utils/qr/generate_qr.py https://gradle.org \\
        -o docs/presentations/gradle/gradle-official-qr.png

    python generate_qr.py https://github.com/TNG/ArchUnit \\
        -o ../../../presentations/archunit/archunit-github-qr.png

Requires: ``pip install -r docs/presentation-template/utils/qr/requirements-qr.txt``
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

try:
    import qrcode
    from qrcode.constants import ERROR_CORRECT_H, ERROR_CORRECT_L, ERROR_CORRECT_M, ERROR_CORRECT_Q
except ImportError:
    print(
        "Missing dependency. Install with:\n"
        "  pip install -r docs/presentation-template/utils/qr/requirements-qr.txt",
        file=sys.stderr,
    )
    sys.exit(1)

_EC_LEVELS = {
    "L": ERROR_CORRECT_L,
    "M": ERROR_CORRECT_M,
    "Q": ERROR_CORRECT_Q,
    "H": ERROR_CORRECT_H,
}


def generate_qr(
    url: str,
    output: Path,
    *,
    box_size: int = 12,
    border: int = 2,
    error_correction: str = "M",
) -> Path:
    """Write a black-on-white QR PNG and return the output path."""
    qr = qrcode.QRCode(
        version=None,
        error_correction=_EC_LEVELS[error_correction.upper()],
        box_size=box_size,
        border=border,
    )
    qr.add_data(url)
    qr.make(fit=True)

    image = qr.make_image(fill_color="black", back_color="white")
    output = output.resolve()
    output.parent.mkdir(parents=True, exist_ok=True)
    image.save(output)
    return output


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        description="Generate a QR PNG for Ru BizGen presentation slides.",
    )
    parser.add_argument("url", help="Target URL encoded in the QR code")
    parser.add_argument(
        "-o",
        "--output",
        type=Path,
        required=True,
        help="Output PNG path (e.g. ../../../presentations/<slug>/<name>-qr.png)",
    )
    parser.add_argument(
        "--box-size",
        type=int,
        default=12,
        help="Module pixel size (default: 12 → ~sharp on retina next to .qr-card)",
    )
    parser.add_argument(
        "--border",
        type=int,
        default=2,
        help="Quiet-zone modules (default: 2)",
    )
    parser.add_argument(
        "--error-correction",
        choices=sorted(_EC_LEVELS),
        default="M",
        help="QR error-correction level (default: M)",
    )
    args = parser.parse_args(argv)

    path = generate_qr(
        args.url,
        args.output,
        box_size=args.box_size,
        border=args.border,
        error_correction=args.error_correction,
    )
    print(f"Wrote {path} <- {args.url}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
