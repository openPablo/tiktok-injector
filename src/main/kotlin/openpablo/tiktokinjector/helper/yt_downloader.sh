pip install -U yt-dlp

yt-dlp -f "bestvideo" --external-downloader ffmpeg --external-downloader-args "-ss 00:00:10.00 -to 00:03:10.00" -o "/home/pablo/raw_videos/%(id)s.%(ext)s" "https://www.youtube.com/c/NoCopyrightGameplays/videos"