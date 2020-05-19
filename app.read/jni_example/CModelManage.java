import java.util.*;
public class CModelManage
{
      //澹版槑鍘熺敓鍑芥暟锛氬弬鏁颁负String绫诲瀷
      //public native void modelserv(String jsoninput,String jsonoutput);
	  public native String tableRead(String jsoninput);
      //鍔犺浇鏈湴搴撲唬鐮�     
      static
      {
           System.loadLibrary("Table");
      }
}
