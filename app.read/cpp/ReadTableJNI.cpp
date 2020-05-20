#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <ReadTableJNI.h>
#include <iconv.h>
#include "oodb_cpp_apiop.h"
using namespace oodb;

char* charset_convert(const char* from_charset,const char *to_charset,char *in_buf);

void switch_type(int type, char *value,string& str) {
	char buff[128] = {0};
	switch (type) {
	case OODB_CHAR:
		sprintf(buff,"%s|", value);
		break;
	case OODB_TIME: {
		//printf("%d: %s\t",i,ctime((time_t*)value));
		char *timeBuf = ctime((time_t*) value);
		timeBuf[24] = '\0';
		sprintf(buff,"%s|", timeBuf);
		break;
	}
	case OODB_FLOAT:
		sprintf(buff,"%f|",  *(float*) value);
		break;
	case OODB_DOUBLE:
		sprintf(buff,"%lf|", *(double*) value);
		break;
	case OODB_INT:
		sprintf(buff,"%d|",  *(int*) value);
		break;
	case OODB_SMALLINT:
		sprintf(buff,"%d|", *(smallint*) value);
		break;
	case OODB_TINYINT:
		sprintf(buff,"%d|", *(tinyint*) value);
		break;
	case OODB_BIT:
		sprintf(buff,"%d|",*(bit*) value);
		break;
	case OODB_MASK:
		sprintf(buff,"%d|",  *(mask*) value);
		break;
	case OODB_LONG:
		sprintf(buff,"%d|", *(longint*) value);
		//case OODB_UINT		:
		//printf("%d: %d\t",i,*(unsigned int*)value);
		//break;
	case OODB_PAIR_LONG:
	case OODB_MEASID: {
		PAIR_LONG *pair_long = (PAIR_LONG*) value;
		sprintf(buff,"%ld,%ld|",pair_long->first_long,
				pair_long->second_long);
		break;
	}
	default:
		printf("unknow ");

	}
	switch (type) {
	case OODB_CHAR:
		str.append(charset_convert("gb2312","utf-8", buff));
		break;
	default:
		str.append(buff);
	}

//	printf(str.c_str());
}

JNIEXPORT jstring JNICALL
		Java_ReadTableJNI_readTable(JNIEnv *env, jclass jc, jstring sys_name, jstring bob_name, jstring db_name,jstring tb_name)
{
	COodbApiOp oodbapi;
	char* sys = (char*)env->GetStringUTFChars(sys_name,0);
	char* bob = (char*)env->GetStringUTFChars(bob_name,0);
	char* db = (char*)env->GetStringUTFChars(db_name,0);
	char* tb = (char*)env->GetStringUTFChars(tb_name,0);
	int ret = oodbapi.OpenTb(sys, bob, db, tb);
	if (ret < 0) {
		printf("OpenTb() Ê§°Ü!\n");
	}else{
		printf("OpenTb() ³É¹¦!\n");
	}

	CResult read_result;
	/*ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½*/
	ret = oodbapi.ReadTb(read_result, 1);
	if (ret < 0) {
		printf("ReadTb() Ê§°Ü!\n");
	}else{
		printf("ReadTb() ³É¹¦!\n");
	}
//	printf((char *)(read_result.buffer));
	int rec_num = read_result.rec_num;
	int rec_len = read_result.rec_len;
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

char* charset_convert(const char* from_charset,const char *to_charset,char *in_buf)
{

	size_t in_left = strlen(in_buf);
	size_t out_left = in_left*4;
	char *out_buf = (char *)malloc(sizeof(char)*out_left);
	size_t sRet = -1;
	char *pIn = in_buf;
	char *pOut = out_buf;
	size_t outLen = out_left;
	iconv_t icd = (iconv_t)-1;

	if ((NULL == from_charset) || (NULL == to_charset))
	{
		return "-1";
	}

	icd = iconv_open( to_charset,from_charset);
	if (((iconv_t)-1)  == icd)
	{
		return "-2";
	}
//	printf("inLen=%d,outLen=%d\n",in_left,out_left);

	sRet = iconv(icd,&pIn,&in_left,&pOut,&out_left);
	if (((size_t)-1) == sRet)
	{
		printf("errno=%d\n",errno);
		return "-3";
	}

//	printf("in_left=%d,out_left=%d\n",in_left,out_left);
	out_buf[outLen-out_left] = 0;
	iconv_close(icd);
	return out_buf;

}

