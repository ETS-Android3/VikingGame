package group22.viking.game.view;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import group22.viking.game.controller.VikingGame;
import group22.viking.game.models.Assets;

public class MenuView extends View {

    private TextButton tutorialButton;
    private TextButton hostButton;
    private TextButton joinButton;
    private TextButton exitButton;
    private TextButton leaderboardButton;
    private TextButton muteButton;
    private TextButton practiceButton;

    private ImageButton profileButton;

    private TextField joinTextField;

    private Animation[] animations;

    TextureRegion profileTextureRegion;
    TextureRegionDrawable profileTextureRegionDrawable;

    /**
     * Simple storage class for animation info.
     */
    abstract class Animation {
        Image image;
        public float status;
        public float step;
        public float max;
        boolean animateX;

        public Animation(Image image, float x, float y, float step, float max, boolean animateX) {
            this.image = image;
            this.image.setPosition(x, y);
            this.status = 0F;
            this.step = step;
            this.max = max;
            this.animateX = animateX;
        }

        public void makeStep(float deltaTime) {
            status += step * deltaTime;
            if(animateX) image.setX(image.getX() + step * deltaTime);
            else image.setY(image.getY() + step * deltaTime);

            this.stepChanges();
        }

        abstract void stepChanges();
    }

    class LoopAnimation extends Animation {
        public LoopAnimation(Image image, float x, float y, float step, float max, boolean animateX) {
            super(image, x, y, step, max, animateX);
        }

        public void stepChanges() {
            if (status > max) {
                status -= max;
                if(animateX) image.setX(image.getX() - max);
                else image.setY(image.getY() - max);
            }
        }
    }

    class BouncingAnimation extends Animation {
        public BouncingAnimation(Image image, float x, float y, float step, float max, boolean animateX) {
            super(image, x, y, step, max, animateX);
        }

        public void stepChanges() {
            if (Math.abs(status) > max) {
                step *= (status < 0) ^ (step < 0) ? 1 : -1;
            }
        }
    }

    public MenuView(SpriteBatch batch, Camera camera) {
        super(batch, camera);
        this.init();
    }

    @Override
    public void init() {
        // stage clear to make sure there aren't any further animations
        stage.clear();

        createBackground();
        createHeader();
        createButtons();
        createTextField();

        // TODO continue...

        runInitialAnimations();

        stage.act(0);
    }

    private void createButtons() {
        //todo set the texture to the user specific profile image
        profileTextureRegion = new TextureRegion(Assets.getTexture(Assets.WIZARDSPRITEHEAD));
        profileTextureRegionDrawable = new TextureRegionDrawable(profileTextureRegion);

        tutorialButton = ViewComponentFactory.createTextButton(
                "Tutorial",
                new Vector2(150, VikingGame.SCREEN_HEIGHT/2+80-50),
                ViewComponentFactory.BIG_BUTTON_SIZE
        );

        practiceButton = ViewComponentFactory.createTextButton(
                "Practice",
                new Vector2(150, VikingGame.SCREEN_HEIGHT / 2 - 80 - 150 - 50),
                ViewComponentFactory.BIG_BUTTON_SIZE);


        hostButton = ViewComponentFactory.createTextButton(
                "Host",
                new Vector2(VikingGame.SCREEN_WIDTH/2+(VikingGame.SCREEN_WIDTH/2-700-150),VikingGame.SCREEN_HEIGHT/2-80-150-50),
                ViewComponentFactory.BIG_BUTTON_SIZE
        );

        joinButton = ViewComponentFactory.createTextButton(
                "Join",
                new Vector2(VikingGame.SCREEN_WIDTH/2+(VikingGame.SCREEN_WIDTH/2-700-150)+ 530 +20,
                        VikingGame.SCREEN_HEIGHT/2+80-50),
                ViewComponentFactory.SMALL_BUTTON_SIZE);

        exitButton = ViewComponentFactory.createTextButton(
                "Exit",
                new Vector2(150, VikingGame.SCREEN_HEIGHT - 200),
                ViewComponentFactory.VERY_SMALL_BUTTON_SIZE);

        muteButton = ViewComponentFactory.createTextButton(
                "U",
                new Vector2(VikingGame.SCREEN_WIDTH - 120 - 60, 50),
                ViewComponentFactory.VERY_SMALL_BUTTON_SIZE);

        leaderboardButton = ViewComponentFactory.createTextButton(
                "L",
                new Vector2(VikingGame.SCREEN_WIDTH - 120 - 60 - 120 - 60, 50),
                ViewComponentFactory.VERY_SMALL_BUTTON_SIZE);

        profileButton = ViewComponentFactory.createImageButton(
                profileTextureRegionDrawable,
                // new Vector2(VikingGame.SCREEN_WIDTH/2-profileButton.getWidth()/2,
                new Vector2(VikingGame.SCREEN_WIDTH/2-500F/2,
                        //VikingGame.SCREEN_HEIGHT/2-profileButton.getHeight()/2-80),
                        VikingGame.SCREEN_HEIGHT/2-500F/2-80),
                ViewComponentFactory.PROFILE_IMAGE_SIZE);


        stage.addActor(tutorialButton);
        stage.addActor(practiceButton);
        stage.addActor(hostButton);
        stage.addActor(profileButton);
        stage.addActor(joinButton);
        stage.addActor(leaderboardButton);
        stage.addActor(exitButton);
        stage.addActor(muteButton);
    }

