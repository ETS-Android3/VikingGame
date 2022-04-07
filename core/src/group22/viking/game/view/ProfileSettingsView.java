package group22.viking.game.view;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import group22.viking.game.controller.VikingGame;
import group22.viking.game.models.Assets;


public class ProfileSettingsView extends View {

    //Images
    private Image profileImage;

    //buttons
    private TextButton exitButton;
    private TextButton leftButton;
    private TextButton rightButton;
    private TextButton changeNameButton;

    //Text Fields
    private TextField nameField;

    public ProfileSettingsView(SpriteBatch batch, Camera camera) {
        super(batch, camera);
        this.init();
    }

    @Override
    public void init() {
        // stage clear to make sure there aren't any further animations
        stage.clear();

        //todo get img file from db or json log file
        profileImage = new Image(Assets.getTexture(Assets.WIZARDSPRITEHEAD));
        profileImage.setWidth(400);
        profileImage.setHeight(400);
        profileImage.setPosition(VikingGame.SCREEN_WIDTH/4,
                VikingGame.SCREEN_HEIGHT-profileImage.getHeight()-150);
        profileImage.addAction(sequence(alpha(0),parallel(fadeIn(0.5f),moveBy(0,-20,.5f, Interpolation.pow5Out))));
        stage.addActor(profileImage);




        createButtons();
        createTextField();
        // TODO continue...

        runInitialAnimations();

        stage.act(0);
    }


    private void createButtons() {
        //only for profile
        Vector2 carouselButtonSize = new Vector2(80,profileImage.getHeight());

        //for profile and leaderboard
        Vector2 exitButtonSize = new Vector2(150,VikingGame.SCREEN_HEIGHT-300);

        exitButton = ViewComponentFactory.createTextButton(
                "<",
                new Vector2(50, 150),
                exitButtonSize
        );


        leftButton = ViewComponentFactory.createTextButton(
                "<",
                new Vector2(profileImage.getX() - carouselButtonSize.x,
                        VikingGame.SCREEN_HEIGHT - profileImage.getHeight() - 150),
                carouselButtonSize
        );


        rightButton = ViewComponentFactory.createTextButton(
                ">",
                new Vector2(profileImage.getX() + profileImage.getWidth(),
                        VikingGame.SCREEN_HEIGHT - profileImage.getHeight() - 150),
                carouselButtonSize
        );

        changeNameButton = ViewComponentFactory.createTextButton(
                "Submit",
                new Vector2(profileImage.getX() + profileImage.getWidth() + carouselButtonSize.x + 100 + 600 + 50,
                        VikingGame.SCREEN_HEIGHT - profileImage.getHeight() - 150),
                new Vector2(600F / 3, 150)
        );



        stage.addActor(exitButton);
        stage.addActor(leftButton);
        stage.addActor(rightButton);
        stage.addActor(changeNameButton);

    }

    private void createTextField() {

        nameField = ViewComponentFactory.createTextField(
                "",
                new Vector2(profileImage.getX() + profileImage.getWidth() + rightButton.getWidth() + 100,
                        VikingGame.SCREEN_HEIGHT - profileImage.getHeight() - 150),
                new Vector2(600, 150)
        );

        //todo get name from Database or json log file
        nameField.setText("Caio");

        stage.addActor(nameField);

    }


    @Override
    public void runInitialAnimations() {
        Action fadeInAnimation = sequence(alpha(0),
                parallel(fadeIn(0.5f),
                        moveBy(0,-20,.5f, Interpolation.pow5Out)
                ));

        exitButton.addAction(fadeInAnimation);
        leftButton.addAction(fadeInAnimation);
        rightButton.addAction(fadeInAnimation);
        nameField.addAction(fadeInAnimation);
        changeNameButton.addAction(fadeInAnimation);
        profileImage.addAction(fadeInAnimation);
    }

    @Override
    void drawElements(float deltaTime) {

        Assets.FONT48.draw(batch, "Profile Settings State", 20,80);
        stage.act(deltaTime);
        stage.draw();
    }

    public TextButton getExitButton() {
        return exitButton;
    }

    public TextButton getLeftButton() {
        return leftButton;
    }

    public TextButton getRightButton() {
        return rightButton;
    }

    public TextButton getChangeNameButton() {
        return changeNameButton;
    }

    public TextField getNameField() {
        return nameField;
    }
}