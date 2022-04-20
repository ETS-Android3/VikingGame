package group22.viking.game.controller.firebase;

import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import group22.viking.game.controller.VikingGame;

/**
 * The ProfileCollection follows the concept: First write data in the database, and save it to the
 * local collection AFTER a confirmation of the server.
 */
public class ProfileCollection extends FirebaseCollection{

    private String hostId;
    private String guestId;
    private String localPlayerId;
    private final Preferences preferences;

    private ArrayList<String> leaderboard;

    public ProfileCollection(FirebaseInterface firebaseInterface, Preferences preferences) {
        super(firebaseInterface, new Profile(null), "profile");
        this.preferences = preferences;
    }

    /**
     * Initialize profile collection: check preferences for local profile and load it or create new.
     *
     * @param listener for synchronization
     */
    public void init(final OnCollectionUpdatedListener listener) {
        if(!preferences.contains(VikingGame.PREFERENCES_PROFILE_KEY) ||
                preferences.getString(VikingGame.PREFERENCES_PROFILE_KEY) == null ||
                preferences.getString(VikingGame.PREFERENCES_PROFILE_KEY).isEmpty()) {
            System.out.println("NO LOCAL PROFILE FOUND");
            createDefaultProfile(listener);
            return;
        }
        this.localPlayerId = preferences.getString(VikingGame.PREFERENCES_PROFILE_KEY);
        this.readProfile(localPlayerId, new OnCollectionUpdatedListener() {
            @Override
            public void onSuccess(FirebaseDocument document) {
                System.out.println("PROFILE SUCCESSFULLY LOADED FROM DB");
                listener.onSuccess(document);
            }

            @Override
            public void onFailure() {
                System.out.println("PROFILE DOESN'T EXIST");
                listener.onFailure();
            }
        });
    }

    /**
     * Create default profile for new user.
     *
     * @param listener for synchronization
     */
    private void createDefaultProfile(OnCollectionUpdatedListener listener) {
        String[] names = {"James", "Robert", "John", "Mary", "Patricia", "Linda", "Fighter", "Olaf",
                "Jan", "Bjorn", "Knut", "Lars", "Kjell", "Hans", "Astrid", "Ingrid", "Kari", "Liv",
                "Anne", "Marit", "Solveig", "Ursula", "Hildegard", "Ilse", "Gerda", "Ingeborg",
                "Helga", "Gunther", "Carl", "Heinz", "Werner", "Wolfgang", "Ludwig", "Kurt", "Horst"};
        int nameIndex = (int) (Math.random() * names.length);
        createProfile(
                names[nameIndex],
                0,
                listener
        );
    }


    /**
     * Creates profile entry in database and returns unique profile-id
     *
     * @param name {String}
     * @param avatarId {int}
     */
    public void createProfile(final String name, final long avatarId, final OnCollectionUpdatedListener listener) {
        this.firebaseInterface.addDocumentWithGeneratedId(
                this.identifier,
                new HashMap<String, Object>(){{
                    put(Profile.KEY_NAME, name);
                    put(Profile.KEY_AVATAR_ID, avatarId);
                    put(Profile.KEY_GAMES_WON, 0);
                    put(Profile.KEY_GAMES_LOST, 0);
                    put(Profile.KEY_HIGHSCORE, 0);
                }},
                new OnPostDataListener() {
                    @Override
                    public void onSuccess(String documentId) {
                        System.out.println("ProfileCollection: Host is: " + documentId);

                        Profile profile = new Profile(documentId);
                        add(documentId, profile);

                        localPlayerId = documentId;
                        preferences.putString(VikingGame.PREFERENCES_PROFILE_KEY, documentId);
                        preferences.putBoolean(VikingGame.PREFERENCES_SOUND_KEY, true);
                        preferences.flush();

                        readProfile(documentId, listener);
                    }
                    @Override
                    public void onFailure() {
                        System.out.println("ProfileCollection: Saving profile failed.");
                        listener.onFailure();
                    }
                });
    }

    public void updateLocalProfile(final String name,
                                   final long avatarId,
                                   final OnCollectionUpdatedListener listener)
    {
        final Profile profile = getLocalPlayerProfile();

        this.firebaseInterface.addOrUpdateDocument(
                this.identifier,
                this.localPlayerId,
                new HashMap<String, Object>(){{
                    put(Profile.KEY_NAME, name);
                    put(Profile.KEY_AVATAR_ID, avatarId);
                    put(Profile.KEY_GAMES_WON, profile.getWonGames());
                    put(Profile.KEY_GAMES_LOST, profile.getLostGames());
                    put(Profile.KEY_HIGHSCORE, profile.getHighscore());
                }},
                new OnPostDataListener() {
                    @Override
                    public void onSuccess(String documentId) {
                        System.out.println("ProfileCollection: Host is: " + documentId);
                        readProfile(documentId, listener);
                    }
                    @Override
                    public void onFailure() {
                        System.out.println("ProfileCollection: Saving profile failed.");
                        listener.onFailure();
                    }
                });
    }


