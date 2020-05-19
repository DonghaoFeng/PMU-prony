.SUFFIXES:
.SUFFIXES: .cxx .cpp .o

#ver=debug

ifeq ($(ver), debug)
VCC=g++ -g -gstabs+ -DDEBUG 
else ifeq ($(ver), release)
VCC=g++ -gstabs+ 
else
VCC=g++ -g -gstabs+ 
endif

CC = $(VCC)
D5000=$(HOME)/src
MODEL_CENTER=$(HOME)/fdh_test

BINDIR=$(MODEL_CENTER)
OBJDIR=$(MODEL_CENTER)
LIBDIR=$(MODEL_CENTER)
SRCDIR=$(MODEL_CENTER)/include

ID_CMP=$(SRCDIR)

LIB_PATH=-L. -L$(HOME)/lib

LIBS= -lpthread -lrt -lc -lm   -loodb_cpp_apiop

INC_PATH=-I$(HOME)/inclue/platform/oodbcpp -I$(HOME)/inclue/platform/rtdbc -I$(HOME)/include/platform/servicebus -I$(HOME)/source/include/oodbcpp -I$(HOME)/source/include \
-I$(HOME)/source/include/rtdbc -I$(HOME)/jdk1.8.0_66/include -I$(HOME)/jdk1.8.0_66/include/linux -I.
#INC_PATH=-I$(HOME)/include -I$(HOME)/include/oodbcpp/ -I$(HOME)/include/rtdbc/ -I$(HOME)/source/include/rtdbc -I$(HOME)/source/include -I$(HOME)/source/include/oodbcpp 
#-I$(D5000)/include -I$(HOME)/include  -I$(HOME)/source/include -I$(HOME)/source/include/oodbcpp/ -I$(HOME)/source/include/rtdbc/

.c.o .cxx.o .cpp.o:
	$(CC) $(INC_PATH) -fPIC -c $< -o $(OBJDIR)/$(@F)
	

	
#閸欘垱澧界悰宀�鈻兼惔锟�
EDATA=ReadTableJNI.o 
libRT.so:$(EDATA)
	cd $(OBJDIR); \
	$(CC) -shared $(LIB_PATH) $(LIBS) -o $(BINDIR)/$@ $(EDATA)

.PHONY:clean
clean:
	rm $(OBJDIR)/*.o
