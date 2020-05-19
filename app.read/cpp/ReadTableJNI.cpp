#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <ReadTableJNI.h>
#include "oodb_cpp_apiop.h"
using namespace oodb;

int switch_type(int type, char *value) {
	switch (type) {
	case OODB_CHAR:
		printf("%s|", value);
		break;
	case OODB_TIME: {
		//printf("%d: %s\t",i,ctime((time_t*)value));
		char *timeBuf = ctime((time_t*) value);
		timeBuf[24] = '\0';
		printf("%s|", timeBuf);
		break;
	}
	case OODB_FLOAT:
		printf("%f|",  *(float*) value);
		break;
	case OODB_DOUBLE:
		printf("%lf|", *(double*) value);
		break;
	case OODB_INT:
		printf("%d|",  *(int*) value);
		break;
	case OODB_SMALLINT:
		printf("%d|", *(smallint*) value);
		break;
	case OODB_TINYINT:
		printf("%d|", *(tinyint*) value);
		break;
	case OODB_BIT:
		printf("%d|",*(bit*) value);
		break;
	case OODB_MASK:
		printf("%d|",  *(mask*) value);
		break;
	case OODB_LONG:
		printf("%d|", *(longint*) value);
		break;
		//case OODB_UINT		:
		//printf("%d: %d\t",i,*(unsigned int*)value);
		//break;
	case OODB_PAIR_LONG:
	case OODB_MEASID: {
		PAIR_LONG *pair_long = (PAIR_LONG*) value;
		printf("%ld,%ld|",pair_long->first_long,
				pair_long->second_long);
		break;
	}
	default:
		printf("unknow ");
		break;

	}
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
		printf("OpenTb() ��ȡʧ��!\n");
	}else{
		printf("OpenTb() ��ȡ�ɹ�!\n");
	}

	CResult read_result;
	/*����������*/
	ret = oodbapi.ReadTb(read_result, 1);
	if (ret < 0) {
		printf("ReadTb() ��ȡʧ��!\n");
	}else{
		printf("ReadTb() ��ȡ�ɹ�!\n");
	}
	printf((char *)(read_result.buffer));
	int rec_num = read_result.rec_num;//���¼��
	int rec_len = read_result.rec_len;//��¼����
	vector < ATTRDESC > attr_desc;
	ret = oodbapi.GetAttrDesc(attr_desc);
	int attr_num = attr_desc.size();//���Ը���
	char* attr_value =(char*) read_result.buffer;
	char* attr_values = attr_value;
	for(int i =0; i< rec_num;i++)
	{
		printf("��¼%05d:",i+1);
		//cout<<"��"<<i+1<<"����¼:";
		for(int k = 0; k<attr_num; k++)
		{
			attr_values= attr_value + attr_desc[k].offset;
			switch_type(attr_desc[k].type,attr_values);
		}
		attr_value+= rec_len;
		cout<<endl<<endl;
	}
	char buff[128] = {0};
	float a = 0.1;
	sprintf(buff, "%f", a);
	return (jstring)(env)->NewStringUTF(buff);
}


