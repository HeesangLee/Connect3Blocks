package dalcoms.pub.connect3blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.andengine.util.color.Color;

public class AppColor {
	private static final AppColor instance = new AppColor();
	private final float COLOR_8BIT = 255.0f;

	private List<Color> listColorTheme;

	public static AppColor getInstance( ) {
		return instance;
	}

	public final Color APP_BACKGROUND = new Color( 247f / COLOR_8BIT, 247f / COLOR_8BIT, 247f / COLOR_8BIT,
			1f );
	public final Color BTN_BG = new Color( 231f / COLOR_8BIT, 231f / COLOR_8BIT, 231f / COLOR_8BIT,
			1f );
	public final Color COMPANY_TEXT_HOMESCENE = new Color( 87f / COLOR_8BIT, 87f / COLOR_8BIT, 87f / COLOR_8BIT,
			1f );
	public final Color GRAY_127 = new Color( 127f / COLOR_8BIT, 127f / COLOR_8BIT, 127f / COLOR_8BIT,
			1f );
	
	public final Color WHITE = new Color( 1f, 1f, 1f, 1f );
	public final Color BLACK = new Color( 0, 0, 0, 1f );
	public final Color RED = new Color( 1f, 0f, 0f, 1f );
	public final Color GUIDE_TXT = new Color( 26f / COLOR_8BIT, 26f / COLOR_8BIT, 26f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );
	public final Color BUTTON_GRAY = new Color( 111f / COLOR_8BIT, 111f / COLOR_8BIT, 111f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );
	public final Color BUTTON_BG_NORMAL = new Color( 170f / COLOR_8BIT, 170f / COLOR_8BIT, 170f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );
	public final Color BUTTON_BG_SELECTED = new Color( 207f / COLOR_8BIT, 207f / COLOR_8BIT,
			207f / COLOR_8BIT, COLOR_8BIT / COLOR_8BIT );
	public final Color BUTTON_TXT_NORMAL = new Color( COLOR_8BIT / COLOR_8BIT, COLOR_8BIT / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT, COLOR_8BIT / COLOR_8BIT );
	public final Color BUTTON_TXT_SELECTEDL = new Color( 51f / COLOR_8BIT, 51f / COLOR_8BIT,
			51f / COLOR_8BIT, COLOR_8BIT / COLOR_8BIT );
	public final Color ENERGY_DISCHARGE = new Color( 51f / COLOR_8BIT, 51f / COLOR_8BIT, 51f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );
	public final Color ENERGY_CHRGE = new Color( 197f / COLOR_8BIT, 197f / COLOR_8BIT, 197f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );
	public final Color BUTTON_PLAY_BLACK = new Color( 0f / COLOR_8BIT, 0f / COLOR_8BIT, 0f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );

	public final Color BULLET = new Color( 51f / COLOR_8BIT, 51f / COLOR_8BIT, 51f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );

	public final Color FONT_DEFAULT = new Color( 51f / COLOR_8BIT, 51f / COLOR_8BIT, 51f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );

	public final Color LEVEL_BOX = new Color( 51f / COLOR_8BIT, 51f / COLOR_8BIT, 51f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );

	public final Color BALL = new Color( 51f / COLOR_8BIT, 51f / COLOR_8BIT, 51f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );

	public final Color RECT_LOADING = new Color( 44f / COLOR_8BIT, 62f / COLOR_8BIT, 80f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );
	public final Color APP_THEME = new Color( 44f / COLOR_8BIT, 62f / COLOR_8BIT, 80f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );

	public final Color ROUND_IMG_BG = new Color( 209f / COLOR_8BIT, 213f / COLOR_8BIT, 216f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );
	public final Color ROUND_NUMPOINT = new Color( 71f / COLOR_8BIT, 85f / COLOR_8BIT, 119f / COLOR_8BIT,
			COLOR_8BIT / COLOR_8BIT );

	private ArrayList<Color> PLAY_BUTTONS = new ArrayList<Color>() {
		{
			add( new Color( 44f / COLOR_8BIT, 130f / COLOR_8BIT, 201f / COLOR_8BIT, 255f / COLOR_8BIT ) );
			add( new Color( 147f / COLOR_8BIT, 101f / COLOR_8BIT, 184f / COLOR_8BIT, 255f / COLOR_8BIT ) );
			add( new Color( 235f / COLOR_8BIT, 107f / COLOR_8BIT, 86f / COLOR_8BIT, 255f / COLOR_8BIT ) );
			add( new Color( 250f / COLOR_8BIT, 197f / COLOR_8BIT, 28f / COLOR_8BIT, 255f / COLOR_8BIT ) );
		}
	};

	public final Color[] THEME = {
			new Color( 255f / COLOR_8BIT, 0f / COLOR_8BIT, 0f / COLOR_8BIT, 255f / COLOR_8BIT ),
			new Color( 255f / COLOR_8BIT, 102f / COLOR_8BIT, 0f / COLOR_8BIT, 255f / COLOR_8BIT ),
			new Color( 68f / COLOR_8BIT, 120f / COLOR_8BIT, 33f / COLOR_8BIT, 255f / COLOR_8BIT ),
			new Color( 0f / COLOR_8BIT, 136f / COLOR_8BIT, 170f / COLOR_8BIT, 255f / COLOR_8BIT ),
			new Color( 170f / COLOR_8BIT, 0f / COLOR_8BIT, 136f / COLOR_8BIT, 255f / COLOR_8BIT ) };

	public final Color[] LOADING_CIRCLES = {
			new Color( 0f / COLOR_8BIT, 0f / COLOR_8BIT, 0f / COLOR_8BIT, 150f / COLOR_8BIT ),
			new Color( 0f / COLOR_8BIT, 0f / COLOR_8BIT, 0f / COLOR_8BIT, 125f / COLOR_8BIT ),
			new Color( 0f / COLOR_8BIT, 0f / COLOR_8BIT, 0f / COLOR_8BIT, 100f / COLOR_8BIT ),
			new Color( 0f / COLOR_8BIT, 0f / COLOR_8BIT, 0f / COLOR_8BIT, 75f / COLOR_8BIT ) };

	public final Color[] SPARK_EFFECT = {
			new Color( 250f / COLOR_8BIT, 197f / COLOR_8BIT, 28f / COLOR_8BIT, 255f / COLOR_8BIT ),
			new Color( 243f / COLOR_8BIT, 121f / COLOR_8BIT, 52f / COLOR_8BIT, 255f / COLOR_8BIT ),
			new Color( 247f / COLOR_8BIT, 218f / COLOR_8BIT, 100f / COLOR_8BIT, 255f / COLOR_8BIT ),
			new Color( 251f / COLOR_8BIT, 160f / COLOR_8BIT, 38f / COLOR_8BIT, 255f / COLOR_8BIT ) };

	public void shuffleThemeColor( ) {
		listColorTheme = Arrays.asList( THEME );
		Collections.shuffle( listColorTheme );
	}

	public Color getShuffledThemeColor( int pIndex ) {
		return listColorTheme.get( pIndex );
	}

	public Color getShuffledPlayButtonColor( ) {
		Collections.shuffle( PLAY_BUTTONS );
		return PLAY_BUTTONS.get( 0 );
	}

	public Color getRandomSparkEffectColor( ) {
		Random rand = new Random();
		int index = rand.nextInt( SPARK_EFFECT.length );
		return SPARK_EFFECT[index];
	}
}