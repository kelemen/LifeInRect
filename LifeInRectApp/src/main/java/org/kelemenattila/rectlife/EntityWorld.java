package org.kelemenattila.rectlife;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jtrim.utils.ExceptionHelper;

/**
 *
 * @author Kelemen Attila
 */
public final class EntityWorld {
    private static final double DEFAULT_MUTATE_RATE = 0.001;
    private static final int NEURON_COUNT = 10;
    private static final double DEFENDER_CHANCE_MULTIPLIER = 0.5;
    private static final Random RND = new Random();

    private final int width;
    private final int height;
    private final Entity<EntityAction>[] board;
    private final BoardPos[][] fighters;
    private volatile DnsCombiner geneCombiner;
    private volatile double accidentRate;

    public EntityWorld(int width, int height) {
        ExceptionHelper.checkArgumentInRange(width, 1, Integer.MAX_VALUE, "width");
        ExceptionHelper.checkArgumentInRange(height, 1, Integer.MAX_VALUE, "height");

        this.width = width;
        this.height = height;
        this.board = createBoard(width, height);
        this.fighters = new BoardPos[width * height][8];
        this.geneCombiner = new StandardGeneticCombiner(DEFAULT_MUTATE_RATE);
        this.accidentRate = 0.001;

        fillBoard();
    }

    public void setAccidentRate(double accidentRate) {
        this.accidentRate = accidentRate;
    }

    private void setEntity(int x, int y, Entity<EntityAction> entity) {
        board[y * width + x] = entity;
    }

    private Entity<EntityAction> getEntity(int x, int y) {
        int actualY = (y + height) % height;
        int actualX = (x + width) % width;
        return board[actualY * width + actualX];
    }

