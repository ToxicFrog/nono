#!/usr/bin/env zsh

set -e
#shopt -s lastpipe

# update-index-with-page <filename> <url>
# Fetch the given URL and add all puzzle index entries on it that don't already
# exist in <file> to the end of it.
function update-index-with-page {
  curl -f "$2" > /tmp/$$ || return 1
  cat /tmp/$$ | sed -En '
    /id="nonogram_[[:digit:]]+"/ {
      s,.*nonograms/i/([[:digit:]]+)".*>(.*)</a>,{ :id \1 :title "\2",
      h;d
    }
    /Size:/ {
      s,.*Added:</td><td>([0-9][0-9])\.([0-9][0-9])\.([0-9][0-9])</td></tr>,:date "20\3-\2-\1",
      s,<tr><td>Size:</td><td>([0-9]+)x([0-9]+)</td></tr>, :width \1 :height \2,
      s,<tr>.*/user/([0-9]+)">([^<]+)</a></td></tr>, :authorid \1 :author "\2",
      s,<tr>.*Picture:</td><td><img[^>]+title="([0-9]+)/10"></td></tr>, :rating \1,
      s,<tr>.*Difficulty:</td><td><img[^>]+title="([0-9]+)/10">.*, :difficulty \1 },
      H;x;s/\n/ /g;p;d
    }
  ' | while read entry; do
    if fgrep -q "$entry" "$1"; then
      return 2
    fi
    echo "$entry" >> "$1"
    echo "$1" "$entry"
  done
}

# update-index <filename> <size>
# Fetch all puzzle index entries of <size> and store the ones not already known
# in <filename>.
function update-index {
  for ((i=1; 1; ++i)); do
    update-index-with-page "$1" "http://www.nonograms.org/nonograms/size/$2/p/$i" \
    || break
  done
}

for size in small medium large; do
  update-index resources/index/nonograms.org.$size "$size"
done
