package group22.viking.game.ECS.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import group22.viking.game.ECS.components.TextureComponent;
import group22.viking.game.controller.VikingGame;
import group22.viking.game.factory.TextureFactory;
import group22.viking.game.input.InputController;
import group22.viking.game.ECS.components.LinearProjectileComponent;
import group22.viking.game.ECS.components.PlayerComponent;
import group22.viking.game.ECS.components.TransformComponent;
import group22.viking.game.factory.ProjectileFactory;

public class PlayerControlSystem extends IteratingSystem {

    private final ComponentMapper<PlayerComponent> cmPlayerComponent;
    private final ComponentMapper<TransformComponent> cmTransformComponent;
    private final ComponentMapper<TextureComponent> cmTextureComponent;

    private final InputController input;
    private ProjectileFactory projectileFactory;
    private final World world;

    private float timeSinceFired = 0;

    public PlayerControlSystem(InputController controller, World world) {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());
        this.world = world;
        cmPlayerComponent = ComponentMapper.getFor(PlayerComponent.class);
        cmTransformComponent = ComponentMapper.getFor(TransformComponent.class);
        cmTextureComponent = ComponentMapper.getFor(TextureComponent.class);
        input = controller;
    }

    @Override
    protected void processEntity(com.badlogic.ashley.core.Entity entity, float deltaTime) {
        PlayerComponent pComp = cmPlayerComponent.get(entity);
        TransformComponent tComp = cmTransformComponent.get(entity);

        //if health is below or equal 0
        if(checkHealth(pComp)){
            //return;
        }

        updateHealthBar(pComp.healthBar, pComp.getHealth());

        timeSinceFired += deltaTime;
        if(input.isMouse1Down) {
            Vector2 pos = input.mouseLocation;
            tComp.rotation  = calculateAngle(pos, new Vector2(tComp.position.x, tComp.position.y));

            if(timeSinceFired > pComp.fireRate)
                shootBullet(tComp, getLookVector(pos, new Vector2(tComp.position.x, tComp.position.y)));
        }
    }

    private void updateHealthBar(Entity healthBar, float health) {
        System.out.println("UPDATE HEALTH BAR: " + health);
        TransformComponent transformComponent = cmTransformComponent.get(healthBar);
        TextureComponent textureComponent = cmTextureComponent.get(healthBar);

        transformComponent.scale.y = health / PlayerComponent.MAX_HEALTH *
                TextureFactory.HEALTH_BAR_SCALE;

        transformComponent.position.y = (VikingGame.SCREEN_HEIGHT / 2) - //screen middle
                ((1 - (health / PlayerComponent.MAX_HEALTH)) * // inverted health
                        (textureComponent.textureRegion.getRegionHeight() *
                                TextureFactory.HEALTH_BAR_SCALE * // sprite size
                                0.5F)); // half sprite reduction
    }

    private boolean checkHealth(PlayerComponent player)
    {
        return player.getHealth() <= 0;
    }

    private void shootBullet(TransformComponent playerTransform, Vector2 direction)
    {
        if(projectileFactory == null)
            projectileFactory = new ProjectileFactory((PooledEngine) getEngine(), world);

        direction = direction.mulAdd(new Vector2(0,0), -1);

        com.badlogic.ashley.core.Entity projectile = projectileFactory.createLinearProjectile(playerTransform.position.x, playerTransform.position.y);
        projectile.getComponent(LinearProjectileComponent.class).setDirection(new Vector3(direction.x, direction.y, 0));
        getEngine().addEntity(projectile);

        timeSinceFired = 0;
    }

    private float calculateAngle(Vector2 mousePos, Vector2 playerPos)
    {
        float radians = (float)Math.atan2(mousePos.x - playerPos.x, mousePos.y - playerPos.y);
        //add 90 degrees offset to correct the angle
        return radians * MathUtils.radiansToDegrees - 150;
    }

    private Vector2 getLookVector(Vector2 mousePos, Vector2 playerPos)
    {
        return new Vector2(playerPos.x - mousePos.x, mousePos.y - playerPos.y).nor();
    }
}
