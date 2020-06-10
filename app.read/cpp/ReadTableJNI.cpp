#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <jni_ReadTableJNI.h>
#include <iconv.h>
#include "oodb_cpp_apiop.h"
using namespace oodb;
char buff[64] = { 0 };
char* charset_convert(const char *from_charset, const char *to_charset,
		char *in_buf);

void switch_type(int type, char *value, string &str) {

	switch (type) {
	case OODB_CHAR:
		strcpy(buff, value);
		break;
	case OODB_TIME: {
		//printf("%d: %s\t",i,ctime((time_t*)value));
		char *timeBuf = ctime((time_t*) value);
		timeBuf[24] = '\0';
		strcpy(buff, timeBuf);
		break;
	}
	case OODB_FLOAT:
		sprintf(buff, "%f|", *(float*) value);
		break;
	case OODB_DOUBLE:
		sprintf(buff, "%lf|", *(double*) value);
		break;
	case OODB_INT:
		sprintf(buff, "%d|", *(int*) value);
		break;
	case OODB_SMALLINT:
		sprintf(buff, "%d|", *(smallint*) value);
		break;
	case OODB_TINYINT:
		sprintf(buff, "%d|", *(tinyint*) value);
		break;
	case OODB_BIT:
		sprintf(buff, "%d|", *(bit*) value);
		break;
	case OODB_MASK:
		sprintf(buff, "%d|", *(mask*) value);
		break;
	case OODB_LONG:
		sprintf(buff, "%d|", *(longint*) value);
		//case OODB_UINT		:
		//printf("%d: %d\t",i,*(unsigned int*)value);
		break;
	case OODB_PAIR_LONG:
	case OODB_MEASID: {
		PAIR_LONG *pair_long = (PAIR_LONG*) value;
		sprintf(buff, "%ld,%ld|", pair_long->first_long,
				pair_long->second_long);
		break;
	}
	default:
		printf("unknow ");
	}
	switch (type) {
	case OODB_CHAR:
		str.append(charset_convert("gb2312", "utf-8", buff));
		str.append("|");
		break;
	case OODB_TIME:
		str.append(buff);
		str.append("|");
		break;
	default:
		str.append(buff);
	}

//	printf(str.c_str());
}

JNIEXPORT jstring JNICALL
Java_jni_ReadTableJNI_readTable(JNIEnv *env, jclass jc, jstring sys_name, jstring bob_name, jstring db_name,jstring tb_name)
{
	COodbApiOp oodbapi;
	char* sys = (char*)env->GetStringUTFChars(sys_name,0);
	char* bob = (char*)env->GetStringUTFChars(bob_name,0);
	char* db = (char*)env->GetStringUTFChars(db_name,0);
	char* tb = (char*)env->GetStringUTFChars(tb_name,0);
	int ret = oodbapi.OpenTb(sys, bob, db, tb);
	if (ret < 0) {
		printf("OpenTb() 失败!\n");
	} else {
//		printf("OpenTb() 成功!\n");
	}

	CResult read_result;
	ret = oodbapi.ReadTb(read_result, 1);
	if (ret < 0) {
		printf("ReadTb() 失败!\n");
	} else {
//		printf("ReadTb() 成功,%s!\n",tb);
	}
//	printf((char *)(read_result.buffer));
	int rec_num = read_result.rec_num;
	int rec_len = read_result.rec_len;
//	printf("rec_len %d|", rec_len);
	vector < ATTRDESC > attr_desc;
	ret = oodbapi.GetAttrDesc(attr_desc);
	int attr_num = attr_desc.size();
	char* attr_value =(char*) read_result.buffer;
	char* attr_values = attr_value;
	string str;
	for(int i =0; i< rec_num;i++)
	{
		for(int k = 0; k<attr_num; k++)
		{
			attr_values= attr_value + attr_desc[k].offset;
			switch_type(attr_desc[k].type,attr_values,str);
		}
		attr_value+= rec_len;
		str.append("\n");
//		cout<<endl<<endl;
	}
	return (jstring)(env)->NewStringUTF(str.c_str());
}

char* charset_convert(const char *from_charset, const char *to_charset,
		char *in_buf) {
	size_t in_left = strlen(in_buf);
	size_t out_left = in_left * 4;
	char *out_buf = (char*) malloc(sizeof(char) * out_left);
	size_t sRet = -1;
	char *pIn = in_buf;
	char *pOut = out_buf;
	size_t outLen = out_left;
	iconv_t icd = (iconv_t) - 1;

	if ((NULL == from_charset) || (NULL == to_charset)) {
		return "-1";
	}

	icd = iconv_open(to_charset, from_charset);
	if (((iconv_t) - 1) == icd) {
		return "-2";
	}
//	printf("inLen=%d,outLen=%d\n",in_left,out_left);

	sRet = iconv(icd, &pIn, &in_left, &pOut, &out_left);
	if (((size_t) - 1) == sRet) {
		printf("errno=%d\n", errno);
		return "-3";
	}

//	printf("in_left=%d,out_left=%d\n",in_left,out_left);
	out_buf[outLen - out_left] = 0;
	iconv_close(icd);
	return out_buf;

}

void fun(char *s)
{
	int i, j = 0;
	for (i = 0; s[i] != '\0'; i++)
	{
		if (s[i] < '0' || s[i] > '9')
		{
			s[j++] = s[i];
		}
	}
	s[j] = '\0';
}

