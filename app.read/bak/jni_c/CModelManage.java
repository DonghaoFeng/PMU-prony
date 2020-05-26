import java.util.*;
public class CModelManage
{
      //声明原生函数：参数为String类型
      //public native void modelserv(String jsoninput,String jsonoutput);
	  public native String modelserv(String jsoninput);
      //加载本地库代码     
      static
      {
           System.loadLibrary("CModelManage");
      }
}