    /**
     * Increases the win or lost_game-field in the database by one.
     *
     * @param profile {Profile}
     * @param win {boolean}                 false if lost game
     */
    public void addFinishedGame(Profile profile,
                                boolean win,
                                long score,
                                final OnCollectionUpdatedListener listener)
    {
        profile.setIsLoaded(false);
        profile.addFinishedGame(win, score);

        Map<String, Object> profileValues = new HashMap<>();
        profileValues.put(Profile.KEY_GAMES_WON, profile.getWonGames());
        profileValues.put(Profile.KEY_GAMES_LOST, profile.getLostGames());
        profileValues.put(Profile.KEY_HIGHSCORE, profile.getHighscore());

        this.firebaseInterface.addOrUpdateDocument(this.identifier, profile.getId(), profileValues, new OnPostDataListener() {
            @Override
            public void onSuccess(String documentId) {
                readProfile(documentId, listener);
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Read profile values from Database.
     *
     * @param profileId {String}
     */
    public void readProfile(String profileId, final OnCollectionUpdatedListener listener) {
        // add profile with unloaded status if profile is not existing yet
        if(isKeyNotExistingLocally(profileId)) {
            this.add(profileId, new Profile(profileId));
        } else {
            this.get(profileId).setIsLoaded(false);
        }

        this.firebaseInterface.get(
                this.identifier,
                profileId,
                new OnGetDataListener() {
                    @Override
                    public void onGetData(String documentId, Map<String, Object> data) {
                        if (data == null) {
                            listener.onFailure();
                            return;
                        }
                        Profile profile = (Profile) get(documentId);
                        for(Map.Entry<String, Object> e : data.entrySet()) {
                            try {
                                profile.set(e.getKey(), e.getValue());
                            } catch (FieldKeyUnknownException exception) {
                                exception.printStackTrace();
                            }
                        }
                        profile.setIsLoaded(true);
                        listener.onSuccess(profile);
                    }

                    @Override
                    public void onFailure() {
                        System.out.println("ProfileCollection: Loading profile failed.");
                        listener.onFailure();
                    }
                }
        );
    }

    /**
     * Load the top leaderboard profiles.
     *
     * @param topPlaces {int} top X places
     * @param listener for synchronization
     */
    public void loadLeaderboard(int topPlaces, final OnCollectionUpdatedListener listener) {
        final int[] counter = {topPlaces};
        leaderboard = new ArrayList<>();
        firebaseInterface.getAll(identifier, Profile.KEY_HIGHSCORE, topPlaces, new OnGetDataListener() {
            @Override
            public void onGetData(String documentId, Map<String, Object> data) {
                Profile profile = new Profile(documentId);
                leaderboard.add(documentId);
                add(documentId, profile);
                for(Map.Entry<String, Object> e : data.entrySet()) {
                    try {
                        profile.set(e.getKey(), e.getValue());
                    } catch (FieldKeyUnknownException exception) {
                        exception.printStackTrace();
                    }
                }
                profile.setIsLoaded(true);
                counter[0]--;
                if(counter[0] <= 0) {
                    listener.onSuccess(getLocalPlayerProfile());
                }
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Return the leaderboard profiles.
     *
     * @return ArrayList of profiles. Empty if not loaded.
     */
    public ArrayList<Profile> getLeaderboard() {
        ArrayList<Profile> leaderboardProfiles = new ArrayList<>();
        if (leaderboard == null) return leaderboardProfiles;
        for(String profileId : leaderboard) {
            leaderboardProfiles.add((Profile) get(profileId));
        }
        return leaderboardProfiles;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public Profile getProfileById(String id) {
        if (isKeyNotExistingLocally(id)) {
            return null;
        }
        return (Profile) get(id);
    }

    public Profile getHostProfile() {
        return getProfileById(hostId);
    }

    public Profile getGuestProfile() {
        return getProfileById(guestId);
    }

    public Profile getLocalPlayerProfile() {return getProfileById(localPlayerId);}

    public Preferences getPreferences() {
        return preferences;
    }
}
