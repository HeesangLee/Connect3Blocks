package dalcoms.pub.connect3blocks;

import java.io.IOException;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.extension.svg.opengl.texture.atlas.bitmap.SVGBitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.Log;

public class ResourcesManager {
	private static final ResourcesManager instance = new ResourcesManager();

	protected Engine engine;
	protected MainActivity activity;
	protected Camera camera;
	protected VertexBufferObjectManager vbom;

	protected Vibrator pVibrator;

	protected float resizeFactor = 1f;
	private final float cameraWidthRef = 1080f;

	// =======================================
	// Graphic : Atlas
	private BuildableBitmapTextureAtlas atlasNearest;
	private final Point sizeAtlasNearest = new Point( 1280, 1968 );

	private BuildableBitmapTextureAtlas atlasBilinear;
	private final Point sizeAtlasBilinear = new Point( 1280, 1968 );

	private BuildableBitmapTextureAtlas atlasNearestPremultiplyAlpha;
	private final Point sizeAtlasNearestPremultiplyAlpha = new Point( 1280, 1968 );

	private BuildableBitmapTextureAtlas atlasBilinearPremultiplyAlpha;
	private final Point sizeAtlasBilineaPremultiplyAlphar = new Point( 1280, 1968 );
	// Regions
	public ITextureRegion regionLoadingCircle;
//	public ITiledTextureRegion regionMarketShareStar;
	public ITextureRegion regionCicle38;
	public ITextureRegion regionCicle80;
	public ITextureRegion regionCicle150;
	public ITextureRegion regionCicle225;
	public ITextureRegion regionTriangle;
	public ITextureRegion regionArrow;
	public ITextureRegion regionFinger;
	public ITextureRegion regionGround;
	public ITextureRegion regionTank;
	public ITextureRegion regionButtonBg;
	public ITextureRegion regionIconPlay;
	public ITextureRegion regionBall;
	public ITextureRegion regionHeart;
	public ITextureRegion regionGameHeart;
	public ITextureRegion regionLevelButtonBg;
	public ITiledTextureRegion regionLevelButtonLevelBg;
	public ITiledTextureRegion regionLevelButtonAchievment;
	public ITextureRegion regionPrickleV108x35;
	public ITextureRegion regionCoin50;
	public ITextureRegion regionArrowRight;
	public ITextureRegion regionRoundLock;
	public ITextureRegion regionIconRight;
	public ITextureRegion regionIconList;
	public ITextureRegion regionIconMarket;
	public ITextureRegion regionIconShare;
	public ITextureRegion regionIconStar;

	public ITiledTextureRegion regionSoundOnOff;

	// Sound
	private static final int FONT_SIZE_DEFAULT = 64;
	private static final int FONT_SIZE_BUTTON = 64;
	private static final int FONT_SIZE_BUTTON_SMALL = 45;
	private static final int FONT_SIZE_ROUNDMENU = 256;
	private Font fontDefault;
	private Font fontButton;
	private Font fontButtonSmall;
	private Font fontPoint;
	private Font fontRoundMenu;

	private Sound soundIntro;

	// =======================================

	public static ResourcesManager getInstance( ) {
		return instance;
	}

	public static void prepare( Engine e, MainActivity ma, Camera c,
			VertexBufferObjectManager vertexBufferObjectManager ) {
		getInstance().engine = e;
		getInstance().activity = ma;
		getInstance().camera = c;
		getInstance().vbom = vertexBufferObjectManager;
		getInstance().resizeFactor = c.getWidth()
				/ getInstance().cameraWidthRef;
		getInstance().setVibrator();
	}

	private void setVibrator( ) {
		this.pVibrator = ( Vibrator ) this.activity
				.getSystemService( Context.VIBRATOR_SERVICE );
	}

	public Vibrator getVibrator( ) {
		return this.pVibrator;
	}

	public Engine getEngine( ) {
		return engine;
	}

	public MainActivity getActivity( ) {
		return activity;
	}

	public Camera getCamera( ) {
		return camera;
	}

	public VertexBufferObjectManager getVbom( ) {
		return vbom;
	}

	public float getResizeFactor( ) {
		return this.resizeFactor;
	}

	public int applyResizeFactor( int pSize ) {
		return ( int ) ( pSize * getResizeFactor() );
	}

	public float applyResizeFactor( float pSize ) {
		return pSize * getResizeFactor();
	}

