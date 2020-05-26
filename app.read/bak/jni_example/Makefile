.SUFFIXES:
.SUFFIXES: .cxx .cpp .o

#ver=debug

ifeq ($(ver), debug)
GCC=g++ -g -gstabs+ -DDEBUG
else ifeq ($(ver), release)
GCC=g++ -gstabs+
else
GCC=g++ -g -gstabs+
endif

CC=$(GCC)
D5000=$(SRC_HOME)

PRJDIR=$(SRC_HOME)/jni_c
SRCDIR=$(SRC_HOME)/jni_c
OBJDIR=$(SRC_HOME)/jni_c/obj
LIBDIR=$(SRC_HOME)/jni_c/lib

LIB_PATH=-L$(HOME)/lib

LIBS=-lmodel_exp -lid_cmp_jni -liconv

MODEL_INCLUDE=$(D5000)/include/model_include
INC_PATH=-I$(D5000)/include \
		-I$(MODEL_INCLUDE) -I$(MODEL_INCLUDE)/model_pub_api \
		-I$(PRJDIR) \
		-I$(HOME)/jdk1.8.0_66/include -I$(HOME)/jdk1.8.0_66/include/linux \
		-I$(SRCDIR)/json \
		-I$(SRC_HOME)/app/model_center/src/boost
		
VPATH=$(BASE_DIR)/xml_parse:$(BASE_DIR)/cime_mgr 

.c.o .cxx.o .cpp.o:
	$(CC) $(INC_PATH) -fPIC -c $< -o $(OBJDIR)/$(@F)
	
CMODEL_MANAGE=CModelManage.o json.o
libCModelManage.so:$(CMODEL_MANAGE)
	cd $(OBJDIR); \
	$(CC) -shared $(LIB_PATH) $(LIBS) -o $(LIBDIR)/$@ $(CMODEL_MANAGE)

.PHONY:clean
clean:
	rm $(OBJDIR)/*.o
	rm $(LIBDIR)/*.so
