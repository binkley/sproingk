#!/usr/bin/env python3

import urllib.request, json, sys

url = "http://sproingk-binkley.boxfuse.io:8080/health"
with urllib.request.urlopen(url) as url:
    data = json.loads(url.read().decode())
    sys.exit(0 if "UP" == data["status"] else 1)
