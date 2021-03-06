package pl.czyzycki.towerdef.gameplay.entities;


import pl.czyzycki.towerdef.TowerDef;
import pl.czyzycki.towerdef.TowerDef.GameSound;
import pl.czyzycki.towerdef.gameplay.helpers.Circle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Pocisk naprowadzany na konkretny cel, ale zadaj�cy obra�enia na danym obszarze.
 * Gdy cel zginie od innych obra�e�, pocisk wybucha.
 *
 */
public class Bullet {
	
	public static Sprite basicSprite;
	
	Array<Enemy> targetedEnemies, targetedEnemies2;
	
	Enemy target;
	Circle hitZone, blastZone;
	Vector2 pos,direction;
	Sprite sprite;
	float damage, speed;
	float timeToEndAnim;
	boolean exploded;
	
	public Bullet() {}
	
	public Bullet(boolean activeBullet) {
		hitZone = new Circle();
		blastZone = new Circle();
		if(activeBullet) {
			pos = new Vector2();
			direction = new Vector2();
			hitZone.pos = pos;
			blastZone.pos = pos;
			sprite = new Sprite(basicSprite);
		}
	}
	
	public void set(Bullet modelBullet) {
		targetedEnemies = modelBullet.targetedEnemies;
		targetedEnemies2 = modelBullet.targetedEnemies2;
		hitZone.radius = modelBullet.hitZone.radius;
		blastZone.radius = modelBullet.blastZone.radius;
		damage = modelBullet.damage;
		speed = modelBullet.speed;
	}
	
	public Bullet set(Bullet modelBullet, BulletTower tower) {
		set(modelBullet);
		damage = tower.getDamage();
		pos.set(tower.pos);
		direction.set(tower.direction);
		sprite.setPosition(this.pos.x, this.pos.y);
		sprite.setRotation(this.direction.angle()-90f);
		target = tower.target;
		exploded = false;
		return this;
	}
	
	/**
	 * @return Do usuni�cia?
	 */
	public boolean update(float dt) {
		if(exploded) {
			timeToEndAnim -= dt;
			if(timeToEndAnim <= 0)
				return true;
			else
				return false;
		}
		
		direction.set(target.pos.tmp().sub(pos)).nor();
		pos.add(direction.tmp().mul(speed*dt));
		if(!target.alive || Circle.colliding(hitZone, target.hitZone)) {
			TowerDef.getGame().playSound(GameSound.BULLET_HIT);
			for(Enemy enemy : targetedEnemies) {
				if(Circle.colliding(blastZone, enemy.hitZone)) enemy.takeHit(damage);
			}
			for(Enemy enemy : targetedEnemies2) {
				if(Circle.colliding(blastZone, enemy.hitZone)) enemy.takeHit(damage);
			}
			exploded = true;
			timeToEndAnim = 1;
			return false;
		}
		sprite.setPosition(pos.x, pos.y);
		sprite.setRotation(direction.angle()-90f);
		return false;
	}
	
	public void drawExplosion(ShapeRenderer shapeRenderer) {
		if(exploded) {
			shapeRenderer.setColor(1f, 1f, 0.5f, 0.4f*(float)Math.sin(timeToEndAnim/Math.PI));
			blastZone.draw(shapeRenderer);
		}
	}
	
	public void draw(SpriteBatch batch) {
		if(!exploded)
			sprite.draw(batch);
	}
	
	public void debugDraw(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(1, 1, 0, 0.3f);
		blastZone.draw(shapeRenderer);
		shapeRenderer.setColor(1, 0, 0, 0.2f);
		hitZone.draw(shapeRenderer);
	}
	
	static public class BulletPool extends Pool<Bullet> {

		public BulletPool() {
			super(20);
		}
		
		@Override
		protected Bullet newObject() {
			return new Bullet(true);
		}
		
	}
}
