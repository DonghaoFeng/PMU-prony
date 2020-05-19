    g++ -fPIC -D_REENTRANT -c -I$(HOME)/inclue/platform/oodbcpp -I$(HOME)/inclue/platform/rtdbc -I$(HOME)/include/platform/servicebus ReadTableJNI.cpp
    g++ -shared MessageJNI.o -L$HOME/lib -lrte -o libjrte.so
    cp libjrte.so $HOME/lib