	private void setAssetBasePath( ) {
		int cameraWidth;
		setSvgAssetBasePath( "svg/" );

		cameraWidth = ( int ) camera.getWidth();
		switch ( cameraWidth ) {
			case 480 :
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath( "gfx480/" );
				break;
			case 720 :
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath( "gfx720/" );
				break;
			case 1080 :
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath( "gfx1080/" );
				break;
			default :
				BitmapTextureAtlasTextureRegionFactory.setAssetBasePath( "gfx720/" );
				Log.v( "assetPathErro", "SomethingWrong~" );
				break;
		}
	}

	private void setSvgAssetBasePath( String pAssetBasePath ) {
		SVGBitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath( pAssetBasePath );
	}

	public void loadResources( ) {
		loadFonts();
		loadGraphicResources();
		loadSounds();
	}

	private void loadSounds( ) {
		SoundFactory.setAssetBasePath( "sfx/" );

		try {
			soundIntro = SoundFactory.createSoundFromAsset(
					this.engine.getSoundManager(), this.activity, "intro.ogg" );
			soundIntro.setLooping( false );

		} catch ( IllegalStateException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	public Sound getSoundIntro( ) {
		return soundIntro;
	}

	private void loadGraphicResources( ) {
		setAssetBasePath();
		loadGraphicResourcesNearest();
		loadGraphicResourcesBilinear();
		loadGraphicResourcesNearestPremultiplyAlpha();
		loadGraphicResourcesBilinearPremultiplyAlpha();
	}

	private void loadGraphicResourcesBilinearPremultiplyAlpha( ) {
		atlasBilinearPremultiplyAlpha = new BuildableBitmapTextureAtlas(
				this.activity.getTextureManager(),
				this.applyResizeFactor( sizeAtlasBilineaPremultiplyAlphar.x ),
				this.applyResizeFactor( sizeAtlasBilineaPremultiplyAlphar.y ),
				BitmapTextureFormat.RGBA_8888,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA );

		regionGround = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasBilinearPremultiplyAlpha,
						this.activity, "ground_1080x300.svg",
						this.applyResizeFactor( 1080 ),
						this.applyResizeFactor( 300 ) );
		regionCicle38 = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasBilinearPremultiplyAlpha,
						this.activity, "circle.svg",
						this.applyResizeFactor( 38 ),
						this.applyResizeFactor( 38 ) );
		regionCicle80 = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasBilinearPremultiplyAlpha,
						this.activity, "circle.svg",
						this.applyResizeFactor( 80 ),
						this.applyResizeFactor( 80 ) );
		regionCoin50 = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasBilinearPremultiplyAlpha,
						this.activity, "coin_50x50.svg",
						this.applyResizeFactor( 50 ),
						this.applyResizeFactor( 50 ) );

		regionRoundLock = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasBilinearPremultiplyAlpha,
						this.activity, "roundlock_300x300.svg",
						this.applyResizeFactor( 300 ),
						this.applyResizeFactor( 300 ) );

		try {
			atlasBilinearPremultiplyAlpha
					.build( new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 0, applyResizeFactor( 5 ) ) );
			atlasBilinearPremultiplyAlpha.load();
		} catch ( TextureAtlasBuilderException e ) {
			e.printStackTrace();
		}
	}

	private void loadGraphicResourcesNearestPremultiplyAlpha( ) {
		atlasNearestPremultiplyAlpha = new BuildableBitmapTextureAtlas(
				this.activity.getTextureManager(),
				this.applyResizeFactor( sizeAtlasNearestPremultiplyAlpha.x ),
				this.applyResizeFactor( sizeAtlasNearestPremultiplyAlpha.y ),
				BitmapTextureFormat.RGBA_8888,
				TextureOptions.NEAREST_PREMULTIPLYALPHA );

		regionArrow = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearestPremultiplyAlpha,
						this.activity, "arrow_63x70.svg",
						this.applyResizeFactor( 63 ), this.applyResizeFactor( 70 ) );

		regionIconPlay = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearestPremultiplyAlpha,
						this.activity, "icon_play_120x120.svg",
						this.applyResizeFactor( 120 ),
						this.applyResizeFactor( 120 ) );

		regionBall = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasNearestPremultiplyAlpha, this.activity,
				"ball_172x172.svg", this.applyResizeFactor( 172 ),
				this.applyResizeFactor( 172 ) );

		regionHeart = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasNearestPremultiplyAlpha, this.activity,
				"heart_100x80.svg", this.applyResizeFactor( 100 ),
				this.applyResizeFactor( 80 ) );

		regionGameHeart = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasNearestPremultiplyAlpha, this.activity,
				"heart_100x80.svg", this.applyResizeFactor( 63 ),
				this.applyResizeFactor( 54 ) );

		try {
			atlasNearestPremultiplyAlpha
					.build( new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 0, applyResizeFactor( 5 ) ) );
			atlasNearestPremultiplyAlpha.load();
		} catch ( TextureAtlasBuilderException e ) {
			e.printStackTrace();
		}

	}

	private void loadGraphicResourcesBilinear( ) {
		atlasBilinear = new BuildableBitmapTextureAtlas(
				this.activity.getTextureManager(),
				this.applyResizeFactor( sizeAtlasBilinear.x ),
				this.applyResizeFactor( sizeAtlasBilinear.y ),
				BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR );

		regionFinger = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasBilinear, this.activity,
						"finger_139x126.svg", this.applyResizeFactor( 139 ),
						this.applyResizeFactor( 126 ) );

		regionTank = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasBilinear, this.activity, "tank_180x125.svg",
				this.applyResizeFactor( 180 ), this.applyResizeFactor( 125 ) );

		regionLevelButtonBg = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasBilinear, this.activity, "level_button_bg_453x100.svg",
				this.applyResizeFactor( 453 ), this.applyResizeFactor( 100 ) );

		regionLevelButtonLevelBg = SVGBitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				this.atlasBilinear,
				this.activity,
				"level_button_level_bg_1x2_200x100.svg",
				this.applyResizeFactor( 200 ),
				this.applyResizeFactor( 100 ),
				2,
				1 );

		regionArrowRight = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasBilinear, this.activity, "arrowright_80x160.svg",
				this.applyResizeFactor( 80 ), this.applyResizeFactor( 160 ) );

		regionIconRight = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasBilinear, this.activity, "iconright_43x58.svg",
				this.applyResizeFactor( 43 ), this.applyResizeFactor( 58 ) );

		regionTriangle = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasBilinear, this.activity, "play_35x42.svg",
				this.applyResizeFactor( 35 ), this.applyResizeFactor( 42 ) );

		regionSoundOnOff = SVGBitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset( this.atlasBilinear, this.activity,
						"soundonoff_160x80_1x2.svg",
						this.applyResizeFactor( 160 ),
						this.applyResizeFactor( 80 ), 2, 1 );
		
		regionIconList = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(
				this.atlasBilinear, this.activity, "listicon_57x32.svg",
				this.applyResizeFactor( 57 ), this.applyResizeFactor( 32 ) );

		try {
			atlasBilinear
					.build( new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 0, applyResizeFactor( 5 ) ) );
			atlasBilinear.load();
		} catch ( TextureAtlasBuilderException e ) {
			e.printStackTrace();
		}

	}

	private void loadGraphicResourcesNearest( ) {// Default texture option
		atlasNearest = new BuildableBitmapTextureAtlas(
				this.activity.getTextureManager(),
				this.applyResizeFactor( sizeAtlasNearest.x ),
				this.applyResizeFactor( sizeAtlasNearest.y ),
				BitmapTextureFormat.RGBA_8888, TextureOptions.NEAREST );

		regionLoadingCircle = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest, this.activity,
						"loading_circle_700x700.svg",
						this.applyResizeFactor( 700 ),
						this.applyResizeFactor( 700 ) );