    private void createTextField() {

        joinTextField = ViewComponentFactory.createTextField(
                "Enter PIN",
                new Vector2(VikingGame.SCREEN_WIDTH / 2 + (VikingGame.SCREEN_WIDTH / 2 - 700 - 150),
                        VikingGame.SCREEN_HEIGHT / 2 + 80 - 50),
                new Vector2(530, 150)
        );

        stage.addActor(joinTextField);
    }

    private void createBackground() {
        Image waveDark = new Image(Assets.getTexture(Assets.WAVEDARK));
        stage.addActor(waveDark);

        Image castle = new Image(Assets.getTexture(Assets.CASTLE));
        castle.setWidth(600);
        castle.setHeight(500);
        stage.addActor(castle);

        Image waveMedium = new Image(Assets.getTexture(Assets.WAVEMEDIUM));
        stage.addActor(waveMedium);

        Image vikingShip = new Image(Assets.getTexture(Assets.VIKINGSHIP));
        vikingShip.setWidth(600);
        vikingShip.setHeight(500);
        stage.addActor(vikingShip);

        Image waveLight = new Image(Assets.getTexture(Assets.WAVELIGHT));
        stage.addActor(waveLight);

        Image waveVeryLight = new Image(Assets.getTexture(Assets.WAVEVERYLIGHT));
        stage.addActor(waveVeryLight);

        this.animations = new Animation[]{
                new LoopAnimation(waveDark, -600, 0, 8, 296, true),
                new BouncingAnimation(castle, 1500, 290, 10, 7, false),
                new LoopAnimation(waveMedium, -300, 0, 16, 296, true),
                new BouncingAnimation(vikingShip, 200, 200, 10, 15, true),
                new BouncingAnimation(vikingShip, 200, 200, 30, 15, false),
                new LoopAnimation(waveLight, -600, 0, 32, 296, true),
                new LoopAnimation(waveVeryLight, -400, 0, 64, 296, true)
        };

    }

    private void createHeader() {
        //header
        Image vikingHeader = new Image(Assets.getTexture(Assets.VIKINGHEADER));
        vikingHeader.setPosition(VikingGame.SCREEN_WIDTH/2-430,VikingGame.SCREEN_HEIGHT -250);
        vikingHeader.setWidth(660);
        vikingHeader.setHeight(200);
        Image stopHeader = new Image(Assets.getTexture(Assets.STOPHEADER));
        stopHeader.setPosition(VikingGame.SCREEN_WIDTH/2,VikingGame.SCREEN_HEIGHT -380);
        stopHeader.setWidth(430);
        stopHeader.setHeight(300);
        stage.addActor(vikingHeader);
        stage.addActor(stopHeader);
    }

    @Override
    public void runInitialAnimations() {
        tutorialButton.addAction(ViewComponentFactory.FADE_IN_ANIMATION);
        practiceButton.addAction(ViewComponentFactory.FADE_IN_ANIMATION);
        hostButton.addAction(ViewComponentFactory.FADE_IN_ANIMATION);
        joinTextField.addAction(ViewComponentFactory.FADE_IN_ANIMATION);
        profileButton.addAction(ViewComponentFactory.FADE_IN_ANIMATION);
        joinButton.addAction(ViewComponentFactory.FADE_IN_ANIMATION);
        leaderboardButton.addAction(ViewComponentFactory.FADE_IN_ANIMATION);
        exitButton.addAction(ViewComponentFactory.FADE_IN_ANIMATION);

    }

    void drawElements(float deltaTime) {

        for (Animation animation : animations) {
            animation.makeStep(deltaTime);
        }

        stage.act(deltaTime);
        stage.draw();
    }

    public TextButton getTutorialButton() {
        return tutorialButton;
    }

    public TextButton getHostButton() {
        return hostButton;
    }

    public TextButton getJoinButton() {
        return joinButton;
    }

    public TextButton getExitButton() {
        return exitButton;
    }

    public TextButton getLeaderboardButton() {
        return leaderboardButton;
    }

    public TextButton getMuteButton() {
        return muteButton;
    }

    public TextButton getPracticeButton() {
        return practiceButton;
    }

    public TextField getJoinTextField() {
        return joinTextField;
    }

    public ImageButton getProfileButton() {
        return profileButton;
    }
}