JNIEXPORT jobject JNICALL
Java_jni_ReadTableJNI_readTableN(JNIEnv *env, jclass jc, jstring sys_name, jstring bob_name, jstring db_name,jstring tb_name)
{
	COodbApiOp oodbapi;
	char* sys = (char*)env->GetStringUTFChars(sys_name,0);
	char* bob = (char*)env->GetStringUTFChars(bob_name,0);
	char* db = (char*)env->GetStringUTFChars(db_name,0);
	char* tb = (char*)env->GetStringUTFChars(tb_name,0);
	int ret = oodbapi.OpenTb(sys, bob, db, tb);
	if (ret < 0) {
		printf("OpenTb() failed!\n");
	} else {
//		printf("OpenTb() 鎴愬姛!\n");
	}

	CResult read_result;
	ret = oodbapi.ReadTb(read_result, 1);
	if (ret < 0) {
		printf("ReadTb() failed!\n");
	} else {
//		printf("ReadTb() 鎴愬姛,%s!\n",tb);
	}
//	printf((char *)(read_result.buffer));
	int rec_num = read_result.rec_num;
	int rec_len = read_result.rec_len;
//	printf("rec_len %d|", rec_len);
	vector < ATTRDESC > attr_desc;
	ret = oodbapi.GetAttrDesc(attr_desc);
	int attr_num = attr_desc.size();
	char* attr_value =(char*) read_result.buffer;
	char* attr_values = attr_value;

	jclass list_jcs = env->FindClass("java/util/ArrayList");
	if (list_jcs == NULL) {
//		LOGI("ArrayList not find!");
		return NULL;
	}
	jmethodID list_init = env->GetMethodID(list_jcs, "<init>", "()V");

	jobject list_obj = env->NewObject(list_jcs, list_init, "");

	jmethodID list_add = env->GetMethodID(list_jcs, "add",
			"(Ljava/lang/Object;)Z");

	tb[0] -=32;
	fun(tb);
	string str ;
	str.append("app/read/bean/");
	str.append(tb);
	str.append("Record");
	jclass index_cls = env->FindClass(str.c_str());
	jmethodID index_init = env->GetMethodID(index_cls, "<init>",
			"()V");

	jfieldID fid;
	for(int i =0; i< rec_num;i++)
	{
		jobject index_obj = env->NewObject(index_cls, index_init, "");
		for(int k = 0; k<attr_num; k++)
		{
			attr_values= attr_value + attr_desc[k].offset;
			const char * name = attr_desc[k].name.c_str();
//			printf("%d|",attr_desc[k].type);
			switch (attr_desc[k].type) {
				case OODB_CHAR: {
					fid=env->GetFieldID(index_cls,name,"Ljava/lang/String;");
					jstring str = (jstring)(env)->NewStringUTF(attr_values);
					env->SetObjectField(index_obj, fid, str);
					break;
				}
				case OODB_TIME: {
					//printf("%d: %s\t",i,ctime((time_t*)value));
					fid=env->GetFieldID(index_cls,name,"Ljava/lang/String;");
					char *timeBuf = ctime((time_t*) attr_values);
					timeBuf[24] = '\0';
					jstring str = (jstring)(env)->NewStringUTF(timeBuf);
					env->SetObjectField(index_obj, fid, str);
					break;
				}
				case OODB_FLOAT: {
					fid=env->GetFieldID(index_cls,name,"F");
					jfloat jf = (jfloat)(*(float*) attr_values);
					env->SetFloatField(index_obj, fid, jf);
					break;
				}
				case OODB_DOUBLE: {
//				sprintf(buff, "%lf|", *(double*) value);
					fid=env->GetFieldID(index_cls,name,"D");
					jdouble jd = (jdouble)(*(double*)attr_values);
					env->SetDoubleField(index_obj, fid, jd);
					break;
				}
				case OODB_INT: {
//				sprintf(buff, "%d|", *(int*) value);
					fid=env->GetFieldID(index_cls,name,"I");
					jint ji = (jint)(*(int*) attr_values);
					env->SetIntField(index_obj, fid, ji);
					break;
				}
				case OODB_SMALLINT:{
//				sprintf(buff, "%d|", *(smallint*) value);
					fid=env->GetFieldID(index_cls,name,"I");

					jint ji = (jint)(*(smallint*) attr_values);
					env->SetIntField(index_obj, fid, ji);
					break;
				}
				case OODB_TINYINT:{
//				sprintf(buff, "%d|", *(tinyint*) value);
					fid=env->GetFieldID(index_cls,name,"I");

					jint ji = (jint)(*(tinyint*) attr_values);
					env->SetIntField(index_obj, fid, ji);
					break;
				}
				case OODB_BIT:
//				sprintf(buff, "%d|", *(bit*) value);
				break;
				case OODB_MASK:
//				sprintf(buff, "%d|", *(mask*) value);
				break;
				case OODB_LONG: {
//				sprintf(buff, "%d|", *(longint*) value);
//					print(name);
					fid=env->GetFieldID(index_cls,name,"J");

					jlong jl = (jlong)(*(longint*) attr_values);
					env->SetIntField(index_obj, fid, jl);
//					//case OODB_UINT		:
//					//printf("%d: %d\t",i,*(unsigned int*)value);
					break;
				}
				case OODB_PAIR_LONG:
				case OODB_MEASID: {
					fid=env->GetFieldID(index_cls,name,"J");
					PAIR_LONG *pair_long = (PAIR_LONG*) attr_values;
					jlong l1 =(jlong)pair_long->first_long;
					jlong l2= (jlong)pair_long->second_long;
					env->SetIntField(index_obj, fid, l1+l2);
					break;
				}
				default:
				printf("unknow ");
			}
		}
		attr_value+= rec_len;
		env->CallBooleanMethod(list_obj, list_add, index_obj);
	}
	return list_obj;
}


