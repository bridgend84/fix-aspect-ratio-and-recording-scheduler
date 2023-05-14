ffmpeg -i m2-14954-08403200-09065300.mpg -map 0:v -map 0:v -map 0:a -c:v:0 libx264 -b:v:0 1.8M -vf:v:0 scale=iw:ih -c:v:1 libx264 -b:v:1 0.75M -s:v:1 480x360 -c:a:2 aac -b:a:2 128k output.mp4
