package com.spacecruiser.game.view.GUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.spacecruiser.game.SpaceCruiser;
import com.spacecruiser.game.model.GameModel;

/**
 * Represents the in game HUD holding game related information
 */

public class GameHUD {

    private final static float HUD_VIEWPORT_WIDTH = 100;
    private final static float HUD_VIEWPORT_HEIGHT = 100;

    /**
     * The current game this HUD belongs to.
     */
    private SpaceCruiser game;

    /**
     * The stage in which the camera and the table with the elements of the HUD are added.
     */
    private Stage stage;
    /**
     * The table in which the elements of the HUD are added.
     */
    private Table table;
    /**
     * The labels of the increasing score and the title.
     */
    private Label scoreLbl, scoreInfo;

    /**
     * The score value to be put on the scoreInfo label.
     */
    private int score;

    /**
     * The back button.
     */
    private ImageButton backBtn;

    /**
     * Constructs an in-game HUD with the current score of the player and a back button.
     * @param game The current game.
     */
    public GameHUD(SpaceCruiser game){
        score = 0;
        scoreLbl = new Label("SCORE",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreInfo = new Label(String.format("%04d",score),new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        this.game = game;
        stage = new Stage(new FitViewport(HUD_VIEWPORT_WIDTH, HUD_VIEWPORT_HEIGHT, new OrthographicCamera()));
        table = new Table();
        table.top().setFillParent(true);



        table.add(scoreLbl).expandX();
        table.add(scoreInfo).expandX();

        createBackBtn();
        table.add(backBtn).size(10,10);
        stage.addActor(table);
    }

    /**
     * Creates the back button with the respective image to be shown on the screen and
     * sets its action when clicked.
     */
    private void createBackBtn(){
        backBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion((Texture)game.getAssetManager().get("images/BlackButton-Active.png"))),
                new TextureRegionDrawable(new TextureRegion((Texture)game.getAssetManager().get("images/BlackButton-Hover.png"))));

        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                game.getScreenManager().drawScreen(ScreenManager.ActiveScreen.MENU);
            }
        });
    }

    /**
     * Updates the score in the label.
     * @param score The new value to be put on the label.
     */
    public void update(float score){
        this.score = (int)score;

        scoreInfo.setText(String.format("%04d",this.score));

        stage.act();
        stage.draw();
    }

    /**
     * Returns the stage of the elements.
     * @return  The stage.
     */
    public Stage getStage(){return this.stage;}

}
