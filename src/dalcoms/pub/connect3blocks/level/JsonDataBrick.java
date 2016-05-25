package dalcoms.pub.connect3blocks.level;

import java.util.Map;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.util.color.Color;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.google.gson.annotations.SerializedName;

public class JsonDataBrick {
	@SerializedName( "name" )
	private String name;

	@SerializedName( "breakLevel" )
	private String breakLevel;

	@SerializedName( "color" )
	private BrickColorRGBA color;

	@SerializedName( "position" )
	private BrickPosition position;

	@SerializedName( "size" )
	private BrickSize size;

	@SerializedName( "physics" )
	private BrickPhysics physics;

	public String getName( ) {
		return this.name;
	}

	public int getBreakLevel( ) {
		return Integer.valueOf( this.breakLevel );
	}

	public Color getColor( ) {
		return color.getColor();
	}

	public float getX( ) {
		return position.getX();
	}

	public float getY( ) {
		return position.getY();
	}

	public String getLayout( ) {
		return position.getLayout();
	}

	public float getWidth( ) {
		return size.getWidth();
	}

	public float getHeight( ) {
		return size.getHeight();
	}

	public BodyType getBodyType( ) {
		return physics.getBodyType();
	}

	public String getBodyShape( ) {
		return physics.getBodyShape();
	}

	public FixtureDef getFixtureDef( ) {
		return physics.getFixtureDef();
	}

	public class BrickColorRGBA {
		@SerializedName( "r" )
		private String r;
		@SerializedName( "g" )
		private String g;
		@SerializedName( "b" )
		private String b;
		@SerializedName( "a" )
		private String a;

		public Color getColor( ) {
			final float COLOR_8BIT = 255f;
			Color result = new Color( Float.valueOf( r ) / COLOR_8BIT,
					Float.valueOf( g ) / COLOR_8BIT,
					Float.valueOf( b ) / COLOR_8BIT,
					Float.valueOf( a ) / COLOR_8BIT );
			return result;
		}
	}

	public class BrickPosition {
		@SerializedName( "x" )
		private String x;
		@SerializedName( "y" )
		private String y;
		@SerializedName( "layout" )
		private String layout;

		public float getX( ) {
			return Float.valueOf( x );
		}

		public float getY( ) {
			return Float.valueOf( y );
		}

		public String getLayout( ) {
			return layout;
		}
	}

	public class BrickSize {
		@SerializedName( "width" )
		private String width;
		@SerializedName( "height" )
		private String height;

		public float getWidth( ) {
			return Float.valueOf( width );
		}

		public float getHeight( ) {
			return Float.valueOf( height );
		}
	}

	public class BrickPhysics {
		@SerializedName( "bodyType" )
		private String bodyType;
		@SerializedName( "bodyShape" )
		private String bodyShape;
		@SerializedName( "fixtureDef" )
		private Map<String, String> fixtureDef;

		public BodyType getBodyType( ) {
			BodyType result = BodyType.StaticBody;
			if ( bodyType.equals( "static" ) ) {
				result = BodyType.StaticBody;
			} else if ( bodyType.equals( "dynmic" ) ) {
				result = BodyType.DynamicBody;
			} else if ( bodyType.equals( "kinematic" ) ) {
				result = BodyType.KinematicBody;
			}

			return result;
		}

		public String getBodyShape( ) {
			return this.bodyShape;
		}

		public FixtureDef getFixtureDef( ) {
			return PhysicsFactory.createFixtureDef(
					Float.valueOf( this.fixtureDef.get( "density" ) ),
					Float.valueOf( this.fixtureDef.get( "elasticity" ) ),
					Float.valueOf( this.fixtureDef.get( "friction" ) ) );
		}

	}
}
