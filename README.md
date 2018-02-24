# Nono -- a nonogram game for the tty

This is a simple curses-style nonogram player. It comes with an index of puzzles
from nonograms.org and will download the puzzles themselves on demand.

It also has code to support loading puzzles from local JSON and text files, but
this is not currently hooked up to the UI anywhere.

It supports both TTY and graphical operation; if a display is available, it'll
use its own built-in terminal emulator, otherwise it will display in the
terminal you ran it from.

## License

Released under the Apache v2 license; see COPYING for details.

Sample nonograms (in resources/puzzles/) are from Thomas Rosenau's
[nonogram solver](https://github.com/ThomasR/nonogram-solver).
