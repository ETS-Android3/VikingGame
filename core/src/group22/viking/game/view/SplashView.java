package group22.viking.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import group22.viking.game.controller.VikingGame;
import group22.viking.game.models.Assets;

public class SplashView extends View {

    private Image goatIcon;

    public SplashView(SpriteBatch batch, Camera camera) {
        super(batch, camera);
        System.out.println("CONSTRUCTOR SPLASHVIEW");
        goatIcon = new Image(new Texture(Assets.GOAT_ICON));
    }

    @Override
    public void init() {

        System.out.println("SPLASH");

        //delegate input Events to all Actors
        Gdx.input.setInputProcessor(stage);

        //Goat image
        goatIcon.setWidth(200);
        goatIcon.setHeight(200);
        goatIcon.setOrigin(goatIcon.getWidth()/2,goatIcon.getHeight()/2);

        goatIcon.setPosition(VikingGame.SCREEN_WIDTH/2-100,VikingGame.SCREEN_HEIGHT+100);

        stage.addActor(goatIcon);
        stage.act(0);

        this.runInitialAnimations();
    }

    @Override
    public void runInitialAnimations() {

    }

    @Override
    void drawElements(float deltaTime) {
        Assets.FONT48.draw(batch, "Version: alpha 0.0.2", 20,80);
        stage.act(deltaTime);
        stage.draw();
    }

    public Image getGoatIcon() {
        return goatIcon;
    }
}
