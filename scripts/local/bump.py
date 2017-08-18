#!/usr/bin/env python3

import xml.etree.ElementTree as xml; 
import subprocess
import re

# Get current version from POM
def version():
    return xml.parse(open('pom.xml')).getroot().find('{http://maven.apache.org/POM/4.0.0}version').text

# Get last tagged release version
def last_version():
    tags=subprocess.Popen(["git", "tag"], stdout=subprocess.PIPE).communicate()[0].split()
    tags=[x.decode('utf-8') for x in tags]
    return '0.0.0' if not tags else max(tags, key=semver)

# Get semver as an integer tuple for comparison
def semver(version):
    semver=re.compile(r'^.*(\d+\.\d+\.\d+).*').search(version).group(1)
    return tuple(map(int, (semver.split('.'))))

# If no version as been bumped since last release, then it's a patch
def is_patch():
    return semver(version()) == semver(last_version())

# Generate a patch version
def patch(version):
    return re.sub(r'(?<=(\d\.){2})\d+', lambda v: str(int(v.group(0)) + 1), version, 1)

def bump():
    return patch(version()) if is_patch() else version()

if __name__ == '__main__':
    print(bump())