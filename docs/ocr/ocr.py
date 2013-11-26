#!/usr/bin/python
# performs ocr on images
# todo: pass lang as argument

import sys
import os
import shutil
import subprocess

USAGE = "USAGE: <imagePath>"
LANG = "eng"

# -- Validation -- ------

if len(sys.argv) <= 1:
  print USAGE
  sys.exit(1)

imagePath = sys.argv[1]

if not os.path.exists(imagePath):
  print "image '%s' does not exist"%imagePath
  sys.exit(1)

# -- Exec -----------------------------------------------------------------------

print "image to tiff..."
tiff_file = imagePath + '.tiff'
#convert -density 300 pp-preview.pdf -depth 8 -alpha off out.tiff
p = subprocess.Popen('convert -density 300 ' + imagePath + ' -depth 8 -alpha off ' + tiff_file, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
for line in p.stdout.readlines():
  print line,
retval = p.wait()


print "ocr..."
#tesseract -l deu ocr.tif
f = tiff_file
ocr_output = imagePath + '.out'
p = subprocess.Popen('tesseract -l ' + LANG + ' ' + f + ' ' + ocr_output, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
for line in p.stdout.readlines():
  print line,
retval = p.wait()
