#!/usr/bin/env python

import sys
import os

inputFile = sys.argv[1]
fileName, fileExtension = os.path.splitext(inputFile)
output = open(fileName + '_output' + fileExtension, 'a')

endInsert = False
endUpdate = False
endRead = False
with open(inputFile, 'r') as file:
    for line in file:
        if line.startswith('[OVERALL]'):
            output.write(line)
        elif line.startswith('[INSERT]'):
            if line.startswith('[INSERT], 0,'):
                endInsert = True
            if endInsert is False:
                output.write(line)
        elif line.startswith('[UPDATE]'):
            if line.startswith('[UPDATE], 0,'):
                endUpdate = True
            if endUpdate is False:
                output.write(line)
        elif line.startswith('[READ]'):
            if line.startswith('[READ], 0,'):
                endRead = True
            if endRead is False:
                output.write(line)
