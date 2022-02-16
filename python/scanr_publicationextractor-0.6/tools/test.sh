nosetests                   \
    --all-modules           \
    --traverse-namespace    \
    --with-coverage         \
    --cover-tests           \
    --with-doctest          \
    --cover-package scanr_publicationextractor \
    --where test/ $*