    private void fillBoard() {
        EntityAction[] actions = EntityAction.values();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setEntity(x, y, new Entity<>(8, NEURON_COUNT, actions));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Entity<EntityAction>[] createBoard(int width, int height) {
        return (Entity<EntityAction>[])new Entity<?>[width * height];
    }

    public void setMutateRate(double newRate) {
        geneCombiner = new StandardGeneticCombiner(newRate);
    }

    private void getNeighbourAppearances(int x, int y, double baseAppearance, double[] neighbours) {
        int index = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    Entity<EntityAction> neighbour = getEntity(x + dx, y + dy);

                    neighbours[index] = neighbour != null
                            ? neighbour.getAppearance() - baseAppearance
                            : 0.0;
                }
            }
        }
    }

    private void putFighter(int fighterX, int fighterY, EntityAction.AttackPosition attackPos) {
        int destX = fighterX + attackPos.getDx();
        int destY = fighterY + attackPos.getDy();
        if (destX < 0 || destY < 0 || destX >= width || destY >= height) {
            return;
        }

        BoardPos fighterPos = new BoardPos(fighterX, fighterY);
        BoardPos[] posContainer = fighters[destY * width + destX];
        for (int i = 0; i < posContainer.length; i++) {
            if (posContainer[i] == null) {
                posContainer[i] = fighterPos;
                break;
            }
        }
    }

    private void chooseActions() {
        double[] neighbours = new double[8];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity<EntityAction> entity = getEntity(x, y);
                if (entity != null) {
                    getNeighbourAppearances(x, y, entity.getAppearance(), neighbours);
                    EntityAction action = entity.think(neighbours);
                    EntityAction.AttackPosition attackPos = action.getAction();
                    if (attackPos != null) {
                        putFighter(x, y, attackPos);
                    }
                }
            }
        }
    }

    private void resolveFight() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BoardPos[] positions = fighters[y * width + x];
                int count = 0;
                for (BoardPos pos: positions) {
                    if (pos == null) {
                        break;
                    }
                    count++;
                }

                if (count > 0) {
                    double defenderChance = DEFENDER_CHANCE_MULTIPLIER / (double)(count + 1);
                    if (Math.random() < defenderChance) {
                        setEntity(0, 0, null);
                    }
                    else {
                        BoardPos toKill = positions[RND.nextInt(count)];
                        setEntity(toKill.x, toKill.y, null);
                    }
                }
            }
        }
        for (int i = 0; i < fighters.length; i++) {
            BoardPos[] positions = fighters[i];
            int count = 0;
            for (BoardPos pos: positions) {
                if (pos == null) {
                    break;
                }
                count++;
            }

            if (count > 0) {
                BoardPos toKill = positions[RND.nextInt(count)];
                setEntity(toKill.x, toKill.y, null);
            }
        }
    }

    private void getNeighbours(int x, int y, List<BoardPos> neighbours) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    int entityX = x + dx;
                    int entityY = y + dy;
                    Entity<EntityAction> entity = getEntity(entityX, entityY);
                    if (entity != null && entity.getAge() > 0) {
                        neighbours.add(new BoardPos(entityX, entityY));
                    }
                }
            }
        }
    }

    private boolean breedPopulationSingleStep() {
        List<BoardPos> neighbours = new ArrayList<>();
        List<BoardPos> otherNeighbours = new ArrayList<>();

        DnsCombiner currentCombiner = geneCombiner;
        boolean didSomething = false;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity<EntityAction> entity = getEntity(x, y);
                if (entity != null) {
                    continue;
                }

                neighbours.clear();
                getNeighbours(x, y, neighbours);

                while (!neighbours.isEmpty()) {
                    int choseIndex1 = RND.nextInt(neighbours.size());
                    BoardPos entity1Pos = neighbours.get(choseIndex1);

                    otherNeighbours.clear();
                    getNeighbours(entity1Pos.x, entity1Pos.y, otherNeighbours);

                    if (!otherNeighbours.isEmpty()) {
                        didSomething = true;
                        int choseIndex2 = RND.nextInt(otherNeighbours.size());
                        BoardPos entity2Pos = otherNeighbours.get(choseIndex2);

                        Entity<EntityAction> entity1 = getEntity(entity1Pos.x, entity1Pos.y);
                        Entity<EntityAction> entity2 = getEntity(entity2Pos.x, entity2Pos.y);
                        Entity<EntityAction> newEntity = entity1.breed(entity2, currentCombiner);
                        setEntity(x, y, newEntity);
                        break;
                    }
                    else {
                        neighbours.remove(choseIndex1);
                    }
                }
            }
        }
        return didSomething;
    }

    private void breedPopulation() {
        while (breedPopulationSingleStep()) {
            // Do nothing but loop until there is nothing to change.
        }
    }

    private void resolveAccidents() {
        double currentAccidentRate = accidentRate;
        for (int i = 0; i < board.length; i++) {
            if (board[i] != null && Math.random() < currentAccidentRate) {
                board[i] = null;
            }
        }
    }

    public void stepWorld() {
        for (int i = 0; i < fighters.length; i++) {
            BoardPos[] positions = fighters[i];
            for (int j = 0; j < positions.length; j++) {
                positions[j] = null;
            }
        }

        chooseActions();
        resolveFight();
        resolveAccidents();
        breedPopulation();
    }

    public WorldView createAppearanceView() {
        // Use TYPE_INT_ARGB instead of TYPE_BYTE_GRAY because Java sometimes
        // tries to be too clever when displaying grayscale images not using
        // the comlete gray scale ([0, 255] in this case).
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        DataBufferInt dataBuffer = (DataBufferInt)image.getRaster().getDataBuffer();
        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        int[] pixels = dataBuffer.getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity<EntityAction> entity = getEntity(x, y);
                int color;

                if (entity != null) {
                    int grayLevel = (int)(entity.getAppearance() * 256.0);
                    grayLevel = Math.max(0, Math.min(grayLevel, 0xFF));
                    color = grayLevel | (grayLevel << 8) | (grayLevel << 16) | 0xFF00_0000;
                }
                else {
                    color = 0xFFFF0000; // red
                }
                pixels[width * y + x] = color;
            }
        }

        return new WorldView("Appearance", image);
    }

    public WorldView createAgeView() {
        // Use TYPE_INT_ARGB instead of TYPE_BYTE_GRAY because Java sometimes
        // tries to be too clever when displaying grayscale images not using
        // the comlete gray scale ([0, 255] in this case).
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        DataBufferInt dataBuffer = (DataBufferInt)image.getRaster().getDataBuffer();
        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        int[] pixels = dataBuffer.getData();

        long minAge = Long.MAX_VALUE;
        long maxAge = Long.MIN_VALUE;
        double avgAge = 0.0;
        int count = 0;
        for (Entity<?> entity: board) {
            if (entity != null) {
                long age = entity.getAge();
                if (age < minAge) minAge = age;
                if (age > maxAge) maxAge = age;

                count++;
                double w0 = (double)(count - 1) / (double)count;
                double w1 = 1.0 / (double)count;
                avgAge = w0 * avgAge + w1 * (double)age;
            }
        }

        double ageRadius = Math.min(maxAge - avgAge, avgAge - minAge);
        double lowAge = avgAge - ageRadius;
        double highAge = avgAge + ageRadius;
        double ageScale = 255.0 / (highAge - lowAge);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity<EntityAction> entity = getEntity(x, y);
                int color;

                if (entity != null) {
                    double age = entity.getAge();
                    double unboundedGrayLevel = ageScale * (age - lowAge);
                    int grayLevel = (int)Math.round(Math.max(0.0, Math.min(unboundedGrayLevel, 255.0)));
                    color = grayLevel | (grayLevel << 8) | (grayLevel << 16) | 0xFF00_0000;
                }
                else {
                    color = 0xFFFF0000; // red
                }
                pixels[width * y + x] = color;
            }
        }

        return new WorldView("Age", image);
    }

    public WorldView[] viewWorld() {
        return new WorldView[]{
            createAppearanceView(),
            createAgeView()
        };
    }

    public static final class WorldView {
        private final String caption;
        private final BufferedImage image;

        public WorldView(String caption, BufferedImage image) {
            ExceptionHelper.checkNotNullArgument(caption, "caption");
            ExceptionHelper.checkNotNullArgument(image, "image");

            this.caption = caption;
            this.image = image;
        }

        public String getCaption() {
            return caption;
        }

        public BufferedImage getImage() {
            return image;
        }
    }

    private static final class BoardPos {
        public final int x;
        public final int y;

        public BoardPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
