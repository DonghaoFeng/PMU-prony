import java.util.*;
import com.alibaba.fastjson.JSONObject;

public class HelloWorld
{
     public static void main(String argv[])
     {
         new HelloWorld();
     }

     public HelloWorld()
     {
		String input = "{\"invok_type\":1,\"external_cime\":\"福建_GLOBAL_DV2_20191216_1.CIME\",\"local_cime\":\"FJ_GLOBAL_DV2_20191216_2.CIME\"}";


		String output = new CModelManage().modelserv(input);
		
		JSONObject object = JSONObject.parseObject(output);
		int ret = object.getIntValue("status");
		String msg = object.getString("msg");
		String outfile = object.getString("fileName");
		
		System.out.print("outfile: ");
		System.out.println(outfile);
	

		String input1 = "{\"invok_type\":0, \"export_type\":1,\"area_name\":\"fujian\",\"cime_path\":\"\",\"st_id\":[{\"id\":\"113997365584592921\"},{\"id\":\"113997365584592901\"},{\"id\":\"113997365584592899\"}]}";
		String output1 = new CModelManage().modelserv(input1);
		
		JSONObject object1 = JSONObject.parseObject(output1);
		int ret1 = object1.getIntValue("status");
		String msg1 = object1.getString("msg");
		String outfile1 = object1.getString("fileName");
		
		System.out.print("outfile1: ");
		System.out.println(outfile1);
	
     }
}