package group22.viking.game.controller.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import group22.viking.game.controller.GameStateManager;
import group22.viking.game.controller.VikingGame;
import group22.viking.game.view.LeaderboardScreen;
import group22.viking.game.view.LeaderboardView;
import group22.viking.game.view.MenuView;
import group22.viking.game.view.ProfileSettingsView;


public class LeaderboardState extends State {



    public LeaderboardState(VikingGame game) {
        super(new LeaderboardView(game.getBatch(), game.getCamera()), game);
        Gdx.input.setInputProcessor(view.getStage());
        addListenersToButtons();

        System.out.println("MENU STATE CREATED");
    }

    @Override
    protected void handleInput() {

    }


    public void update(float dt) {

    }



    private void addListenersToButtons() {
        ((LeaderboardView) view).getExitButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                dispose();
                System.out.println("EXIT BUTTON CLICKED");
                //GameStateManager.getInstance(game).pop();

                //todo this works but the above code doesnt... something is wrong with init or the constructor
                GameStateManager.getInstance(game).push(new MenuState(game));
            }
        });


    }



}
