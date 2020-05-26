#include <jni.h>
#include <stdio.h>
#include <iostream.h>
#include <CModelManage.h>
#include <string.h>
#include <iconv.h>
#include "json/json.h"
#include "json/json-forwards.h"
#include "mdExport.h"
#include "id_cmp.h"

using namespace std;

extern "C"

//JNIEXPORT void JNICALL
//      Java_CModelManage_modelserv(JNIEnv *env,jobject obj, jstring jsoninput,jstring jsonoutput)
int charset_convert(const char* from_charset,const char *to_charset,char *in_buf,size_t in_left,char *out_buf,size_t out_left);
string charset_convert_string(const char* from_charset,const char *to_charset,string inString);

JNIEXPORT jstring JNICALL
      Java_CModelManage_modelserv(JNIEnv *env,jobject obj, jstring jsoninput)
{
		jstring jsonoutput;

		// 从 instring 字符串取得指向字符串 UTF 编码的指针
      //注意C语言必须(*env)->         C++ env->
      const char* str =
	(char *)(env)->GetStringUTFChars(jsoninput, JNI_FALSE);
	printf("input---->%s\n",str);
	string strinput = str;
	Json::Reader reader;
	Json::Value value;
	if (reader.parse(strinput,value))
	{
		int type = value["invok_type"].asInt();
		if (type==0)//export
		{
			int exptype = value["export_type"].asInt();
			if (exptype==0)//exp all
			{
				cout<<"call exp all here."<<exptype<<endl;
			}
			else if (exptype==1)//exp by st
			{
				cout<<"call exp st here."<<exptype<<endl;
				const Json::Value arrayObj = value["st_id"];
				vector<string> vecStID;
				for (unsigned int i=0;i<arrayObj.size();i++)
				{
					if (!arrayObj[i].isMember("id"))
						continue;
					vecStID.push_back(arrayObj[i]["id"].asString());
					
				}
				
				string area_name = value["area_name"].asString();
				string cime_path = value["cime_path"].asString();
/*				string db_name = value["db_name"].asString();
				string db_ip = value["db_ip"].asString();
				string db_user = value["db_user"].asString();
				string db_pw = value["db_pw"].asString();*/
				//utf-8 to gb2312
				area_name = charset_convert_string("utf-8","gb2312",area_name);
			
				string outfile;
				int stat=0;
				string msg;

				stat = mdExpST(area_name,vecStID,"",outfile,msg);

				cout<<"call exp stat= ."<<stat<<endl;
				cout<<"call exp msg= ."<<msg<<endl;
				//jsonoutput
				Json::Value output;
				
				output["status"] = stat;
				output["msg"] = msg;
				output["fileName"] = outfile;
				
						
				string out1= output.toStyledString();
				// string 转 jstring
				jsonoutput = (jstring)(env)->NewStringUTF(out1.c_str());
				
			}
			else if (exptype==2)//exp by area
			{
				cout<<"call exp area here."<<exptype<<endl;
				
			}
			else
			{
				cout<<"error! unknown exptype: "<<exptype<<endl;
			}
			
		}
		else if (type==1)//id_cmp
		{
			cout<<"call id_cmp here."<<type<<endl;
			string external_cime = value["external_cime"].asString();
			string local_cime = value["local_cime"].asString();
			//utf-8 to gb2312
			external_cime = charset_convert_string("utf-8","gb2312",external_cime);
			local_cime = charset_convert_string("utf-8","gb2312",local_cime);
			
			string outfile="";
			string outfileutf8="";
			int stat = id_cmp(external_cime,local_cime,outfile,0);
			cout<<"CModelManage.so: outfile: "<<outfile<<endl;

			//gb2312 to utf-8
			outfileutf8 = charset_convert_string("gb2312","utf-8",outfile);
			//jsonoutput
			Json::Value output;
			
			output["status"] = stat;
			output["msg"] = "unsupported";
			output["fileName"] = outfileutf8;
			
			string out1 = output.toStyledString();			   
			jsonoutput = (jstring)(env)->NewStringUTF(out1.c_str());
	
		}
		else
		{
			cout<<"error! unknown type: "<<type<<endl;
		}
	}
	else
	{
		cout<<"reader.parse error!"<<endl;
	}
      // 通知虚拟机本地代码不再需要通过 str 访问 Java 字符串。
    (env)->ReleaseStringUTFChars(jsoninput, (const char *)str );

    return jsonoutput;
}

int charset_convert(const char* from_charset,const char *to_charset,char *in_buf,size_t in_left,char *out_buf,size_t out_left)
{
	size_t sRet = -1;
	char *pIn = in_buf;
	char *pOut = out_buf;
	size_t outLen = out_left;
	iconv_t icd = (iconv_t)-1;
	
	if ((NULL == from_charset) || (NULL == to_charset))
	{
		return -1;
	}
	
	icd = iconv_open( to_charset,from_charset);
	if (((iconv_t)-1)  == icd)
	{
		return -2;
	}
	printf("inLen=%d,outLen=%d\n",in_left,out_left);
	
	sRet = iconv(icd,&pIn,&in_left,&pOut,&out_left);
	if (((size_t)-1) == sRet)
	{
		printf("errno=%d\n",errno);
		return -3;
	}
	
	printf("in_left=%d,out_left=%d\n",in_left,out_left);
	out_buf[outLen-out_left] = 0;
	iconv_close(icd);
	return (int)(outLen-out_left);
	
}

string charset_convert_string(const char* from_charset,const char *to_charset,string inString)
{
	string outString="";
	if (inString.length()==0)
	{
		printf("charset_convert_string inString NULL error!\n");
	}
	char *inbuf = (char *)malloc(inString.length()+1);
	memset(inbuf,0,inString.length()+1);
	strcpy(inbuf,inString.c_str());
	cout<<"CModelManage.so: inbuf: "<<inbuf<<endl;
	size_t inLen = strlen(inbuf);
	size_t outLen = inLen*4;
	char *outbuf = (char *)malloc(sizeof(char)*outLen);
	memset(outbuf,0,sizeof(char)*outLen);
	int Ret = charset_convert(from_charset,to_charset,inbuf,inLen,outbuf,outLen);
	if (-1 == Ret)
	{
	cout<<"charset_convert error!"<<endl;
	}
	else
	{
	cout<<"charset_convert Ret= "<<Ret<<endl;
	outString = outbuf;
	}

	free(inbuf);
	inbuf = NULL;
	free(outbuf);
	outbuf = NULL;
	printf("6!outString=%s\n",outString.c_str());

	return outString;
}
