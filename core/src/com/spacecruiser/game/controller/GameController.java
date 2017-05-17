package com.spacecruiser.game.controller;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.spacecruiser.game.controller.entities.BigAsteroidBody;
import com.spacecruiser.game.controller.entities.MediumAsteroidBody;
import com.spacecruiser.game.controller.entities.PointsBody;
import com.spacecruiser.game.controller.entities.ShieldBody;
import com.spacecruiser.game.controller.entities.ShipBody;
import com.spacecruiser.game.model.GameModel;
import com.spacecruiser.game.model.entities.AsteroidModel;
import com.spacecruiser.game.model.entities.BonusModel;
import com.spacecruiser.game.model.entities.EntityModel;
import com.spacecruiser.game.model.entities.ShipModel;


import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Controls the physics aspect of the game.
 */

public class GameController {
    /**
     * The arena width in meters.
     */
    public static final int ARENA_WIDTH = 100;

    /**
     * The arena height in meters.
     */
    public static final int ARENA_HEIGHT = 50;

    /**
     * The rotation speed in radians per second.
     */
    private static final float ROTATION_SPEED = 10f;

    /**
     * The acceleration impulse in newtons.
     */
    private static final float ACCELERATION_FORCE = 30f;

    /**
     * The physics world controlled by this controller.
     */
    private final World world;

    /**
     * The spaceship body.
     */
    private final ShipBody shipBody;

    /**
     * Accumulator used to calculate the simulation step.
     */
    private float accumulator;

    /**
     * Creates a new GameController that controls the physics of a certain GameModel.
     *
     * @param model The model controlled by this controller.
     */
    public GameController(GameModel model) {
        world = new World(new Vector2(0, 0), true);

        shipBody = new ShipBody(world, model.getShip());

        List<AsteroidModel> asteroids = model.getAsteroids();
        for (AsteroidModel asteroid : asteroids)
        if (asteroid.getSize() == AsteroidModel.AsteroidSize.BIG)
            new BigAsteroidBody(world, asteroid);
        else if (asteroid.getSize() == AsteroidModel.AsteroidSize.MEDIUM)
            new MediumAsteroidBody(world, asteroid);


        List<BonusModel> bonus = model.getBonus();
        for (BonusModel b : bonus) {
            if (b.getType() == BonusModel.BonusType.SHIELD)
                new ShieldBody(world, b);

            if (b.getType() == BonusModel.BonusType.POINTS)
                new PointsBody(world, b);
        }

    }

    /**
     * Calculates the next physics step of duration delta (in seconds).
     *
     * @param delta The size of this physics step in seconds.
     */
    public void update(float delta) {
        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 6, 2);
            accumulator -= 1/60f;
        }

        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for (Body body : bodies) {
            verifyBounds(body);
            ((EntityModel) body.getUserData()).setPosition(body.getPosition().x, body.getPosition().y);
            ((EntityModel) body.getUserData()).setRotation(body.getAngle());
        }
        this.accelerate(delta);

    }

    /**
     * Verifies if the body is inside the arena bounds and if not
     * wraps it around to the other side.
     *
     * @param body The body to be verified.
     */
    private void verifyBounds(Body body) {
        if (body.getPosition().x < 0)
            body.setTransform(ARENA_WIDTH, body.getPosition().y, body.getAngle());

        if (body.getPosition().y < 0)
            body.setTransform(body.getPosition().x, ARENA_HEIGHT, body.getAngle());

        if (body.getPosition().x > ARENA_WIDTH)
            body.setTransform(0, body.getPosition().y, body.getAngle());

        if (body.getPosition().y > ARENA_HEIGHT)
            body.setTransform(body.getPosition().x, 0, body.getAngle());
    }

    /**
     * Returns the world controlled by this controller. Needed for debugging purposes only.
     *
     * @return The world controlled by this controller.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Rotates the spaceship left. The rotation takes into consideration the
     * constant rotation speed and the delta for this simulation step.
     *
     * @param delta Duration of the rotation in seconds.
     */
    public void rotateLeft(float delta) {
        shipBody.setTransform(shipBody.getX(), shipBody.getY(), shipBody.getAngle() + ROTATION_SPEED * delta);
        shipBody.setAngularVelocity(0);
    }

    /**
     * Rotates the spaceship right. The rotation takes into consideration the
     * constant rotation speed and the delta for this simulation step.
     *
     * @param delta Duration of the rotation in seconds.
     */
    public void rotateRight(float delta) {
        shipBody.getX();

        shipBody.setTransform(shipBody.getX(), shipBody.getY(), shipBody.getAngle() - ROTATION_SPEED * delta);
        shipBody.setAngularVelocity(0);
    }

    /**
     * Accelerates the spaceship. The acceleration takes into consideration the
     * constant acceleration force and the delta for this simulation step.
     *
     * @param delta Duration of the rotation in seconds.
     */

    public void accelerate(float delta) {
        shipBody.applyForceToCenter(-(float) sin(shipBody.getAngle()) * ACCELERATION_FORCE * delta, (float) cos(shipBody.getAngle()) * ACCELERATION_FORCE * delta, true);
        ((ShipModel)shipBody.getUserData()).setAccelerating(true);
    }


    /**
     * Increase current score based on acceleration
     */
    public void increaseScore(float delta, GameModel model){
        model.setScore((int)(model.getScore() + delta * ACCELERATION_FORCE));
    }

}
