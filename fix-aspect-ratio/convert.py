from sys import argv
from subprocess import run
from os.path import exists, dirname, isdir
from os import access, W_OK

def convert(inputURL, outputURL):
    command = [
        'ffmpeg', '-i', inputURL, '-y',
        '-map', '0:v',
        '-map', '0:v',
        '-map', '0:a',
        '-c:v:0', 'libx264',
        '-b:v:0', '1.8M',
        '-vf:v:0', 'yadif',
        '-c:v:1', 'libx264',
        '-b:v:1', '0.75M',
        '-s:v:1', '480x360',
        '-c:a:2', 'aac',
        '-b:a:2', '128k',
        '-af:a:2', 'pan=stereo|FL=FL|FR=FL',
        outputURL
    ]
    return run(command)

def process(fileURL):
    lines = []
    counter = 0
    with open(fileURL, 'r', encoding='utf8') as file:
        for line in file:
            lines.append(line.split())
    for io in lines:
        counter += 1
        if len(io) != 2:
            print('\033[1;31;43m!!!SCRIPT ERROR!!! Illegal number of parameter in line ' + str(counter) + ': ' + ' '.join(io) + ' \033[0;0m')
            continue
        elif not exists(io[0]):
            print('\033[1;31;43m!!!SCRIPT ERROR!!! Input video file doesn\'t exists on this path (line ' + str(counter) + '): ' + io[0] + ' \033[0;0m')
            continue
        elif exists(io[1]):
            print('\033[1;31;43m!!!SCRIPT ERROR!!! Output video file already exists on this path (line ' + str(counter) + '): ' + io[1] + ' \033[0;0m')
            continue
        elif not isdir(dirname(io[1])):
            print('\033[1;31;43m!!!SCRIPT ERROR!!! Output folder doesn\'t exists (line ' + str(counter) + '): ' + dirname(io[1]) + ' \033[0;0m')
            continue
        elif not access(dirname(io[1]), W_OK):
            print('\033[1;31;43m!!!SCRIPT ERROR!!! Output folder isn\'t writeable (line ' + str(counter) + '): ' + dirname(io[1]) + ' \033[0;0m')
            continue
        else:
            result = convert(io[0], io[1])
            if result.stderr:
                print(result.stderr)
            else:
                print('\033[1;32;47m Video file converted succesfully to path: ' + io[1] + ' \033[0;0m')

if len(argv) != 2:
    print('\033[1;31;43m!!!SCRIPT ERROR!!! Illegal number of input parameter \033[0;0m')
elif not exists(argv[1]):
    print('\033[1;31;43m!!!SCRIPT ERROR!!! Input file doesn\'t exists \033[0;0m')
else:
    process(argv[1])
