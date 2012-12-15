package pl.czyzycki.towerdef.gameplay.entities;


import pl.czyzycki.towerdef.gameplay.GameplayScreen;
import pl.czyzycki.towerdef.gameplay.helpers.Circle;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Klasa bazowa wie�yczek
 * @author Ciziu
 *
 */
abstract public class Tower {

	public static Sprite basicSprite; // Sprite placeholder
	
	// Pusta lista wrog�w, s�u��ca za placeholder niekt�rym wie��m
	protected static Array<Enemy> emptyEnemyList = new Array<Enemy>(false,1);
	
	// Enum cech mo�liwych do upgrade'owania
	public enum Upgradeable {
		RANGE,
		COOLDOWN,
		MULTIPLIER,
		DAMAGE
	}
	// Pojedynczy upgrade. Szkic klasy :P
	static public class Upgrade {
		static public class Level {
			public float value, cost;
		}
		public Level[] levels;
		public Upgradeable upgraded;
	}
	//Enum mo�liwych grup cel�w wie�yczki
	public enum Targeted {
		GROUND,
		AIRBORNE,
		BOTH
	}
	
	GameplayScreen screen;
	public Upgrade[] upgrades; // Tablica ugrade'�w danej wie�y (wype�niane z JSONa)
	public int[] upgradeLevelIters = new int[16]; // Poziom upgrade dla konkretnej wie�yczki. 
	Targeted targeted; // Wybrana grupa cel�w (ignorowane w SlowdownTower, zgodnie z projektem)
	Vector2 pos;
	Circle range;
	Sprite sprite;
	float cooldown, timer; // W cooldown parametr, w timer jego licznik
	
	public String icon;
	public String groupIcon;
	
	public String textureFileName;

	public float cost;
	
	StringBuilder timerText;
	
	public Tower() {
		
	}
	
	public Vector2 getPos() {
		return pos;
	}
	
	// Inicjalizacja pami�ci nowego obiektu dodawanego do puli
	protected void _init(GameplayScreen screen) {
		this.screen = screen;
		pos = new Vector2();
		range = new Circle();
		timerText = new StringBuilder();
		range.pos = pos;
		sprite = new Sprite();
	}
	
	// Pseudokonstruktor obiektu wyj�tego z puli
	protected void _set(Tower tower, float x, float y) {
		upgrades = tower.upgrades;
		for(int i=0; i<upgradeLevelIters.length; i++) {
			upgradeLevelIters[i] = 0;
		}
		targeted = tower.targeted;
		pos.set(x, y);
		range.radius = tower.range.radius;
		cooldown = tower.cooldown;
		timer = 0f;
		cost = tower.cost;
		sprite.set(tower.sprite);
		sprite.setPosition(pos.x-sprite.getWidth()/2f, pos.y-sprite.getHeight()/2f);
	}
	
	public void loadSprite(TextureAtlas texAtlas) {
		sprite = new Sprite(texAtlas.createSprite(textureFileName));
	}
	
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public boolean collision(float x, float y) {
		float radius = sprite.getWidth()/2.0f;
		float dx = x - pos.x;
		float dy = y - pos.y;
		return radius*radius > dx*dx + dy*dy;
	}
	
	abstract public void update(float dt);
	
	public void debugDraw(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.FilledCircle);
		shapeRenderer.setColor(0,0,1,0.2f);
		range.draw(shapeRenderer);
		shapeRenderer.end();
	}
	
	public void debugText(SpriteBatch batch, BitmapFont debugFont) {
		timerText.length = 0;
		timerText.append(timer);
		debugFont.draw(batch, timerText, pos.x + 30f, pos.y - 30f);
	}
	
	abstract public Tower obtainCopy(float x, float y);
}