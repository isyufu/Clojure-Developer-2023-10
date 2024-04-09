# game-2048


## Overview

FIXME: Write a paragraph about the library/project and highlight its goals.

## Setup
    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/). Click btn run or new game

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 
