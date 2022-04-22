package group22.viking.game.controller.states;

import com.badlogic.ashley.core.Entity;

import group22.viking.game.controller.GameStateManager;
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

    // opponent health bar
    private Entity opponentHealthBar;
    public OnlinePlayState(VikingGame game, Lobby lobby) {
        super(game, Type.ONLINE);

        this.playerStatusCollection = game.getPlayerStatusCollection();
        this.profileCollection = game.getProfileCollection();

        onlineInit(lobby);
    }

    private void onlineInit(final Lobby lobby) {
        initOpponent(lobby.isHost() ?
                game.getProfileCollection().getProfile(lobby.getGuestId()) :
                game.getProfileCollection().getProfile(lobby.getHostId()));

        playerStatusCollection.addListenerToOpponentStatus(
                lobby.getOwnId(),
                lobby.getOpponentId(),
                new OnCollectionUpdatedListener() {
                    @Override
                    public void onSuccess(FirebaseDocument document) {
                        PlayerStatus opponent = (PlayerStatus) document;
                        if(!opponent.isLoaded()) return;
                        if(opponent.isDead()) {
                            System.out.println("OPPONENT DEAD: " + opponent.isDead());
                            handleOpponentDeath();
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
        System.out.println(profile.getName());
        engine.addEntity((textureFactory.createAvatarHeadRight((int) profile.getAvatarId())));
    }

    private void displayOpponentHealth(long health) {
        System.out.println("OPPONENT:" + health);
        // TODO gui call
        textureFactory.updateHealthBar(opponentHealthBar, health);
    }

    @Override
    public void handleLocalDeath() {
        playerStatusCollection.setOwnDeathAndFinish();
        GameStateManager.getInstance().pop();
    }

    public void handleOpponentDeath() {
        GameStateManager.getInstance().pop();
    }
}
