package dalcoms.pub.connect3blocks.level;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class JsonDataLevel {
	@SerializedName( "level" )
	private String level;
	@SerializedName( "preferences" )
	private JsonDataLevelPreferences preferences;
	@SerializedName( "bricks" )
	private ArrayList<JsonDataBrick> bricks;
	@SerializedName( "entities" )
	private ArrayList<JsonDataEntity> entities;

	public String getLevel( ) {
		return this.level;
	}

	public JsonDataLevelPreferences gePreferences( ) {
		return this.preferences;
	}

	public ArrayList<JsonDataBrick> getBricks( ) {
		return this.bricks;
	}

	public ArrayList<JsonDataEntity> getEntities( ) {
		return this.entities;
	}

}
