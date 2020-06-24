package app.read.bean;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Donghao
 *
 */
public abstract class BaseJSONBean  implements Serializable{

	@Override
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseJSONBean> T fromString(String json) {
		return (T) new Gson().fromJson(json, this.getClass());
	}

}
