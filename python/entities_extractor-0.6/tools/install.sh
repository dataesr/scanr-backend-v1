test -z "$pipdir" && pipdir=$HOME/local/scanr/pipdir || pipdir=$pipdir

pip wheel \
        --wheel-dir $pipdir             \
        --find-links $pipdir            \
        .