//		regionMarketShareStar = SVGBitmapTextureAtlasTextureRegionFactory
//				.createTiledFromAsset( this.atlasNearest, this.activity,
//						"market_share_star_btns_320x480.svg",
//						this.applyResizeFactor( 320 ),
//						this.applyResizeFactor( 480 ), 2, 3 );

		regionButtonBg = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest, this.activity,
						"button_bg_400x120.svg", this.applyResizeFactor( 400 ),
						this.applyResizeFactor( 120 ) );

		regionLevelButtonAchievment = SVGBitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				this.atlasNearest,
				this.activity,
				"level_button_achievement_1x2_100x50.svg",
				this.applyResizeFactor( 100 ),
				this.applyResizeFactor( 50 ),
				2,
				1 );

		regionCicle150 = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest,
						this.activity, "circle.svg",
						this.applyResizeFactor( 150 ),
						this.applyResizeFactor( 150 ) );

		regionCicle225 = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest,
						this.activity, "circle.svg",
						this.applyResizeFactor( 225 ),
						this.applyResizeFactor( 225 ) );

		regionPrickleV108x35 = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest,
						this.activity, "prickleV108x35.svg",
						this.applyResizeFactor( 108 ),
						this.applyResizeFactor( 35 ) );
		
		regionIconMarket = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest,
						this.activity, "marketicon_174x174.svg",
						this.applyResizeFactor( 174 ),
						this.applyResizeFactor( 174 ) );
		regionIconShare = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest,
						this.activity, "shareicon_174x174.svg",
						this.applyResizeFactor( 174 ),
						this.applyResizeFactor( 174 ) );
		regionIconStar = SVGBitmapTextureAtlasTextureRegionFactory
				.createFromAsset( this.atlasNearest,
						this.activity, "staricon_174x174.svg",
						this.applyResizeFactor( 174 ),
						this.applyResizeFactor( 174 ) );
		

		try {
			atlasNearest
					.build( new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 0, applyResizeFactor( 5 ) ) );
			atlasNearest.load();
		} catch ( TextureAtlasBuilderException e ) {
			e.printStackTrace();
		}

	}

	private void loadFonts( ) {
		FontFactory.setAssetBasePath( "fonts/" );

		final ITexture tFontTextureDefault = new BitmapTextureAtlas(
				activity.getTextureManager(),
				( int ) applyResizeFactor( camera.getWidth() ),
				( int ) applyResizeFactor( 512f ), TextureOptions.BILINEAR );

		final ITexture tFontTextureButton = new BitmapTextureAtlas(
				activity.getTextureManager(),
				( int ) applyResizeFactor( camera.getWidth() ),
				( int ) applyResizeFactor( 512f ), TextureOptions.BILINEAR );
		
		final ITexture tFontTextureButtonSmall = new BitmapTextureAtlas(
				activity.getTextureManager(),
				( int ) applyResizeFactor( camera.getWidth() ),
				( int ) applyResizeFactor( 512f ), TextureOptions.BILINEAR );

		final ITexture tFontTexturePoint = new BitmapTextureAtlas(
				activity.getTextureManager(),
				( int ) applyResizeFactor( camera.getWidth() ),
				( int ) applyResizeFactor( 512f ), TextureOptions.BILINEAR );

		this.fontDefault = FontFactory.createFromAsset(
				activity.getFontManager(), tFontTextureDefault,
				activity.getAssets(), "UbuntuB.ttf", FONT_SIZE_DEFAULT, true,
				AppColor.getInstance().WHITE.getABGRPackedInt() );
		this.fontDefault.load();

		this.fontButton = FontFactory.createFromAsset(
				activity.getFontManager(), tFontTextureButton,
				activity.getAssets(), "UbuntuB.ttf", FONT_SIZE_BUTTON, true,
				AppColor.getInstance().WHITE.getABGRPackedInt() );
		this.fontButton.load();

		this.fontPoint = FontFactory.createFromAsset(
				activity.getFontManager(), tFontTexturePoint,
				activity.getAssets(), "UbuntuB.ttf", FONT_SIZE_DEFAULT, true,
				AppColor.getInstance().BALL.getABGRPackedInt() );
		this.fontPoint.load();

		this.fontRoundMenu = FontFactory.createFromAsset(
				activity.getFontManager(), tFontTexturePoint,
				activity.getAssets(), "UbuntuB.ttf", FONT_SIZE_ROUNDMENU, true,
				AppColor.getInstance().BALL.getABGRPackedInt() );
		this.fontRoundMenu.load();
		
		this.fontButtonSmall = FontFactory.createFromAsset(
				activity.getFontManager(), tFontTextureButtonSmall,
				activity.getAssets(), "UbuntuB.ttf", FONT_SIZE_BUTTON_SMALL, true,
				AppColor.getInstance().WHITE.getABGRPackedInt() );
		this.fontButtonSmall.load();

	}

	public Font getFontDefault( ) {
		return this.fontDefault;
	}

	public Font getFontButton( ) {
		return this.fontButton;
	}
	public Font getFontButtonSmall( ) {
		return this.fontButtonSmall;
	}

	public Font getFontPoint( ) {
		return this.fontPoint;
	}

	public Font getFontRoundMenu( ) {
		return this.fontRoundMenu;
	}
}
