import sys
import subprocess

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
        '-af:a:2', '"pan=stereo|FL=FR|FR=FR"',
        outputURL
    ]
    subprocess.run(command)

def process(fileURL):
    lines = []
    with open(fileURL, 'r', encoding='utf8') as file:
        for line in file:
            lines.append(line.split())
    for io in lines:
        if len(io) != 2:
            print('IO error')
            continue
        else:
            convert(io[0], io[1])  

if len(sys.argv) != 2:
    print('Illegal parameter')
else:
    process(sys.argv[1])
