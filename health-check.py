#!/usr/bin/env python

import urllib2, json, sys

url = "http://sproingk-binkley.boxfuse.io:8080/actuator/health"
# url = "http://localhost:8080/actuator/health"
url = urllib2.urlopen(url)
data = json.loads(url.read().decode())
sys.exit(0 if "UP" == data["status"] else 1)
