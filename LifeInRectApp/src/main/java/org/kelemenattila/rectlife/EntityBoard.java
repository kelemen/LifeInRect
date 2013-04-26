package org.kelemenattila.rectlife;

import org.jtrim.utils.ExceptionHelper;

/**
 *
 * @author Kelemen Attila
 */
public final class EntityBoard<Entity> {
    private final int width;
    private final int height;
    private final Entity[] entities;

    @SuppressWarnings("unchecked")
    public EntityBoard(int width, int height) {
        this.entities = (Entity[])new Object[width * height];
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setEntity(int x, int y, Entity entity) {
        ExceptionHelper.checkArgumentInRange(x, 0, width - 1, "x");
        ExceptionHelper.checkArgumentInRange(y, 0, height - 1, "y");
        setEntityUnsafe(x, y, entity);
    }

    public Entity getEntity(int x, int y) {
        ExceptionHelper.checkArgumentInRange(x, 0, width - 1, "x");
        ExceptionHelper.checkArgumentInRange(y, 0, height - 1, "y");
        return getEntityUnsafe(x, y);
    }

    public void setEntityUnsafe(int x, int y, Entity entity) {
        entities[y * width + x] = entity;
    }

    public Entity getEntityUnsafe(int x, int y) {
        return entities[y * width + x];
    }
}
