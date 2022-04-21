package group22.viking.game.controller.states;

import com.badlogic.ashley.core.Entity;

import group22.viking.game.controller.VikingGame;
import group22.viking.game.controller.firebase.FirebaseDocument;
import group22.viking.game.controller.firebase.Lobby;
import group22.viking.game.controller.firebase.OnCollectionUpdatedListener;
import group22.viking.game.controller.firebase.PlayerStatus;
import group22.viking.game.controller.firebase.PlayerStatusCollection;
import group22.viking.game.controller.firebase.Profile;
import group22.viking.game.view.ViewComponentFactory;

public class OnlinePlayState extends AbstractPlayState{

    private PlayerStatusCollection playerStatusCollection;
    private Entity opponentHealthBar;

    public OnlinePlayState(VikingGame game, Lobby lobby) {
        super(game);
        this.playerStatusCollection = game.getPlayerStatusCollection();
        onlineInit(lobby);
    }

    private void onlineInit(final Lobby lobby) {
        initOpponent(lobby.isHost() ?
                game.getProfileCollection().getHostProfile() :
                game.getProfileCollection().getGuestProfile());

        playerStatusCollection.createOwnStatus(
                lobby.getOwnId(),
                lobby.getOpponentId(),
                new OnCollectionUpdatedListener() {
                    @Override
                    public void onSuccess(FirebaseDocument document) {
                        addOpponentListener(lobby);
                    }

                    @Override
                    public void onFailure() {
                        ViewComponentFactory.createErrorDialog().show(getView().getStage());
                    }
                }
        );
    }

    private void addOpponentListener(Lobby lobby) {
        playerStatusCollection.addListenerToOpponentStatus(
                lobby.getOwnId(),
                lobby.getOpponentId(),
                new OnCollectionUpdatedListener() {
                    @Override
                    public void onSuccess(FirebaseDocument document) {
                        PlayerStatus opponent = (PlayerStatus) document;
                        if(opponent.isDead()) {
                            // TODO end game
                            return;
                        }
                        displayOpponentHealth(opponent.getHealth());
                    }

                    @Override
                    public void onFailure() {
                        ViewComponentFactory.createErrorDialog().show(getView().getStage());
                    }
                }
        );
    }

    /**
     * Display opponent avatar and name.
     *
     * @param profile {Profile} opponent profile
     */
    private void initOpponent(Profile profile) {
        opponentHealthBar = textureFactory.createHealthFillingRight();
        engine.addEntity(opponentHealthBar);
        engine.addEntity(textureFactory.createHealthBarRight());
        //engine.addEntity((textureFactory.createAvatarHeadRight((int) profile.getAvatarId())));
    }

    private void displayOpponentHealth(long health) {
        // TODO gui call
    }

    /**
     * Reduce own health. Different behavior depending on type (online vs offline).
     *
     * @param damage {long}
     * @return {long} new health
     */
    private long reduceOwnHealth(long damage) {
        return playerStatusCollection.reduceOwnHealth(damage);
    }

}