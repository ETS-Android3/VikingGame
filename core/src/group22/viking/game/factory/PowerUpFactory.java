package group22.viking.game.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import group22.viking.game.powerups.IPowerUp;
import group22.viking.game.ECS.components.B2dBodyComponent;
import group22.viking.game.ECS.components.PowerUpComponent;
import group22.viking.game.ECS.components.TextureComponent;
import group22.viking.game.ECS.components.TransformComponent;
import group22.viking.game.ECS.components.TypeComponent;
import group22.viking.game.ECS.utils.BodyFactory;
import group22.viking.game.models.Assets;

public class PowerUpFactory extends AbstractFactory {


    private final World world;

    public PowerUpFactory(PooledEngine engine, World world){
        super(engine);
        this.world = world;
    }

    Entity create(Vector3 position, float scale, Texture texture, IPowerUp powerUp) {
        Entity entity = super.createEntity(TypeComponent.EntityType.POWER_UP);
        return entity
                .add(engine.createComponent(TransformComponent.class)
                        .setPosition(position)
                        .setScale(new Vector2(scale, scale))
                )
                .add(engine.createComponent(TextureComponent.class)
                        .setTextureRegion(new TextureRegion(texture))
                )
                .add(engine.createComponent(PowerUpComponent.class)
                    .setPowerUp(powerUp) // TODO
                )
                .add(engine.createComponent(B2dBodyComponent.class)
                    .setBody(BodyFactory.getInstance(world).makeCirclePolyBody(
                            position.x, position.y, texture.getWidth() / 2,
                            BodyDef.BodyType.StaticBody, true), entity)
                );
    }

    public Entity createFasterShootingPowerUp(float x, float y, IPowerUp powerUp) {
        return create(
                new Vector3(x, y, 0),
                1.0F,
                Assets.getTexture(Assets.GOAT_ICON), // TODO
                powerUp
        );
    }

    public Entity createHealthPowerUp(float x, float y, IPowerUp powerUp) {
        return create(
                new Vector3(x, y, 0),
                1.0F,
                Assets.getTexture(Assets.BAD_LOGIC), // TODO
                powerUp
        );
    }
}